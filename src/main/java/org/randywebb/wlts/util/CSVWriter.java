package org.randywebb.wlts.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.randywebb.wlts.beans.DetailedMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class CSVWriter {

	private static Logger log = LoggerFactory.getLogger(CSVWriter.class);

	public static void writeCSVFile(String csvFileName, List<DetailedMember> members) {
		ICsvBeanWriter beanWriter = null;
		CellProcessor[] processors = new CellProcessor[] { 
				new ConvertNullTo(""), // "id", 
				new ConvertNullTo(""), // "mrn", 
				new ConvertNullTo(""), // "formattedMRN",
				new ConvertNullTo(""), // "name", 
				new ConvertNullTo(""), // "givenName",
				new ConvertNullTo(""), // "spokenName",
				new ConvertNullTo(""), // "address", 
				new ConvertNullTo(""), // "age", 
				new FmtDate("dd mmm yyyyy"), // "birthDate",  
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
			String[] header = { 
					"id", 
					"mrn", 
					"formattedMRN",
					"name", 
					"givenName",
					"spokenName",
					"address", 
					"age", 
					"birthDate",  
					"email",
					"phone", 
					"gender", 
					"genderCode",
					"coupleName",
					"householdEmail", 
					"householdId", 
					"householdPhone", 
					"isAdult", 
					"isHead", 
					"isSpouse", 
					"nonMember", 
					"outOfUnitMember", 
					"priesthood", 
					"unitNumber", 
					"unitName" };
		
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
