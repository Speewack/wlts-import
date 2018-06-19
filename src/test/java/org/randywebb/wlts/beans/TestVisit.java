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

class TestVisit {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        Visit visit = new Visit();

        visit.setId("id");
        visit.setAssignmentId("aid");
        visit.setVisited("v");
        visit.setYear("y");
        visit.setMonth("m");

        assertEquals(visit.toString(), "Visit [assignmentId = aid, id = id, month = m, visited = v, year = y]");
    }

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("visits.json")));
            List<Visit> visists = Visit.fromArray( (JSONArray) obj );
            assertEquals(visists.size(),13);
            int month = 5;

            for (int i = 0; i < 13; i += 1) {
                assertEquals(visists.get(i).getAssignmentId(), "33465");
                assertEquals(visists.get(i).getMonth(), "" + month);
                month = 12 == month ? 1 : month + 1;
            }

            for (int i = 0; i < 8; i += 1) {
                assertEquals(visists.get(i).getYear(), "2017");
            }

            for (int i = 8; i < 13; i += 1) {
                assertEquals(visists.get(i).getYear(), "2018");
            }

            assertEquals(visists.get(0).getId(), "81159");
            assertEquals(visists.get(1).getId(), "83780");
            assertEquals(visists.get(2).getId(), "87179");
            assertEquals(visists.get(3).getId(), "89183");
            assertEquals(visists.get(4).getId(), "92259");
            assertEquals(visists.get(5).getId(), "95402");
            assertEquals(visists.get(6).getId(), "96727");
            assertEquals(visists.get(7).getId(), "99384");
            assertEquals(visists.get(8).getId(), "103675");
            assertEquals(visists.get(9).getId(), "106625");
            assertNull(visists.get(10).getId());
            assertNull(visists.get(11).getId());
            assertNull(visists.get(12).getId());

            assertTrue(visists.get(0).getBoolean("visited"));
            assertTrue(visists.get(1).getBoolean("visited"));
            assertTrue(visists.get(2).getBoolean("visited"));
            assertTrue(visists.get(3).getBoolean("visited"));
            assertFalse(visists.get(4).getBoolean("visited"));
            assertFalse(visists.get(5).getBoolean("visited"));
            assertTrue(visists.get(6).getBoolean("visited"));
            assertTrue(visists.get(7).getBoolean("visited"));
            assertTrue(visists.get(8).getBoolean("visited"));
            assertTrue(visists.get(9).getBoolean("visited"));
            assertNull(visists.get(10).getVisited());
            assertNull(visists.get(11).getVisited());
            assertNull(visists.get(12).getVisited());

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
