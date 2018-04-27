package org.randywebb.wlts.reports;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.randywebb.wlts.beans.DetailedMember;
import org.randywebb.wlts.beans.Address;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.beans.Companionship;
import org.randywebb.wlts.beans.Visit;
import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.util.KMLWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinisteringKML {

  private static Logger log = LoggerFactory.getLogger(MinisteringKML.class);

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

  private static String getVisitMessage(Assignment assignment) {
	List<Visit> visits = assignment.getVisits();
	String message = "";

	if (visits.size() > 0) {
		message = visits.get(0).getMonth() + "/" + visits.get(0).getYear() + " - ";
		message += visits.get(visits.size() - 1).getMonth() + "/" + visits.get(visits.size() - 1).getYear() + "\n";

		for (Visit visit : visits) {
			message += (null == visit.getVisited()) ? "?" : (visit.getVisited().substring(0,1).equalsIgnoreCase("t") ? "V" : ".");
		}

	}

	return message;
  }

  private static void mapCompanionships(LdsToolsClient client, JSONObject relocations, boolean routes,
  											String auxiliaryId, String auxiliaryName,
  											String companionshipName,
  											String ministryPrefix,
  											String ministerName,
  											String ministeredName,
  										Map<String,Household> map, KMLWriter.List container) throws IOException, ParseException {
	JSONArray districtsJSON = client.getAppPropertyEndpointList("ministering-companionships-endpoint", auxiliaryId);
	List<District> districts = District.fromArray(districtsJSON);
	KMLWriter.Folder folder = new KMLWriter.Folder()
				.append(new KMLWriter.Name(auxiliaryName))
				.append(new KMLWriter.Description("Map of ministering companionships for " + auxiliaryName));
	List<String> ministerIndividualIds = new ArrayList<String>();
	Map<String,Assignment> ministeredIndividualIds = new HashMap<String,Assignment>();

	if (districtsJSON.size() == 0) {
		return;
	}

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
								.append(new KMLWriter.Name(name))
								.append(new KMLWriter.Description(name))
								.append(new KMLWriter.UseStyle(ministerName))
								.append(new KMLWriter.Point(startLat, startLon, 0.0)));
			}

			for (Assignment assignment : companionship.getAssignments()) {
				String	familyId = assignment.getIndividualId();
				Household family = map.get(familyId);

				if (!ministeredIndividualIds.containsKey(familyId)) {
					ministeredIndividualIds.put(familyId, assignment);
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
											.append(new KMLWriter.Description("Family Ministered: " + family.getMember(familyId).getPreferredName() + "\n"
														+ getVisitMessage(assignment)))
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

	for (Map.Entry<String,Assignment> individualIdAssignment : ministeredIndividualIds.entrySet()) {
		Household household = map.get(individualIdAssignment.getKey());
		Assignment assignment = individualIdAssignment.getValue();
		double lat = Double.parseDouble(relocate(household, relocations).getLattitude());
		double lon = Double.parseDouble(relocate(household, relocations).getLongitude());

		group.append((new KMLWriter.Placemark())
						.append(new KMLWriter.Name(household.getMember(individualIdAssignment.getKey()).getPreferredName()))
						.append(new KMLWriter.Description(household.getMember(individualIdAssignment.getKey()).getPreferredName() + "\n"
									+ getVisitMessage(assignment)))
						.append(new KMLWriter.UseStyle(ministeredName))
						.append(new KMLWriter.Point(lat, lon, 0.0)));
	}

	if (!routes) {
		folder.append(group);
	}

	container.add(folder);

  }

  public static void generateMinistersReport(LdsToolsClient client, JSONObject relocations, boolean routes, String filePath) throws IOException, ParseException {
  	KMLWriter.Document document = new KMLWriter.Document();
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	JSONArray households = (JSONArray) ward.get("households");
	double minLat=0.0, maxLat=0.0, minLon=0.0, maxLon=0.0;
    List<Household> household_list = Household.fromArray(households);
	Map<String, Household> idToHousehold = client.leaderReportsAvailable() ? new HashMap<String,Household>() : null;

	if (null != idToHousehold) {
		for (Household household : household_list) {
			for (String individualId : household.getIndividualIds()) {
				idToHousehold.put(individualId, household);
			}
		}
	}

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
  public static void generateMapReport(LdsToolsClient client, JSONObject relocations, String filePath) throws IOException, ParseException {
  	KMLWriter.Document document = new KMLWriter.Document();
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	JSONArray households = (JSONArray) ward.get("households");
	double minLat=0.0, maxLat=0.0, minLon=0.0, maxLon=0.0;
    List<Household> household_list = Household.fromArray(households);
	Map<String, Household> idToHousehold = client.leaderReportsAvailable() ? new HashMap<String,Household>() : null;
  	KMLWriter.Folder folder = new KMLWriter.Folder();


	if (null != idToHousehold) {
		for (Household household : household_list) {
			for (String individualId : household.getIndividualIds()) {
				idToHousehold.put(individualId, household);
			}
		}
	}

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


}
