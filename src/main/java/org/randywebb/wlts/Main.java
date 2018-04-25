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

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.beans.DetailedMember;
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

  /**
   * @param args
   * @throws Exception
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static void main(String[] args) throws Exception {

    // read input parameters
    if ( (args.length < 2) || (args.length > 4) ) {
      System.out.println("Usage Main [verb] username [password] target_file");
      System.out.println("\t[verb] may be --map or --ministers or empty");
      System.out.println("\tif [password] is not passed, it will be requested");
      System.exit(1);
    }

	final boolean ministers = args[0].equals("--ministers");
	final boolean map = args[0].equals("--map");
	final boolean verb = ministers || map;
	final boolean hasPassword = (verb && args.length == 4) || (!verb && args.length == 3);
	final int usernameIndex = verb ? 1 : 0;
	final int targetIndex = 1 + (hasPassword ? 1 : 0) + (verb ? 1 : 0);
	final int passwordIndex = hasPassword ? (verb ? 2 : 1) : 0;

	// determine username and password
	String password = hasPassword ? args[passwordIndex] : null;
	String username = args[usernameIndex];

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
	String filePath = args[targetIndex];

	if (!verb && isUserAdmin(client.getEndpointInfo("current-user-detail"))) {
		generateWLTSReport(client, filePath);
	} else if (map) {
		generateMapReport(client, filePath);
	} else if (ministers) {
		generateMinistersReport(client, filePath);
	} else {
		System.out.println("You do not have permissions to export all data. You can pass '--map' or '--ministers' as the first argument to export what you can");
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

  private static KMLWriter.Folder mapCompanionships(LdsToolsClient client, String auxiliaryId, String auxiliaryName, String companionshipName, String ministryName, Map<String,Household> map) throws IOException, ParseException {
	JSONArray districtsJSON = client.getAppPropertyEndpointList("ministering-companionships-endpoint", auxiliaryId);
	List<District> districts = new ArrayList<District>();
	DistrictConsumer action = new DistrictConsumer(districts);
	KMLWriter.Folder folder = new KMLWriter.Folder()
		.append(new KMLWriter.Name(auxiliaryName))
		.append(new KMLWriter.Description("Ministry map for " + auxiliaryName));

	if (districtsJSON.size() == 0) {
		return null;
	}

	districtsJSON.forEach(action);

	for (District district : districts) {
		KMLWriter.Folder districtFolder = new KMLWriter.Folder()
					.append(new KMLWriter.Name(district.getName()))
					.append(new KMLWriter.Description("Map of ministering companionships for " + district.getName()));

		for (Companionship companionship : district.getCompanionships()) {
			String name = "", prefix;
			double startLon = 0.0, startLat = 0.0;
			boolean haveStart = false;
			KMLWriter.Placemark connected = new KMLWriter.Placemark();
			KMLWriter.Line connection = new KMLWriter.Line();

			prefix = "";
			for (Teacher teacher : companionship.getTeachers()) {
				String	individualId = teacher.getIndividualId();
				Household household = map.get(individualId);

				if (null != household) {
					name = name + prefix + household.getMember(individualId).getPreferredName();
					prefix = " - ";

					if (null != household.getHouseholdAddress().getLattitude() && null != household.getHouseholdAddress().getLongitude()) {
						double lat = Double.parseDouble(household.getHouseholdAddress().getLattitude());
						double lon = Double.parseDouble(household.getHouseholdAddress().getLongitude());

						connection.add(lat, lon, 0.0);
						if (!haveStart) {
							haveStart = true;
							startLon = lon;
							startLat = lat;
						}
					}
				}
			}

			districtFolder.append(connected
									.append(new KMLWriter.Name(name))
									.append(new KMLWriter.Description("Ministering Companionship: " + name))
									.append(new KMLWriter.UseStyle(companionshipName))
									.append(connection));

			for (Assignment assignment : companionship.getAssignments()) {
				String	familyId = assignment.getIndividualId();
				Household family = map.get(familyId);
				connected = new KMLWriter.Placemark();
				connection = new KMLWriter.Line();

				prefix = "";
				name = "";
				for (Teacher teacher : companionship.getTeachers()) {
					String	individualId = teacher.getIndividualId();
					Household household = map.get(individualId);


				}

			}

		}

		folder.append(districtFolder);

	}

	return folder;
  }

  private static void generateMinistersReport(LdsToolsClient client, String filePath) throws IOException, ParseException {
  	KMLWriter.Document document = new KMLWriter.Document();
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	JSONArray households = (JSONArray) ward.get("households");
	double minLat=0.0, maxLat=0.0, minLon=0.0, maxLon=0.0;
    List<Household> household_list = new LinkedList<Household>();
	Map<String, Household> idToHousehold = client.leaderReportsAvailable() ? new HashMap<String,Household>() : null;
    HouseholdConsumer action = new HouseholdConsumer(household_list, idToHousehold);

	//System.out.println( households.toJSONString());
  	System.out.println("Unit: " + (String) ward.get("orgName"));
    households.forEach(action);

  	document.append(new KMLWriter.Name((String) ward.get("orgName")))
  			.append(new KMLWriter.Open())
  			.append(new KMLWriter.Description("Map of the " + (String) ward.get("orgName"))) // put date in here
  			.append((new KMLWriter.Style("companionship"))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(10)).append(new KMLWriter.StyleColor("87000000"))))
  			.append((new KMLWriter.Style("ministry"))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(4)).append(new KMLWriter.StyleColor("7f00ffff"))));;

	if (client.leaderReportsAvailable()) {
		List<String> priesthood = new ArrayList<String>();
		List<String> reliefsociety = new ArrayList<String>();
		getAuxiliaries(client, priesthood, reliefsociety);

		for (String aux : priesthood) {
			KMLWriter.Folder found = mapCompanionships(client, aux, "Priesthood", "companionship", "ministry", idToHousehold);

			if (null != found) {
				document.append(found);
			}
		}
		for (String aux : reliefsociety) {
			KMLWriter.Folder found = mapCompanionships(client, aux, "Relief Society", "companionship", "ministry", idToHousehold);

			if (null != found) {
				document.append(found);
			}
		}
	}

	KMLWriter.write(filePath, document);

  }

  /** Generate .kml file.
  	@param client lds tools client to use for connection
  	@param filePath the path to the .kml file to generate
  	Icons for maps can be found at: http://kml4earth.appspot.com/icons.html
  */
  private static void generateMapReport(LdsToolsClient client, String filePath) throws IOException, ParseException {
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
		if (null != household.getHouseholdAddress().getLattitude() && null != household.getHouseholdAddress().getLongitude()) {
			double lat = Double.parseDouble(household.getHouseholdAddress().getLattitude());
			double lon = Double.parseDouble(household.getHouseholdAddress().getLongitude());

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
  			.append((new KMLWriter.Style("companionship"))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(10)).append(new KMLWriter.StyleColor("87000000"))))
  			.append((new KMLWriter.Style("ministry"))
  				.append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(4)).append(new KMLWriter.StyleColor("7f00ffff"))))
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
