package org.randywebb.wlts.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.randywebb.wlts.beans.DetailedMember;
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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Utility methods to export the detailed member list as CSV. */
public final class DetailedMemberListCSV {

    /** default constructor. */
    private DetailedMemberListCSV() {
    }

    /** Can be used for logging debugging messages. */
    private static Logger log = LoggerFactory.getLogger(DetailedMemberListCSV.class);

    /**
        @param client LDS Tools client to use
        @param filePath The path to the CSV file to generate
        @throws IOException on network errors
        @throws ParseException If the JSON was not the format that was expected
    */
    public static void generateWLTSReport(LdsToolsClient client, String filePath)
                                                throws IOException, ParseException {
        // Parse JSON Membership file into beans

        InputStream in = client.getMemberInfo();

        List<DetailedMember> members = processDetailMembers(in);

        /*
        List<DetailedMember> members = processDetailMembers(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("detailedmembership.json"));
        List<Household> households = processHouseholds(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("membership.json"));
        */

        writeCSVFile(filePath, members);

        System.out.println("Export complete");
    }

    /** Parse a list of members from a JSON array stream.
        @param in The member info stream
        @return The list of members from the stream
        @throws IOException on io error
        @throws ParseException When the JSON is not formatted as expected
    */
    private static List<DetailedMember> processDetailMembers(InputStream in)
                                                throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new InputStreamReader(in));

        return DetailedMember.fromArray((JSONArray) obj);
    }

    /** Write a list of members as a CSV file.
        @param csvFileName The path to write the CSV file to
        @param members The list of members to write to the CSV file
    */
    public static void writeCSVFile(String csvFileName, List<DetailedMember> members) {
        ICsvMapWriter beanWriter = null;
        String[] header = {"id", "mrn", "formattedMRN", "name", "givenName", "spokenName",
                            "street", "city", "state", "zip", "age", "birthDate", "email",
                            "phone", "gender", "genderCode", "coupleName", "householdEmail",
                            "householdId", "householdPhone", "isAdult", "isHead", "isSpouse",
                            "nonMember", "outOfUnitMember", "priesthood", "unitNumber",
                            "unitName"};
        CellProcessor[] processors = DetailedMember.csvProcessors(header,
                                                    new ConvertNullTo(""),
                                                    new FmtBool("true", "false"),
                                                    new ConvertNullTo(""),
                                                    new FmtDate("dd MMM yyyy"));

        try {
            beanWriter = new CsvMapWriter(new FileWriter(csvFileName),
                                            CsvPreference.STANDARD_PREFERENCE);

            beanWriter.writeHeader(header);

            for (DetailedMember member : members) {
                beanWriter.write(member.forCSV(), header, processors);
            }

        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Error writing the CSV file", ex);
            }
        } finally {
            if (beanWriter != null) {
                try {
                    beanWriter.close();
                } catch (IOException ex) {
                    if (log.isErrorEnabled()) {
                        log.error("Error closing the writer", ex);
                    }
                }
            }
        }

    }

}
