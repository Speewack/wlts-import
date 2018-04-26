/**
 *
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.io.FileReader;
import java.io.FileNotFoundException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.beans.DetailedMember;
import org.randywebb.wlts.beans.Address;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.beans.Companionship;
import org.randywebb.wlts.beans.Visit;
import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.ldstools.json.DetailedMemberConsumer;
import org.randywebb.wlts.ldstools.json.DistrictConsumer;
import org.randywebb.wlts.ldstools.json.HouseholdConsumer;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.util.CSVWriter;
import org.randywebb.wlts.util.KMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Main {

  private static Logger log = LoggerFactory.getLogger(Main.class);

  private static String[] parseArgs(String[] args, String[] onOff, String[] hasValue, Map<String,String> results) {
  	String	next = null;
  	List<String> ordinal = new ArrayList<String>();
	List<String> onOffList = (null == onOff) ? null : Arrays.asList(onOff);
	List<String> hasValueList = (null == hasValue) ? null : Arrays.asList(hasValue);

  	for (String arg : args) {
  		if (null != next) {
  			results.put(next, arg);
  			next = null;
  		} else if ( (null != onOff) && onOffList.contains(arg)) {
  			results.put(arg, "true");
  		} else if( (null != hasValue) && hasValueList.contains(arg) ) {
  			next = arg;
  		} else {
  			ordinal.add(arg);
  		}
  	}

  	if (null != next) {
  		System.out.println("Passed " + next + " but need a value after it");
  		System.exit(1);
  	}

	return ordinal.toArray(new String[0]);
  }

  private static JSONObject loadJSONFile(String path) throws IOException, FileNotFoundException, ParseException {
  	return (JSONObject) (new JSONParser()).parse(new FileReader(path));
  }

  private static Address relocate(Household household, JSONObject relocation) {
  	JSONObject relocatable = (null == relocation) ? null : (JSONObject) relocation.get(household.getCoupleName());
	Address previous = household.getHouseholdAddress();

  	if (null != relocatable) {
		Address address = new Address();

		address.setLattitude( (null == relocatable.get("latitude")) ? previous.getLattitude() : relocatable.get("latitude").toString());
		address.setLongitude( (null == relocatable.get("longitude")) ? previous.getLongitude() : relocatable.get("longitude").toString());
		address.setPostalCode( (null == relocatable.get("postalCode")) ? previous.getPostalCode() : relocatable.get("postalCode").toString());
		address.setState( (null == relocatable.get("state")) ? previous.getState() : relocatable.get("state").toString());
		if (null == relocatable.get("address")) {
			address.setStreetAddress( (null == relocatable.get("desc1") || null == relocatable.get("desc2"))
									? previous.getLattitude()
									: relocatable.get("desc1").toString() + ", " + relocatable.get("desc2").toString());
		} else {
			address.setStreetAddress(relocatable.get("address").toString());
		}

		return address;

  	}

	return previous;

  }

  /**
   * @param args
   * @throws Exception
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static void main(String... args) throws Exception {
  	Map<String,String> switches = new HashMap<String,String>();
  	String[] outputFileTypes = {"--map", "--routes", "--ministers", "--relocate"};
	String[] arguments = parseArgs(args, null, outputFileTypes, switches);
	JSONObject relocations = switches.containsKey("--relocate") ? loadJSONFile(switches.get("--relocate")) : null;

    // read input parameters
    if ( (arguments.length < 1) || (arguments.length > 3) ) {
      System.out.println("Usage Main [--map /path/to/file.kml] [--ministers /path/to/file.kml] [--routes /path/to/file.kml] username [password] [target_file]");
      System.out.println("\tif [password] is not passed, it will be requested");
      System.out.println("\tif [target_file] is only passed when --map, --ministers and --routes is not passed");
      log.trace("arguments.length = " + arguments.length);
      for (String argument : arguments) {
      	log.trace("\t" + argument);
      }
      System.exit(1);
    }

	final boolean ministers = switches.containsKey("--ministers");
	final boolean routes = switches.containsKey("--routes");
	final boolean map = switches.containsKey("--map");
	final boolean verb = ministers || map || routes;
	final boolean hasPassword = (verb && arguments.length == 2) || (!verb && arguments.length == 3);
	final int usernameIndex = 0;
	final int targetIndex = 1 + (hasPassword ? 1 : 0);
	final int passwordIndex = 1;

	if ( !verb && (arguments.length < 2) ) {
      System.out.println("Usage Main [--map /path/to/file.kml] [--ministers /path/to/file.kml] [--routes /path/to/file.kml] username [password] target_file");
      System.out.println("\tif [password] is not passed, it will be requested");
      System.exit(1);
	}

	// determine username and password
	String password = hasPassword ? arguments[passwordIndex] : null;
	String username = arguments[usernameIndex];

	if (!hasPassword) {
		System.out.print("Enter password [" + username + "]: ");
		System.out.flush();
		password = (new Scanner(System.in)).nextLine();
	}

    // Initialize LDSTools Client
    LdsToolsClient client = null;

    try {
		client = new LdsToolsClient(username, password);
    } catch(AuthenticationException exception) {
    	System.out.println("Authentication error: " + exception.getMessage());
    	System.exit(1);
    }

    // Capture file path from args

	if (!verb && isUserAdmin(client.getEndpointInfo("current-user-detail"))) {
		String filePath = arguments[targetIndex];

		generateWLTSReport(client, filePath);

	} else if (verb) {

		if (map) {
			generateMapReport(client, relocations, switches.get("--map"));
		}

		if (ministers) {
			generateMinistersReport(client, relocations, false, switches.get("--ministers"));
		}

		if (routes) {
			generateMinistersReport(client, relocations, true, switches.get("--routes"));
		}

	} else {
		System.out.println("You do not have permissions to export all data. You can pass '--map', '--routes' or '--ministers' followed by an output file to export what you can");
	}

  }

  private static void addAuxiliaries(JSONObject person, List<String> htIds, List<String> vtIds) {
	JSONObject teachers = (JSONObject) person.get("teacherAuxIds");
	JSONArray ht = (JSONArray) teachers.get("htAuxiliaries");
	JSONArray vt = (JSONArray) teachers.get("vtAuxiliaries");

	if (null != ht) {
		for (Object ministerObject : ht) {
			String value = Long.toString((Long) ministerObject);

			if (!htIds.contains(value)) {
				htIds.add(value);
			}
		}
	}

	if (null != vt) {
		for (Object ministerObject : vt) {
			String value = Long.toString((Long) ministerObject);

			if (!vtIds.contains(value)) {
				vtIds.add(value);
			}
		}
	}

  }

  private static void getAuxiliaries(LdsToolsClient client, List<String> htIds, List<String> vtIds) throws IOException, ParseException {
	JSONObject members = client.getAppPropertyEndpointInfo("ministering-members-endpoint");
	JSONArray families = (JSONArray) members.get("families");

	for (Object familyObject : families) {
		JSONObject family = (JSONObject) familyObject;
		JSONObject spouse = (JSONObject) family.get("spouse");
		JSONArray children = (JSONArray) family.get("children");

		addAuxiliaries((JSONObject) family.get("headOfHouse"), htIds, vtIds);

		if (null != spouse) {
			addAuxiliaries(spouse, htIds, vtIds);
		}

		if (null != children) {
			for (Object childObject : children) {
				addAuxiliaries((JSONObject) childObject, htIds, vtIds);
			}
		}

	}
  }

  private static void mapCompanionships(LdsToolsClient client, JSONObject relocations, boolean routes,
  											String auxiliaryId, String auxiliaryName,
  											String companionshipName,
  											String ministryPrefix,
  											String ministerName,
  											String ministeredName,
  										Map<String,Household> map, KMLWriter.List container) throws IOException, ParseException {
	JSONArray districtsJSON = client.getAppPropertyEndpointList("ministering-companionships-endpoint", auxiliaryId);
	List<District> districts = new ArrayList<District>();
	DistrictConsumer action = new DistrictConsumer(districts);
	KMLWriter.Folder folder = new KMLWriter.Folder()
				.append(new KMLWriter.Name(auxiliaryName))
				.append(new KMLWriter.Description("Map of ministering companionships for " + auxiliaryName));
	List<String> ministerIndividualIds = new ArrayList<String>();
	List<String> ministeredIndividualIds = new ArrayList<String>();

	if (districtsJSON.size() == 0) {
		return;
	}

	districtsJSON.forEach(action);

	for (District district : districts) {
		KMLWriter.Folder districtFolder = new KMLWriter.Folder()
					.append(new KMLWriter.Name(district.getName()))
					.append(new KMLWriter.Description("Map of ministering companionships for " + district.getName()));
		int ministryIndex = 0;

		for (Companionship companionship : district.getCompanionships()) {
			String name = "", prefix;
			double startLon = 0.0, startLat = 0.0;
			boolean haveStart = false;
			KMLWriter.Placemark connected = new KMLWriter.Placemark();
			KMLWriter.Line connection = new KMLWriter.Line();
			boolean distinctCompanionLocations = false;

			prefix = "";
			for (Teacher teacher : companionship.getTeachers()) {
				String	individualId = teacher.getIndividualId();
				Household household = map.get(individualId);

				if (!ministerIndividualIds.contains(individualId) && (companionship.getAssignments().size() > 0) ) {
					ministerIndividualIds.add(individualId);
				}

				if (null != household) {
					name = name + prefix + household.getMember(individualId).getPreferredName();
					prefix = " - ";

					if (null != relocate(household, relocations).getLattitude() && null != relocate(household, relocations).getLongitude()) {
						double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
						double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

						connection.add(lat, lon, 0.0);
						if (!haveStart) {
							haveStart = true;
							startLon = lon;
							startLat = lat;
						}
						distinctCompanionLocations = distinctCompanionLocations || (startLon != lon) || (startLat != lat);
					} else {
						System.out.println("WARNING: No location found for " + household.getCoupleName());
					}
				}
			}

			if (distinctCompanionLocations) {
				districtFolder
							.append(connected
										.append(new KMLWriter.Name(name))
										.append(new KMLWriter.Description("Ministering Companionship: " + name))
										.append(new KMLWriter.UseStyle(companionshipName))
										.append(connection));
			} else if (companionship.getTeachers().size() > 0) {
				districtFolder.append((new KMLWriter.Placemark())
								.append(new KMLWriter.Name(district.getName() + " - " + name))
								.append(new KMLWriter.Description(name))
								.append(new KMLWriter.UseStyle(ministerName))
								.append(new KMLWriter.Point(startLat, startLon, 0.0)));
			}

			for (Assignment assignment : companionship.getAssignments()) {
				String	familyId = assignment.getIndividualId();
				Household family = map.get(familyId);

				if (!ministeredIndividualIds.contains(familyId)) {
					ministeredIndividualIds.add(familyId);
				}

				if (null != relocate(family, relocations).getLattitude() && null != relocate(family, relocations).getLongitude()) {
					connected = new KMLWriter.Placemark();
					connection = new KMLWriter.Line();
					double lat_family = Double.parseDouble(relocate(family, relocations).getLattitude());
					double lon_family = Double.parseDouble(relocate(family, relocations).getLongitude());

					connection.add(lat_family, lon_family, 0.0);

					prefix = "";
					name = family.getMember(familyId).getPreferredName() + " ministered by ";
					for (Teacher teacher : companionship.getTeachers()) {
						String	individualId = teacher.getIndividualId();
						Household household = map.get(individualId);

						name += prefix + household.getMember(individualId).getPreferredName();
						prefix = ", ";
						if (null != relocate(household, relocations).getLattitude() && null != relocate(household, relocations).getLongitude()) {
							double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
							double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

							connection.add(lat, lon, 0.0);
						}

					}

					connection.add(lat_family, lon_family, 0.0);
					districtFolder.append(connected
											.append(new KMLWriter.Name(name))
											.append(new KMLWriter.Description("Family Ministered: " + family.getMember(familyId).getPreferredName()))
											.append(new KMLWriter.UseStyle(ministryPrefix + ministryIndex))
											.append(connection));

				} else {
					System.out.println("WARNING: No location found for " + family.getCoupleName());
				}

			}

			ministryIndex += 1;

		}

		if (routes) {
			folder.add(districtFolder);
		}

	}

	KMLWriter.Folder group = new KMLWriter.Folder()
									.append(new KMLWriter.Name("Ministers"))
									.append(new KMLWriter.Description("All persons assigned to minister that have assignments"));

	for (String individualId : ministerIndividualIds) {
		Household household = map.get(individualId);
		double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
		double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

		group.append((new KMLWriter.Placemark())
						.append(new KMLWriter.Name(household.getMember(individualId).getPreferredName()))
						.append(new KMLWriter.Description(household.getMember(individualId).getPreferredName()))
						.append(new KMLWriter.UseStyle(ministerName))
						.append(new KMLWriter.Point(lat, lon, 0.0)));
	}

	if (!routes) {
		folder.append(group);
	}

	group = new KMLWriter.Folder()
					.append(new KMLWriter.Name("Ministered"))
					.append(new KMLWriter.Description("All persons who are assigned ministers"));

	for (String individualId : ministeredIndividualIds) {
		Household household = map.get(individualId);
		double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
		double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

		group.append((new KMLWriter.Placemark())
						.append(new KMLWriter.Name(household.getMember(individualId).getPreferredName()))
						.append(new KMLWriter.Description(household.getMember(individualId).getPreferredName()))
						.append(new KMLWriter.UseStyle(ministeredName))
						.append(new KMLWriter.Point(lat, lon, 0.0)));
	}

	if (!routes) {
		folder.append(group);
	}

	container.add(folder);

  }

  private static void generateMinistersReport(LdsToolsClient client, JSONObject relocations, boolean routes, String filePath) throws IOException, ParseException {
  	KMLWriter.Document document = new KMLWriter.Document();
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	JSONArray households = (JSONArray) ward.get("households");
	double minLat=0.0, maxLat=0.0, minLon=0.0, maxLon=0.0;
    List<Household> household_list = new LinkedList<Household>();
	Map<String, Household> idToHousehold = client.leaderReportsAvailable() ? new HashMap<String,Household>() : null;
    HouseholdConsumer action = new HouseholdConsumer(household_list, idToHousehold);

    households.forEach(action);

	String[] colors = {
		"7fff0000", "7f0000ff", "7fffff00", "7f00ffff", "7fff00ff", "7f7f7f7f", "7f00ff00",
		"7fff7f7f", "7f7f7fff", "7fffff7f", "7f7fffff", "7fff7fff", "7f7f7f7f", "7f7fff7f"
	};

  	document.append(new KMLWriter.Name((String) ward.get("orgName")))
  			.append(new KMLWriter.Open())
  			.append(new KMLWriter.Description("Map of the " + (String) ward.get("orgName"))) // put date in here
  			.append((new KMLWriter.Style("minister"))
  				.append(new KMLWriter.StyleIcon("http://maps.google.com/mapfiles/kml/shapes/capital_big_highlight.png")))
  			.append((new KMLWriter.Style("ministered"))
  				.append(new KMLWriter.StyleIcon("http://maps.google.com/mapfiles/kml/shapes/placemark_square.png")))
  			.append((new KMLWriter.Style("companionship"))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(8)).append(new KMLWriter.StyleColor("44000000"))));

	for (int colorIndex = 0; colorIndex < colors.length; ++colorIndex) {
		document
  			.append((new KMLWriter.Style("ministry"+colorIndex))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(2)).append(new KMLWriter.StyleColor(colors[colorIndex]))));
	}

	if (client.leaderReportsAvailable()) {
		List<String> priesthood = new ArrayList<String>();
		List<String> reliefsociety = new ArrayList<String>();

		getAuxiliaries(client, priesthood, reliefsociety);

		for (String aux : priesthood) {
			mapCompanionships(client, relocations, routes, aux, "Priesthood", "companionship", "ministry", "minister", "ministered", idToHousehold, document);
		}

		for (String aux : reliefsociety) {
			mapCompanionships(client, relocations, routes, aux, "Relief Society", "companionship", "ministry", "minister", "ministered", idToHousehold, document);
		}
	}

	KMLWriter.write(filePath, document);

  }

  /** Generate .kml file.
  	@param client lds tools client to use for connection
  	@param filePath the path to the .kml file to generate
  	Icons for maps can be found at: http://kml4earth.appspot.com/icons.html
  */
  private static void generateMapReport(LdsToolsClient client, JSONObject relocations, String filePath) throws IOException, ParseException {
  	KMLWriter.Document document = new KMLWriter.Document();
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	JSONArray households = (JSONArray) ward.get("households");
	double minLat=0.0, maxLat=0.0, minLon=0.0, maxLon=0.0;
    List<Household> household_list = new LinkedList<Household>();
	Map<String, Household> idToHousehold = client.leaderReportsAvailable() ? new HashMap<String,Household>() : null;
    HouseholdConsumer action = new HouseholdConsumer(household_list, idToHousehold);
  	KMLWriter.Folder folder = new KMLWriter.Folder();

	//System.out.println( households.toJSONString());
  	System.out.println("Unit: " + (String) ward.get("orgName"));
    households.forEach(action);

	folder.append(new KMLWriter.Name("Households"))
		.append(new KMLWriter.Description("Households in the " + (String) ward.get("orgName")));

	for (Household household : household_list) {
		if (null != relocate(household, relocations).getLattitude() && null != relocate(household, relocations).getLongitude()) {
			double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
			double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

			if (minLat == 0.0 || lat < minLat) {
				minLat = lat;
			}
			if (minLat == 0.0 || lat > maxLat) {
				maxLat = lat;
			}
			if (minLon == 0.0 || lon < minLon) {
				minLon = lon;
			}
			if (minLon == 0.0 || lon > maxLon) {
				maxLon = lon;
			}
			folder.append((new KMLWriter.Placemark())
							.append(new KMLWriter.Name(household.getHouseholdName()))
							.append(new KMLWriter.Description(household.getCoupleName()))
							.append(new KMLWriter.UseStyle("home"))
							.append(new KMLWriter.Point(lat, lon, 0.0)));

		} else {
			System.out.println("WARNING: We don't have lat/lon for " + household.getCoupleName());
			// TODO: figure out where these are
		}
	}

  	document.append(new KMLWriter.Name((String) ward.get("orgName")))
  			.append(new KMLWriter.Open())
  			.append(new KMLWriter.Description("Map of the " + (String) ward.get("orgName"))) // put date in here
  			.append((new KMLWriter.Style("home"))
  				.append(new KMLWriter.StyleIcon("http://maps.google.com/mapfiles/kml/shapes/placemark_circle_highlight.png")))
  			.append(folder);

	KMLWriter.write(filePath, document);

  }

  private static void generateWLTSReport(LdsToolsClient client, String filePath) throws IOException, ParseException {
	// Parse JSON Membership file into beans

	InputStream in = client.getMemberInfo();

	List<DetailedMember> members = processDetailMembers(in);

	// List<DetailedMember> members = processDetailMembers(Thread.currentThread().getContextClassLoader().getResourceAsStream("detailedmembership.json"));
	// List<Household> households = processHouseholds(Thread.currentThread().getContextClassLoader().getResourceAsStream("membership.json"));

	CSVWriter.writeCSVFile(filePath, members);

	System.out.println("Export complete");
  }

  private static boolean isUserAdmin(JSONObject response) throws IOException, ParseException {
  	JSONArray units = (JSONArray) response.get("units");
  	JSONObject firstUnit = (JSONObject) units.get(0);

  	return (Boolean) firstUnit.get("hasUnitAdminRights");
  }

  private static List<DetailedMember> processDetailMembers(InputStream in) throws IOException, ParseException {
    List<DetailedMember> members = new LinkedList<DetailedMember>();

    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new InputStreamReader(in));

    JSONArray membersJSON = (JSONArray) obj;

    DetailedMemberConsumer action = new DetailedMemberConsumer(members);

    membersJSON.forEach(action);

    return members;
  }

  private static List<Household> processHouseholds(InputStream in) throws IOException, ParseException {

    List<Household> households = new LinkedList<Household>();

    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new InputStreamReader(in));
    JSONObject jo = (JSONObject) obj;

    JSONArray householdsJSON = (JSONArray) jo.get("households");

    HouseholdConsumer action = new HouseholdConsumer(households);

    householdsJSON.forEach(action);

    return households;
  }
}
