package org.randywebb.wlts;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.auth.AuthenticationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.reports.DetailedMemberListCSV;
import org.randywebb.wlts.reports.DetailedMinisteredCSV;
import org.randywebb.wlts.reports.MinisteringKML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main application.
* @author randyw
*
*/
public class Main {

    /** Can be used for logging debugging messages */
    private static Logger log = LoggerFactory.getLogger(Main.class);

    /** Display documentation for command line argumnets. */
    private static void printUsage() {
        System.out.println("Usage: Main [username [password]] target_file");
        System.out.println("       Main [username [password]] [--relocate relocate_file] <verb> target_file [<verb> target_file]...");
        System.out.println("       Main --help");
        System.out.println();
        System.out.println("  Verbs");
        System.out.println("    --map        target_file is a .kml file that maps every household");
        System.out.println("    --routes     target_file is a .kml file that maps ministering routes (requires Leadership account)");
        System.out.println("    --ministers  target_file is a .kml file that maps ministers and ministered households (requires Leadership account)");
        System.out.println("    --wlts       target_file is a .csv file that contains all households (requires Admin account)");
        System.out.println("    --ministered target_file is a .csv file that contains all families being ministered to");
        System.out.println();
        System.out.println("  --help         Display this content");
        System.out.println("  --relocate     relcoate_file is a json file that maps coupleName to fields to replace in the Household records");
        System.out.println("      ie {\"Smith, Joe & Jane\" : { \"latitude\" : 35.0000,\"longitude\" : -95.0000}}");
        System.out.println("      Suggested fields");
        System.out.println("        address    The street address");
        System.out.println("        state      The state code");
        System.out.println("        postalCode Zip or province code");
        System.out.println("        latitude");
        System.out.println("        longitude");
    }

    /**
    * @param args
    *          Arguments to the program. See printUsage()
    * @throws Exception
    *           Just in case we didn't handle an exception in a user-friendly way
    */
    public static void main(String... args) throws Exception {
        Map<String, String> switches = new HashMap<String, String>();
        String[] outputFileTypes = { "--map", "--routes", "--ministers", "--relocate", "--wlts", "--ministered" };
        String[] onOff = { "--help" };
        String[] arguments = parseArgs(args, onOff, outputFileTypes, switches);
        JSONObject relocations = switches.containsKey("--relocate") ? loadJSONFile(switches.get("--relocate")) : null;

        if (switches.containsKey("--help")) {
            printUsage();
            System.exit(0);
        }

        // read input parameters
        if (arguments.length > 3) {
            printUsage();
            log.trace("arguments.length = " + arguments.length);
            for (String argument : arguments) {
                log.trace("\t" + argument);
            }
            System.exit(1);
        }

        final boolean ministers = switches.containsKey("--ministers");
        final boolean routes = switches.containsKey("--routes");
        final boolean ministered = switches.containsKey("--ministered");
        final boolean map = switches.containsKey("--map");
        final boolean wlts = switches.containsKey("--wlts");
        final boolean verb = ministers || map || routes || wlts || ministered;
        final boolean hasUsername = verb && arguments.length > 0 || !verb && arguments.length > 1;
        final boolean hasPassword = hasUsername && (verb && arguments.length > 1 || !verb && arguments.length > 2);
        final int usernameIndex = 0;
        final int targetIndex = (hasUsername ? 1 : 0) + (hasPassword ? 1 : 0);
        final int passwordIndex = 1;

        if (!verb && arguments.length < 1) {
            printUsage();
            log.trace("arguments.length = " + arguments.length);
            for (String argument : arguments) {
                log.trace("\t" + argument);
            }
            System.exit(1);
        }

        // determine username and password
        String password = hasPassword ? arguments[passwordIndex] : null;
        String username = hasUsername ? arguments[usernameIndex] : null;

        if (!hasUsername) {
            System.out.print("Enter username: ");
            System.out.flush();
            username = new Scanner(System.in).nextLine();
        }

        if (!hasPassword) {
            Console console = System.console();
            String prompt = "Enter password [" + username + "]: ";

            if (null == console) {
                System.out.print(prompt);
                System.out.flush();
                password = new Scanner(System.in).nextLine();
            } else {
                password = new String(console.readPassword(prompt));
            }
        }

        // Initialize LDSTools Client
        LdsToolsClient client = null;

        try {

            if (hasUsername || hasPassword) {
                System.out.println("Logging '" + username + "' into LDS Tools");
            }

            client = new LdsToolsClient(username, password);
        } catch (AuthenticationException exception) {
            System.out.println("Authentication error: " + exception.getMessage());
            System.exit(1);
        }

        // Generate the requested report

        if (wlts || !verb) {
            String filePath = wlts ? switches.get("--wlts") : arguments[targetIndex];

            if (!isUserAdmin(client.getEndpointInfo("current-user-detail"))) {
                printUsage();
                System.out.println("You need to have Admin access to generate that report.");
                System.exit(1);
            }

            DetailedMemberListCSV.generateWLTSReport(client, filePath);

        }

        if (map) {
            MinisteringKML.generateMapReport(client, relocations, switches.get("--map"));
        }

        if (ministers) {
            MinisteringKML.generateMinistersReport(client, relocations, false, switches.get("--ministers"));
        }

        if (routes) {
            MinisteringKML.generateMinistersReport(client, relocations, true, switches.get("--routes"));
        }

        if (ministered) {
            DetailedMinisteredCSV.generateMiniseteredReport(client, switches.get("--ministered"), relocations);
        }

    }

    /**
    * Determine whether the user is an Admin and thus, has access to pull necessary data from home unit
    *
    * @param userDetail
    *          JSONObject containing LDS User Detail
    * @return boolean indicating whether user has Admin permissions for home unit
    * @throws IOException on io errors
    * @throws ParseException on JSON errors
    */
    private static boolean isUserAdmin(JSONObject userDetail) throws IOException, ParseException {
        Long unitNumber = (Long) userDetail.get("homeUnitNbr");

        return isUserAdmin(userDetail, unitNumber);
    }

    /**
    * Determine whether the user is an Admin and thus, has access to pull necessary data from specified unit
    *
    * @param userDetail
    *          JSONObject containing LDS User Detail
    * @param unitNumber
    *          Unit Number for which to fetch data
    * @return boolean indicating whether user has Admin permissions for requested unit
    * @throws IOException on io errors
    * @throws ParseException on JSON errors
    */
    private static boolean isUserAdmin(JSONObject userDetail, Long unitNumber) throws IOException, ParseException {
        JSONArray units = (JSONArray) userDetail.get("units");

        if (null == unitNumber) {
            unitNumber = (Long) userDetail.get("homeUnitNbr");
        }

        for (Object unitObject : units) {
            JSONObject unit = (JSONObject) unitObject;

            if (unitNumber.equals(unit.get("unitNo"))) {
                return (Boolean) unit.get("hasUnitAdminRights");
            }

            for (Object localUnitObject : (JSONArray) unit.get("localUnits")) {
                JSONObject localUnit = (JSONObject) localUnitObject;

                if (unitNumber.equals(localUnit.get("unitNo"))) {
                    return (Boolean) localUnit.get("hasUnitAdminRights");
                }

            }

        }

        return false;
    }

    /** Parse the command line arguments.
        @param args The command line arguments
        @param onOff The names of the command line arguments that have no values (ie --verbose)
        @param hasValue The names of the command line arguments that have values (ie --map path.kml)
        @param results A map that will receive all the param-values (ie '--map':'path.kml')
        @return The list of command line arguments that were not onOff or hasValue
    */
    private static String[] parseArgs(String[] args, String[] onOff, String[] hasValue, Map<String, String> results) {
        String next = null;
        List<String> ordinal = new ArrayList<String>();
        List<String> onOffList = (null == onOff) ? null : Arrays.asList(onOff);
        List<String> hasValueList = (null == hasValue) ? null : Arrays.asList(hasValue);

        for (String arg : args) {
            if (null != next) {
                results.put(next, arg);
                next = null;
            } else if (null != onOff && onOffList.contains(arg)) {
                results.put(arg, "true");
            } else if (null != hasValue && hasValueList.contains(arg)) {
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

    /** Load a JSON object from a file.
        @param path The path to the JSON file
        @return The contents of the file as a JSON object
        @throws IOException on io error
        @throws FileNotFoundException if the path does not exist
        @throws ParseException if the file does not contain a proper JSON object
    */
    private static JSONObject loadJSONFile(String path) throws IOException, FileNotFoundException, ParseException {
        return (JSONObject) (new JSONParser()).parse(new FileReader(path));
    }

}
