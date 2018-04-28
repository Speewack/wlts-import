/**
 *
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.Console;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.reports.DetailedMemberListCSV;
import org.randywebb.wlts.reports.MinisteringKML;
import org.randywebb.wlts.reports.DetailedMinisteredCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Main {

  private static Logger log = LoggerFactory.getLogger(Main.class);

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
   * @throws Exception
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static void main(String... args) throws Exception {
  	Map<String,String> switches = new HashMap<String,String>();
  	String[] outputFileTypes = {"--map", "--routes", "--ministers", "--relocate", "--wlts", "--ministered"};
  	String[] onOff = {"--help"};
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
	final boolean hasUsername = (verb && arguments.length > 0) || (!verb && arguments.length > 1);
	final boolean hasPassword = hasUsername && ((verb && arguments.length > 1) || (!verb && arguments.length > 2));
	final int usernameIndex = 0;
	final int targetIndex =  (hasUsername ? 1 : 0) + (hasPassword ? 1 : 0);
	final int passwordIndex = 1;

	if ( !verb && (arguments.length < 1) ) {
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
		username = (new Scanner(System.in)).nextLine();
	}

	if (!hasPassword) {
		Console console = System.console();
		String prompt = "Enter password [" + username + "]: ";

		if (null == console) {
			System.out.print(prompt);
			System.out.flush();
			password = (new Scanner(System.in)).nextLine();
		} else {
			password = new String(console.readPassword(prompt));
		}
	}

    // Initialize LDSTools Client
    LdsToolsClient client = null;

    try {
		client = new LdsToolsClient(username, password);
    } catch(AuthenticationException exception) {
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

  private static boolean isUserAdmin(JSONObject response) throws IOException, ParseException {
  	JSONArray units = (JSONArray) response.get("units");
  	JSONObject firstUnit = (JSONObject) units.get(0);

  	return (Boolean) firstUnit.get("hasUnitAdminRights");
  }

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

}
