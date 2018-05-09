package org.randywebb.wlts.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.Companionship;
import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.beans.Visit;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.ldstools.rest.MinistryHelpers;
import org.randywebb.wlts.util.KMLWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Utility methods to generate KML files from ministering information. */
public final class MinisteringKML {

    /** default constructor. */
    private MinisteringKML() {
    }

    /** Can be used for logging debugging messages */
    //private static Logger log = LoggerFactory.getLogger(MinisteringKML.class);

    /** For an assignment, generates a historical view of the visits like:
                3/2017 - 2/2018 VVV?....?...VV?
        @param assignment The assignment to get the visit history for
        @return A string the shows the start month, end month
                    and visit history. V = visit, ? = not entered, . = Not Visited
    */
    private static String getVisitMessage(Assignment assignment) {
        List<Visit> visits = assignment.getVisits();
        String message = "";

        if (!visits.isEmpty()) {
            message = visits.get(0).getMonth() + "/" + visits.get(0).getYear() + " - ";
            message += visits.get(visits.size() - 1).getMonth();
            message += "/" + visits.get(visits.size() - 1).getYear() + "\n";

            for (Visit visit : visits) {
                message += null == visit.getVisited()
                            ? "?"
                            : visit.getVisited().substring(0, 1).equalsIgnoreCase("t") ? "V" : ".";
            }

        }

        return message;
    }

    /** Map ministering companionships.
        @param client The LDS tools logged in client
        @param relocations Map of coupleName to items to update in their address
        @param routes If true, ministers and their routes are grouped into districts
                        and lines are mapped between companionships (thick black line)
                        and between each companion and their assigned family (thin colored line).
                            If false, each minister and ministered are
                        a symbol on the map, and grouped by ministers and ministered
                            (ministered includes the ministers who are ministered to).
        @param auxiliaryId Which auxiliary we are to map
        @param auxiliaryName The name of the auxiliary we are mapping
        @param companionshipName The name of the style to use for the companionships
                                    (thick, black line)
        @param ministryPrefix The prefix for the name of the styles for ministry lines
                            (thin, colored line). Will be suffixed by the index 0 ... n
        @param ministerName The name of the style to use for ministers (icon style)
        @param ministeredName The name of the style to use for ministered families (icon style)
        @param map map of individualId to the household they are in
        @param container The KML entity that will contain the entries generated
        @throws IOException on network error
        @throws ParseException When parsing incorrect JSON
    */
    private static void mapCompanionships(LdsToolsClient client,
                    JSONObject relocations, boolean routes,
                    String auxiliaryId, String auxiliaryName,
                    String companionshipName,
                    String ministryPrefix,
                    String ministerName, String ministeredName,
                    Map<String, Household> map, KMLWriter.List container)
                                        throws IOException, ParseException {
        JSONArray districtsJSON = client.getAppPropertyEndpointList(
                                    "ministering-companionships-endpoint", auxiliaryId);
        List<District> districts = District.fromArray(districtsJSON);
        KMLWriter.Folder folder = new KMLWriter.Folder()
                                    .append(new KMLWriter.Name(auxiliaryName))
                                    .append(new KMLWriter.Description(
                                        "Map of ministering companionships for " + auxiliaryName));
        List<String> ministerIndividualIds = new ArrayList<String>();
        Map<String, Assignment> ministeredIndividualIds = new HashMap<String, Assignment>();

        if (districtsJSON.size() == 0) {
            return;
        }

        for (District district : districts) {
            KMLWriter.Folder districtFolder = new KMLWriter.Folder()
                                            .append(new KMLWriter.Name(district.getName()))
                                            .append(new KMLWriter.Description(
                                                "Map of ministering companionships for "
                                                    + district.getName()));
            int ministryIndex = 0;

            for (Companionship companionship : district.getCompanionships()) {
                String name = "";
                String prefix;
                double startLon = 0.0;
                double startLat = 0.0;
                boolean haveStart = false;
                KMLWriter.Placemark connected = new KMLWriter.Placemark();
                KMLWriter.Line connection = new KMLWriter.Line();
                boolean distinctCompanionLocations = false;

                prefix = "";
                for (Teacher teacher : companionship.getTeachers()) {
                    String    individualId = teacher.getIndividualId();
                    Household household = map.get(individualId);

                    if (!ministerIndividualIds.contains(individualId)
                                && companionship.getAssignments().size() > 0) {
                        ministerIndividualIds.add(individualId);
                    }

                    if (null != household) {
                        name = name + prefix + household.getMember(individualId).getPreferredName();
                        prefix = " - ";

                        if (null != household.relocate(relocations).getLattitude()
                                    && null != household.relocate(relocations).getLongitude()) {
                            double lat = household.relocate(relocations).getLatitudeValue();
                            double lon = household.relocate(relocations).getLongitudeValue();

                            connection.add(lat, lon, 0.0);
                            if (!haveStart) {
                                haveStart = true;
                                startLon = lon;
                                startLat = lat;
                            }
                            distinctCompanionLocations = distinctCompanionLocations
                                                            || startLon != lon
                                                            || startLat != lat;
                        } else {
                            System.out.println("WARNING: No location found for "
                                                            + household.getCoupleName());
                        }
                    }
                }

                if (distinctCompanionLocations) {
                    districtFolder.append(connected
                                .append(new KMLWriter.Name(name))
                                .append(new KMLWriter.Description("Ministering Companionship: "
                                                                        + name))
                                .append(new KMLWriter.UseStyle(companionshipName))
                                .append(connection));
                } else if (companionship.getTeachers().size() > 0) {
                    districtFolder.append(new KMLWriter.Placemark()
                                .append(new KMLWriter.Name(name))
                                .append(new KMLWriter.Description(name))
                                .append(new KMLWriter.UseStyle(ministerName))
                                .append(new KMLWriter.Point(startLat, startLon, 0.0)));
                }

                for (Assignment assignment : companionship.getAssignments()) {
                    String    familyId = assignment.getIndividualId();
                    Household family = map.get(familyId);

                    if (!ministeredIndividualIds.containsKey(familyId)) {
                        ministeredIndividualIds.put(familyId, assignment);
                    }

                    if (null != family.relocate(relocations).getLattitude()
                                && null != family.relocate(relocations).getLongitude()) {
                        connected = new KMLWriter.Placemark();
                        connection = new KMLWriter.Line();
                        double latFamily = family.relocate(relocations).getLatitudeValue();
                        double lonFamily = family.relocate(relocations).getLongitudeValue();

                        connection.add(latFamily, lonFamily, 0.0);

                        prefix = "";
                        name = family.getMember(familyId).getPreferredName() + " ministered by ";
                        for (Teacher teacher : companionship.getTeachers()) {
                            String    individualId = teacher.getIndividualId();
                            Household household = map.get(individualId);

                            name += prefix + household.getMember(individualId).getPreferredName();
                            prefix = ", ";
                            if (null != household.relocate(relocations).getLattitude()
                                    && null != household.relocate(relocations).getLongitude()) {
                                double lat = household.relocate(relocations).getLatitudeValue();
                                double lon = household.relocate(relocations).getLongitudeValue();

                                connection.add(lat, lon, 0.0);
                            }

                        }

                        connection.add(latFamily, lonFamily, 0.0);
                        districtFolder.append(connected
                                        .append(new KMLWriter.Name(name))
                                        .append(new KMLWriter.Description("Family Ministered: "
                                                    + family.getMember(familyId).getPreferredName()
                                                    + "\n"
                                                    + getVisitMessage(assignment)))
                                        .append(new KMLWriter.UseStyle(ministryPrefix
                                                                        + ministryIndex))
                                        .append(connection));

                    } else {
                        System.out.println("WARNING: No location found for "
                                                            + family.getCoupleName());
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
                                .append(new KMLWriter.Description(
                                    "All persons assigned to minister that have assignments"));

        for (String individualId : ministerIndividualIds) {
            Household household = map.get(individualId);
            double lat = household.relocate(relocations).getLatitudeValue();
            double lon = household.relocate(relocations).getLongitudeValue();

            group.append(new KMLWriter.Placemark()
                .append(new KMLWriter.Name(household.getMember(individualId).getPreferredName()))
                .append(new KMLWriter.Description(household.getMember(individualId)
                                    .getPreferredName()))
                .append(new KMLWriter.UseStyle(ministerName))
                .append(new KMLWriter.Point(lat, lon, 0.0)));
        }

        if (!routes) {
            folder.append(group);
        }

        group = new KMLWriter.Folder()
                    .append(new KMLWriter.Name("Ministered"))
                    .append(new KMLWriter.Description("All persons who are assigned ministers"));

        for (Map.Entry<String, Assignment> indAssignment : ministeredIndividualIds.entrySet()) {
            Household household = map.get(indAssignment.getKey());
            Assignment assignment = indAssignment.getValue();
            double lat = household.relocate(relocations).getLatitudeValue();
            double lon = household.relocate(relocations).getLongitudeValue();

            group.append(new KMLWriter.Placemark()
                .append(new KMLWriter.Name(household.getMember(
                                            indAssignment.getKey()).getPreferredName()))
                .append(new KMLWriter.Description(household.getMember(
                                                    indAssignment.getKey()).getPreferredName()
                                                    + "\n"
                                                    + getVisitMessage(assignment)))
                .append(new KMLWriter.UseStyle(ministeredName))
                .append(new KMLWriter.Point(lat, lon, 0.0)));
        }

        if (!routes) {
            folder.append(group);
        }

        container.add(folder);
    }

    /** Generates a KML map file that shows ministers and the ministered.
        This requires leadership privileges in order to generate.
        @param client The logged in LDS Tools client
        @param relocations Map of coupleName to items to update in their address
        @param routes If true, ministers and their routes are grouped into districts
                        and lines are mapped between companionships (thick black line)
                        and between each companion and their assigned family (thin colored line).
                        If false, each minister and ministered are
                        a symbol on the map, and grouped by ministers and ministered
                        (ministered includes the ministers who are ministered to).
        @param filePath The path to the .kml file to generate
        @throws IOException on network error
        @throws ParseException When parsing incorrect JSON
    */
    public static void generateMinistersReport(LdsToolsClient client, JSONObject relocations,
                    boolean routes, String filePath) throws IOException, ParseException {
        final String url1 = "http://maps.google.com/mapfiles/kml/shapes/capital_big_highlight.png";
        final String url2 = "http://maps.google.com/mapfiles/kml/shapes/placemark_square.png";
        final int thickLine = 8;
        final int thinLine = 2;
        KMLWriter.Document document = new KMLWriter.Document();
        JSONObject ward = client.getEndpointInfo(
                                    "unit-members-and-callings-v2", client.getUnitNumber());
        JSONArray households = (JSONArray) ward.get("households");
        List<Household> householdList = Household.fromArray(households);
        Map<String, Household> idToHousehold = client.leaderReportsAvailable()
                                            ? Household.mapIndividualIdsToHousehold(householdList)
                                            : null;

        String[] colors = {"7fff0000", "7f0000ff", "7fffff00",
                            "7f00ffff", "7fff00ff", "7f7f7f7f",
                            "7f00ff00", "7fff7f7f", "7f7f7fff",
                            "7fffff7f", "7f7fffff", "7fff7fff",
                            "7f7f7f7f", "7f7fff7f"};

        document.append(new KMLWriter.Name((String) ward.get("orgName")))
                .append(new KMLWriter.Open())
                .append(new KMLWriter.Description("Map of the "
                                    + (String) ward.get("orgName"))) // put date in here
                .append(new KMLWriter.Style("minister")
                .append(new KMLWriter.StyleIcon(url1)))
                .append(new KMLWriter.Style("ministered")
                .append(new KMLWriter.StyleIcon(url2)))
                .append(new KMLWriter.Style("companionship")
                .append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(thickLine))
                                            .append(new KMLWriter.StyleColor("44000000"))));

        for (int colorIndex = 0; colorIndex < colors.length; ++colorIndex) {
            document
                .append(new KMLWriter.Style("ministry" + colorIndex)
                .append(new KMLWriter.LineStyle().append(new KMLWriter.StyleWidth(thinLine))
                                    .append(new KMLWriter.StyleColor(colors[colorIndex]))));
        }

        if (client.leaderReportsAvailable()) {
            List<String> priesthood = new ArrayList<String>();
            List<String> reliefsociety = new ArrayList<String>();

            MinistryHelpers.getAuxiliaries(client, priesthood, reliefsociety);

            for (String aux : priesthood) {
                mapCompanionships(client, relocations, routes, aux, "Priesthood",
                    "companionship", "ministry", "minister", "ministered", idToHousehold, document);
            }

            for (String aux : reliefsociety) {
                mapCompanionships(client, relocations, routes, aux, "Relief Society",
                    "companionship", "ministry", "minister", "ministered", idToHousehold, document);
            }
        }

        KMLWriter.write(filePath, document);

    }

    /** Generate .kml file for all members of the ward.
        This requires no special privileges to generate.
        Icons for maps can be found at: http://kml4earth.appspot.com/icons.html
        @param client lds tools client to use for connection
        @param relocations a JSONObject that maps coupleName field to various address
                            fields to change
        @param filePath the path to the .kml file to generate
        @throws IOException on io error
        @throws ParseException on JSON error
    */
    public static void generateMapReport(LdsToolsClient client, JSONObject relocations,
                                    String filePath) throws IOException, ParseException {
        String url = "http://maps.google.com/mapfiles/kml/shapes/placemark_circle_highlight.png";
        KMLWriter.Document document = new KMLWriter.Document();
        JSONObject ward = client.getEndpointInfo(
                                "unit-members-and-callings-v2", client.getUnitNumber());
        JSONArray households = (JSONArray) ward.get("households");
        double minLat = 0.0;
        double maxLat = 0.0;
        double minLon = 0.0;
        double maxLon = 0.0;
        List<Household> householdList = Household.fromArray(households);
        KMLWriter.Folder folder = new KMLWriter.Folder();

        folder.append(new KMLWriter.Name("Households"))
            .append(new KMLWriter.Description("Households in the " + (String) ward.get("orgName")));

        for (Household household : householdList) {

            if (null != household.relocate(relocations).getLattitude()
                        && null != household.relocate(relocations).getLongitude()) {
                double lat = household.relocate(relocations).getLatitudeValue();
                double lon = household.relocate(relocations).getLongitudeValue();

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
                folder.append(new KMLWriter.Placemark()
                    .append(new KMLWriter.Name(household.getHouseholdName()))
                    .append(new KMLWriter.Description(household.getCoupleName()))
                    .append(new KMLWriter.UseStyle("home"))
                    .append(new KMLWriter.Point(lat, lon, 0.0)));

            } else {
                System.out.println("WARNING: We don't have lat/lon for "
                                                        + household.getCoupleName());
                // TODO: figure out where these are
            }

        }

        document.append(new KMLWriter.Name((String) ward.get("orgName")))
        .append(new KMLWriter.Open())
        .append(new KMLWriter.Description("Map of the "
                                    + (String) ward.get("orgName"))) // put date in here
        .append(new KMLWriter.Style("home")
        .append(new KMLWriter.StyleIcon(url)))
        .append(folder);

        KMLWriter.write(filePath, document);

    }

}
