/**
 *
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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
    if (args.length != 3) {
      System.out.println("Usage Main username password target_file");
      System.exit(1);
    }

    // Initialize LDSTools Client
    LdsToolsClient client = new LdsToolsClient(args[0], args[1]);

    // Capture file path from args
    String filePath = args[2];

    // Parse JSON Membership file into beans

    InputStream in = client.getMemberInfo();

    List<DetailedMember> members = processDetailMembers(in);

    // List<DetailedMember> members = processDetailMembers(Thread.currentThread().getContextClassLoader().getResourceAsStream("detailedmembership.json"));
    // List<Household> households = processHouseholds(Thread.currentThread().getContextClassLoader().getResourceAsStream("membership.json"));

    CSVWriter.writeCSVFile(filePath, members);

    System.out.println("Export complete");

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
