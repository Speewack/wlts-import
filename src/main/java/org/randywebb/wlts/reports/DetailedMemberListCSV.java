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
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
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
    JSONObject jo = (JSONObject) obj;

    return DetailedMember.fromArray( (JSONArray) jo.get("households") );
  }

  private static List<Household> processHouseholds(InputStream in) throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new InputStreamReader(in));
    JSONObject jo = (JSONObject) obj;

    return Household.fromArray( (JSONArray) jo.get("households") );
  }

  public static void writeCSVFile(String csvFileName, List<DetailedMember> members) {
    ICsvBeanWriter beanWriter = null;
    CellProcessor[] processors = new CellProcessor[] { new ConvertNullTo(""), // "id",
        new ConvertNullTo(""), // "mrn",
        new ConvertNullTo(""), // "formattedMRN",
        new ConvertNullTo(""), // "name",
        new ConvertNullTo(""), // "givenName",
        new ConvertNullTo(""), // "spokenName",
        new ConvertNullTo(""), // "street",
        new ConvertNullTo(""), // "city",
        new ConvertNullTo(""), // "state",
        new ConvertNullTo(""), // "zip",
        new ConvertNullTo(""), // "age",
        new FmtDate("dd MMM yyyy"), // "birthDate",
        new ConvertNullTo(""), // "email",
        new ConvertNullTo(""), // "phone",
        new ConvertNullTo(""), // "gender",
        new ConvertNullTo(""), // "genderCode",
        new ConvertNullTo(""), // "coupleName",
        new ConvertNullTo(""), // "householdEmail",
        new ConvertNullTo(""), // "householdId",
        new ConvertNullTo(""), // "householdPhone",
        new FmtBool("true", "false"), // "isAdult",
        new FmtBool("true", "false"), // "isHead",
        new FmtBool("true", "false"), // "isSpouse",
        new FmtBool("true", "false"), // "nonMember",
        new FmtBool("true", "false"), // "outOfUnitMember",
        new ConvertNullTo(""), // "priesthood",
        new ConvertNullTo(""), // "unitNumber",
        new ConvertNullTo("") // "unitName"
    };

    try {
      beanWriter = new CsvBeanWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);
      String[] header = { "id", "mrn", "formattedMRN", "name", "givenName", "spokenName", "street", "city", "state", "zip", "age", "birthDate", "email", "phone", "gender", "genderCode", "coupleName",
          "householdEmail", "householdId", "householdPhone", "isAdult", "isHead", "isSpouse", "nonMember", "outOfUnitMember", "priesthood", "unitNumber", "unitName" };

      beanWriter.writeHeader(header);

      for (DetailedMember member : members) {
        beanWriter.write(member, header, processors);
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
