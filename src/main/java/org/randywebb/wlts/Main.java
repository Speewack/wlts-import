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

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.reports.DetailedMemberListCSV;
import org.randywebb.wlts.reports.MinisteringKML;
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

		DetailedMemberListCSV.generateWLTSReport(client, filePath);

	} else if (verb) {

		if (map) {
			MinisteringKML.generateMapReport(client, relocations, switches.get("--map"));
		}

		if (ministers) {
			MinisteringKML.generateMinistersReport(client, relocations, false, switches.get("--ministers"));
		}

		if (routes) {
			MinisteringKML.generateMinistersReport(client, relocations, true, switches.get("--routes"));
		}

	} else {
		System.out.println("You do not have permissions to export all data. You can pass '--map', '--routes' or '--ministers' followed by an output file to export what you can");
	}

  }

  private static boolean isUserAdmin(JSONObject response) throws IOException, ParseException {
  	JSONArray units = (JSONArray) response.get("units");
  	JSONObject firstUnit = (JSONObject) units.get(0);

  	return (Boolean) firstUnit.get("hasUnitAdminRights");
  }

}
