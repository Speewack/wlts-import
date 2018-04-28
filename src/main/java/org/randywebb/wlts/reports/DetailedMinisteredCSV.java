package org.randywebb.wlts.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.randywebb.wlts.beans.DetailedMinistered;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.ldstools.rest.MinistryHelpers;

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

public class DetailedMinisteredCSV {

  private static Logger log = LoggerFactory.getLogger(DetailedMinisteredCSV.class);

  public static void generateMiniseteredReport(LdsToolsClient client, String filePath, JSONObject relocations) throws IOException, ParseException {
  	JSONObject ward = client.getEndpointInfo("unit-members-and-callings-v2", client.getUnitNumber());
    List<Household> household_list = Household.fromArray((JSONArray) ward.get("households"));
	List<String> priesthood = new ArrayList<String>();
	List<String> reliefsociety = new ArrayList<String>();
	List<DetailedMinistered> ministered = new ArrayList<DetailedMinistered>();

	MinistryHelpers.getAuxiliaries(client, priesthood, reliefsociety);

	for (String auxiliaryId : priesthood) {
		List<District> districts = District.fromArray(client.getAppPropertyEndpointList("ministering-companionships-endpoint", auxiliaryId));
		ministered.addAll(DetailedMinistered.fromDistricts(districts, household_list, relocations));
	}

	for (String auxiliaryId : reliefsociety) {
		List<District> districts = District.fromArray(client.getAppPropertyEndpointList("ministering-companionships-endpoint", auxiliaryId));
		ministered.addAll(DetailedMinistered.fromDistricts(districts, household_list, relocations));
	}

	writeCSVFile(filePath, ministered);

	System.out.println("Export complete");
  }

  public static void writeCSVFile(String csvFileName, List<DetailedMinistered> members) {
    ICsvMapWriter beanWriter = null;
    CellProcessor[] processors = new CellProcessor[] { new ConvertNullTo(""), // "id",
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo(""),
        new ConvertNullTo("")
    };

    try {
      beanWriter = new CsvMapWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);
      String[] header = {
      	"individualId", "assignmentType", "ministeringCompanionshipId", "auxiliaryId", "districtLeaderId",
      	"districtLeaderIndividualId", "districtName", "districtId", "ministerStartDate", "fullName",
      	"preferredName", "memberId", "surname", "givenName", "phone", "email", "latitude", "longitude",
      	"postalCode", "state", "desc1", "desc2", "desc3", "householdName", "householdPhone",
      	"householdEmailAddress", "householdCoupleName", "ministers", "districtLeaderName", "nearestHouseholdName"
 };

      beanWriter.writeHeader(header);

      for (DetailedMinistered member : members) {
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
