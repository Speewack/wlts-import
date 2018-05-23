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
    @DisplayName("Test JSON Parsing")
    void testAddressJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("visits.json")));
            List<Visit> visists = Visit.fromArray( (JSONArray) obj );
            assertEquals(visists.size(),13);
            int month = 5;

            for (int i = 0; i < 12; i += 1) {
                assertEquals(visists.get(i).getInteger("assignmentId").intValue(), 33465);
                assertEquals(visists.get(i).getInteger("month").intValue(), month);
                month = 12 == month ? 1 : month + 1;
            }

            for (int i = 0; i < 8; i += 1) {
                assertEquals(visists.get(i).getInteger("year").intValue(), 2017);
            }

            for (int i = 8; i < 12; i += 1) {
                assertEquals(visists.get(i).getInteger("year").intValue(), 2018);
            }

            assertEquals(visists.get(0).getInteger("id").intValue(), 81159);
            assertEquals(visists.get(1).getInteger("id").intValue(), 83780);
            assertEquals(visists.get(2).getInteger("id").intValue(), 87179);
            assertEquals(visists.get(3).getInteger("id").intValue(), 89183);
            assertEquals(visists.get(4).getInteger("id").intValue(), 92259);
            assertEquals(visists.get(5).getInteger("id").intValue(), 95402);
            assertEquals(visists.get(6).getInteger("id").intValue(), 96727);
            assertEquals(visists.get(7).getInteger("id").intValue(), 99384);
            assertEquals(visists.get(8).getInteger("id").intValue(), 103675);
            assertEquals(visists.get(9).getInteger("id").intValue(), 106625);
            assertNull(visists.get(10).getInteger("id"));
            assertNull(visists.get(11).getInteger("id"));
            assertNull(visists.get(12).getInteger("id"));

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
            assertNull(visists.get(10).getBoolean("visited"));
            assertNull(visists.get(11).getBoolean("visited"));
            assertNull(visists.get(12).getBoolean("visited"));

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
