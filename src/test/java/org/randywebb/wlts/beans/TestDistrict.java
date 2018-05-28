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

class TestDistrict {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        District district = new District();

        district.setId("id");
        district.setAuxiliaryId("aid");
        district.setDistrictLeaderId("dlid");
        district.setDistrictLeaderIndividualId("dliid");
        district.setName("n");

        assertEquals(district.toString(), "District [auxiliaryId = aid, districtLeaderId = dlid, districtLeaderIndividualId = dliid, id = id, name = n, companionships = [] ]");

    }

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("districts.json")));
            List<District> districts = District.fromArray( (JSONArray) obj );
            long auxiliaryId = 9955389L;
            long districtLeaderIds[] = {99149899L, 99149898L, 99149896L, 99048696L};
            long ids[] = {9965290L, 9965289L, 9965309L, 9990178L};
            String names[] = {"Central", "North West", "South East", "Unassigned"};
            int companionshipCounts[] = {12, 12, 13, 6};

            assertEquals(districts.size(), ids.length);
            assertEquals(districtLeaderIds.length, ids.length);
            assertEquals(names.length, ids.length);
            assertEquals(companionshipCounts.length, ids.length);

            for (int i = 0; i < ids.length; i += 1) {
                District district = districts.get(i);

                assertEquals(district.getAuxiliaryId(), "" + auxiliaryId);
                assertEquals(district.getId(), "" + ids[i]);
                assertEquals(district.getDistrictLeaderId(), "" + districtLeaderIds[i]);
                assertEquals(district.getName(), names[i]);
                assertEquals(district.getCompanionships().size(), companionshipCounts[i]);
                assertNull(district.getDistrictLeaderIndividualId());
            }

            // companionships and their children are tested in their own tests

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
