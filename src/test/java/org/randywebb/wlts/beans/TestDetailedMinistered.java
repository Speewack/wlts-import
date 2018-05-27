package org.randywebb.wlts.beans;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TestDetailedMinistered {

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            JSONObject testData = (JSONObject) new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("detailedMinistered.json")));
            List<District> districts = District.fromArray((JSONArray) testData.get("districts"));
            List<Household> households = Household.fromArray((JSONArray) testData.get("households"));
            List<DetailedMinistered> ministered;

            assertTrue(null != districts);
            assertTrue(null != households);

            ministered = DetailedMinistered.fromDistricts(districts, households, null);

            String individualIds[] = {"99520562427", "99520561444", "99293166028", "9984358113"};
            String fullNames[] = {"AppleSeed, Jane", "AppleSeed, Sam", "AppleSeed, Sally", "AppleSeed, Johnny"};
            String coupleNames[] = {"AppleSeed, Sam & Sally", "AppleSeed, Sam", "AppleSeed, Sam & Sally", "AppleSeed, Sam"};

            assertEquals(ministered.size(), individualIds.length);
            assertEquals(fullNames.length, individualIds.length);
            assertEquals(coupleNames.length, individualIds.length);

            for (int i = 0; i < individualIds.length; i += 1) {
                assertEquals(ministered.get(i).get("individualId"), individualIds[i]);
                assertEquals(ministered.get(i).get("fullName"), fullNames[i]);
                assertEquals(ministered.get(i).get("assignmentType"), "HT");
                assertEquals(ministered.get(i).get("ministeringCompanionshipId"), "9960466");
                assertEquals(ministered.get(i).get("auxiliaryId"), "9955389");
                assertEquals(ministered.get(i).get("districtLeaderId"), "99149899");
                assertEquals(ministered.get(i).get("districtName"), "Central");
                assertEquals(ministered.get(i).get("districtId"), "9965290");
                assertEquals(ministered.get(i).get("ministerStartDate"), "1470277332157");
                assertEquals(ministered.get(i).get("householdPhone"), "423-867-5309");
                assertEquals(ministered.get(i).get("householdEmailAddress"), "Sam_Appleseed@gmail.com");
                assertEquals(ministered.get(i).get("householdCoupleName"), coupleNames[i]);
                assertEquals(ministered.get(i).get("ministers"), "AppleSeed, Johnny & AppleSeed, Sam");
                assertNull(ministered.get(i).get("districtLeaderIndividualId"));
                assertNull(ministered.get(i).get("districtLeaderName"));
                assertNull(ministered.get(i).get("nearestHouseholdName"));
            }

            // some fields copied directly from districts or households are not tested

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
