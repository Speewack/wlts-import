package org.randywebb.wlts.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.randywebb.wlts.beans.DetailedMinistered;
import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.ldstools.rest.LdsToolsClient;
import org.randywebb.wlts.ldstools.rest.MinistryHelpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/** Utility methods to export detailed ministered families in CSV */
public class DetailedMinisteredCSV {

	/** Can be used for logging debugging messages */
	private static Logger log = LoggerFactory.getLogger(DetailedMinisteredCSV.class);

	/** Generates a CSV file of all the families on the ministering lists the user has access to.
		@param client The logged in LDS tools client
		@param filePath the path to the CSV file to create
		@param relocations mappings from coupleName to fields to update in the address (used for incorrect or missing information in LDS tools)
		@throws IOException on network error
		@throws ParseException When the JSON is not as we expected
	*/
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

	/** Given a list of members, writes them to a CSV file.
		@param csvFileName The path to the CSV file to create
		@param members The members, in order, to be written to the CSV file
	*/
	private static void writeCSVFile(String csvFileName, List<DetailedMinistered> members) {
		ICsvMapWriter beanWriter = null;
		String[] header = {
			"individualId", "assignmentType", "ministeringCompanionshipId", "auxiliaryId", "districtLeaderId",
			"districtLeaderIndividualId", "districtName", "districtId", "ministerStartDate", "fullName",
			"preferredName", "memberId", "surname", "givenName", "phone", "email", "latitude", "longitude",
			"postalCode", "state", "desc1", "desc2", "desc3", "householdName", "householdPhone",
			"householdEmailAddress", "householdCoupleName", "ministers", "districtLeaderName", "nearestHouseholdName"
		};
		CellProcessor[] processors = DetailedMinistered.csvProcessors(header, new ConvertNullTo(""));

		try {
			beanWriter = new CsvMapWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

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
