/**
 *
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.randywebb.wlts.beans.DetailedMember;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.ldstools.json.DetailedMemberConsumer;
import org.randywebb.wlts.ldstools.json.HouseholdConsumer;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.util.CSVWriter;
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
      System.out.println("Usage Main username password target_file");
      System.out.println("\tor Main username target_file");
      System.out.println("\tor Main --minimal username password target_file");
      System.out.println("\tor Main --minimal username target_file");
      System.exit(1);
    }

	final boolean minimal = args[0].equals("--minimal");
	final boolean hasPassword = (minimal && args.length == 4) || (!minimal && args.length == 3);
	final int usernameIndex = minimal ? 1 : 0;
	final int targetIndex = 1 + (hasPassword ? 1 : 0) + (minimal ? 1 : 0);
	final int passwordIndex = hasPassword ? (minimal ? 2 : 1) : 0;

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

	if (!minimal && isUserAdmin(client.getEndpointInfo("current-user-detail"))) {
		generateWLTSReport(client, filePath);
	} else if (minimal) {
		generateMinimalReport(client, filePath);
		System.out.println("This will be really cool when we implement it!");
	} else {
		System.out.println("You do not have permissions to export all data. You can pass '--minimal' as the first argument to export what you can");
	}

  }

  private static void generateMinimalReport(LdsToolsClient client, String filePath) throws IOException, ParseException {
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
  	System.out.println("Unit: " + (String) ward.get("orgName"));
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
