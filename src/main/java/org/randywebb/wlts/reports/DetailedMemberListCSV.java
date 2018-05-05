package org.randywebb.wlts.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.randywebb.wlts.beans.DetailedMember;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DetailedMemberListCSV {

  private static Logger log = LoggerFactory.getLogger(DetailedMemberListCSV.class);

  public static void generateWLTSReport(LdsToolsClient client, String filePath) throws IOException, ParseException {
	// Parse JSON Membership file into beans

	InputStream in = client.getMemberInfo();

	List<DetailedMember> members = processDetailMembers(in);

	// List<DetailedMember> members = processDetailMembers(Thread.currentThread().getContextClassLoader().getResourceAsStream("detailedmembership.json"));
	// List<Household> households = processHouseholds(Thread.currentThread().getContextClassLoader().getResourceAsStream("membership.json"));

	writeCSVFile(filePath, members);

	System.out.println("Export complete");
  }

  private static List<DetailedMember> processDetailMembers(InputStream in) throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new InputStreamReader(in));

    return DetailedMember.fromArray( (JSONArray) obj );
  }

  private static List<Household> processHouseholds(InputStream in) throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new InputStreamReader(in));
    JSONObject jo = (JSONObject) obj;

    return Household.fromArray( (JSONArray) jo.get("households") );
  }

  public static void writeCSVFile(String csvFileName, List<DetailedMember> members) {
    ICsvMapWriter beanWriter = null;
    String[] header = { "id", "mrn", "formattedMRN", "name", "givenName", "spokenName", "street", "city", "state", "zip", "age", "birthDate", "email", "phone", "gender", "genderCode", "coupleName",
          "householdEmail", "householdId", "householdPhone", "isAdult", "isHead", "isSpouse", "nonMember", "outOfUnitMember", "priesthood", "unitNumber", "unitName" };
    CellProcessor[] processors = DetailedMember.csvProcessors(header, new ConvertNullTo(""), new FmtBool("true", "false"), new ConvertNullTo(""), new FmtDate("dd MMM yyyy"));

    try {
      beanWriter = new CsvMapWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

      beanWriter.writeHeader(header);

      for (DetailedMember member : members) {
        beanWriter.write(member.forCSV(), header, processors);
      }

    } catch (IOException ex) {
      log.error("Error writing the CSV file: " + ex);
    } finally {
      if (beanWriter != null) {
        try {
          beanWriter.close();
        } catch (IOException ex) {
          log.error("Error closing the writer: " + ex);
        }
      }
    }
  }

}
