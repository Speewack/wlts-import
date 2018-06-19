package org.randywebb.wlts.beans;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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

class TestHousehold {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        Household household = new Household();
        HouseholdMember head = new HouseholdMember();
        HouseholdMember spouse = new HouseholdMember();
        Address address = new Address();

        address.setStreetAddress("sa");
        address.setUnitNumber("un");
        address.setCity("c");
        address.setState("s");
        address.setPostalCode("pc");
        address.setLongitude("lon");
        address.setLattitude("la");

        head.setFullName("head fn");
        head.setPreferredName("head pn");
        head.setMemberId("head mi");
        head.setIndividualId("head ii");
        head.setSurname("head sn");
        head.setGivenName("head gn");
        head.setPhone("head ph");
        head.setEmail("head em");

        spouse.setFullName("spouse fn");
        spouse.setPreferredName("spouse pn");
        spouse.setMemberId("spouse mi");
        spouse.setIndividualId("spouse ii");
        spouse.setSurname("spouse sn");
        spouse.setGivenName("spouse gn");
        spouse.setPhone("spouse ph");
        spouse.setEmail("spouse em");

        household.setHouseholdName("hhn");
        household.setHeadOfHousehold(head);
        household.setSpouse(spouse);
        household.setHouseholdAddress(address);
        household.setPhone("ph");
        household.setEmailAddress("em");
        household.setCoupleName("cn");
        household.setHeadOfHouseholdIndividualID("hhiid");
        household.setChildren(new ArrayList<HouseholdMember>(Arrays.asList(new HouseholdMember(), new HouseholdMember())));
        household.addChild(new HouseholdMember());

        assertNull(household.getMember("john"));
        assertEquals(household.toString(), "Household [coupleName = cn, emailAddress = em, headOfHouseIndividualId = hhiid, householdName = hhn, phone = ph, headOfHousehold = Member [email = head em, fullName = head fn, givenName = head gn, individualId = head ii, memberId = head mi, phone = head ph, preferredName = head pn, surname = head sn], spouse = Member [email = spouse em, fullName = spouse fn, givenName = spouse gn, individualId = spouse ii, memberId = spouse mi, phone = spouse ph, preferredName = spouse pn, surname = spouse sn], householdAddress = Address [city = c, includeLatLong = true, latitude = la, longitude = lon, postalCode = pc, state = s, streetAddress = sa, streetAddress2 = un], children = [Member [], Member [], Member []] ]");
    }

    @Test
    @DisplayName("Test Relocation")
    void testRelocation() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("households.json")));
            List<Household> households = Household.fromArray( (JSONArray) obj );
            JSONObject relocations = (JSONObject)new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("relocations.json")));
            Household nearest1 = households.get(0).nearest(households, relocations);
            Household nearest2 = households.get(1).nearest(households, relocations);
            Household nearest3 = households.get(3).nearest(households, relocations);
            Household furthest1 = households.get(0).furthest(households, relocations);
            Household furthest2 = households.get(1).furthest(households, relocations);
            Household furthest3 = households.get(3).furthest(households, relocations);
            Household empty = new Household();

            assertEquals(households.get(0).getCoupleName(), "AppleSeed, Sam & Sally");
            assertEquals(nearest1.getCoupleName(), "AppleSeed, Sam & Jane");
            assertEquals(furthest1.getCoupleName(), "AppleSeed, Sam");

            assertEquals(households.get(1).getCoupleName(), "AppleSeed, Sam");
            assertEquals(nearest2.getCoupleName(), "AppleSeed, Sam & Sally");
            assertEquals(furthest2.getCoupleName(), "AppleSeed, Sam & Jane");

            assertEquals(households.get(3).getCoupleName(), "AppleSeed, Sam & Jane");
            assertEquals(nearest3.getCoupleName(), "AppleSeed, Sam & Sally");
            assertEquals(furthest3.getCoupleName(), "AppleSeed, Sam");

            assertEquals(households.get(0).distance(households.get(0), null), 0.0);

            empty.setHouseholdAddress(new Address());
            assertEquals(households.get(0).distance(empty, null), 0.0);

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

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
