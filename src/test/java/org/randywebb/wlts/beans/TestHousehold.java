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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TestHousehold {

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("households.json")));
            List<Household> households = Household.fromArray( (JSONArray) obj );
            String coupleNames[] = {"AppleSeed, Sam & Sally", "AppleSeed, Sam", "AppleSeed, Sam", "AppleSeed, Sam & Jane"};
            int childrenCounts[] = {2, 1, 0, 0};

            assertEquals(households.size(), coupleNames.length);
            assertEquals(childrenCounts.length, coupleNames.length);

            assertEquals(households.get(0).getSpouse().getIndividualId(), "99293166028");
            assertNull(households.get(1).getSpouse());
            assertNull(households.get(2).getSpouse());
            assertEquals(households.get(3).getSpouse().getIndividualId(), "99520562427");

            for (int i = 0; i < childrenCounts.length; i += 1) {
                assertEquals(households.get(i).getChildren().size(), childrenCounts[i]);
                assertEquals(households.get(i).getCoupleName(), coupleNames[i]);
                assertEquals(households.get(i).getHeadOfHouseholdIndividualID(), "99520561444");
                assertEquals(households.get(i).getHouseholdName(), "AppleSeed");
                assertEquals(households.get(i).getHouseholdAddress().getStreetAddress(), "3421 Toyville Trl");
                assertEquals(households.get(i).getHouseholdAddress().getCity(), "Round Rock");
                assertEquals(households.get(i).getEmailAddress(), "Sam_Appleseed@gmail.com");
                assertEquals(households.get(i).getPhone(), "423-867-5309");
                assertEquals(households.get(i).getHeadOfHousehold().getIndividualId(), "99520561444");
            }

            // Address and household members are tested in their own tests

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
