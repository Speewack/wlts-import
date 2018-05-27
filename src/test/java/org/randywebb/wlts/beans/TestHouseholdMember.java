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

class TestHouseholdMember {

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("householdMembers.json")));
            List<HouseholdMember> members = HouseholdMember.fromArray( (JSONArray) obj );
            String emails[] = {"JohnnyAppleseed@gmail.com", "JaneAppleseed@gmail.com", "Sam_Appleseed@gmail.com", null, "Molly.Appleseed@gmail.com", null, null};
            String fullNames[] = {"AppleSeed, Johnny", "AppleSeed, Jane", "AppleSeed, Sam", "AppleSeed, Sally", "AppleSeed, Molly", "AppleSeed, Lucy", "AppleSeed, Bruce"};
            String givenNames[] = {"Johnny", "Jane", "Sam", "Sally", "Molly", "Lucy", "Bruce"};
            long individualIds[] = {9984358113L, 99520562427L, 99520561444L, 99293166028L, 99293167011L, 9950329804L, 9950330787L};
            String memberId[] = {"990-0860-7332", null, null, null, null, null, null};
            String preferredNames[] = {"AppleSeed, Johnny", "AppleSeed, Jane", "AppleSeed, Sam", "AppleSeed, Sally", "AppleSeed, Molly", "AppleSeed, Lucy", "AppleSeed, Bruce"};
            String surname = "AppleSeed";

            assertEquals(members.size(), emails.length);
            assertEquals(fullNames.length, emails.length);
            assertEquals(givenNames.length, emails.length);
            assertEquals(individualIds.length, emails.length);
            assertEquals(memberId.length, emails.length);
            assertEquals(preferredNames.length, emails.length);

            for (int i = 0; i < individualIds.length; i += 1) {
                assertEquals(members.get(i).getEmail(), emails[i]);
                assertEquals(members.get(i).getFullName(), fullNames[i]);
                assertEquals(members.get(i).getGivenName(), givenNames[i]);
                assertEquals(members.get(i).getIndividualId(), "" + individualIds[i]);
                assertEquals(members.get(i).getMemberId(), memberId[i]);
                assertEquals(members.get(i).getPreferredName(), preferredNames[i]);
                assertEquals(members.get(i).getSurname(), surname);
            }

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
