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

class TestAssignment {

    @Test
    @DisplayName("Test JSON Parsing")
    void testAddressJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("assignments.json")));
            List<Assignment> assignments = Assignment.fromArray( (JSONArray) obj );
            int assignmentIds[] = {33443, 33409, 33440, 33444};

            assertEquals(assignments.size(), assignmentIds.length);

            for (int j = 0; j < assignmentIds.length; j += 1) {
                Assignment assignment = assignments.get(j);
                List<Visit> visits = assignment.getVisits();
                int month = 5;

                for (int i = 0; i < 12; i += 1) {
                    assertEquals(visits.get(i).getInteger("assignmentId").intValue(), assignmentIds[j]);
                    assertEquals(visits.get(i).getInteger("month").intValue(), month);
                    month = 12 == month ? 1 : month + 1;
                }

                for (int i = 0; i < 8; i += 1) {
                    assertEquals(visits.get(i).getInteger("year").intValue(), 2017);
                }

                for (int i = 8; i < 12; i += 1) {
                    assertEquals(visits.get(i).getInteger("year").intValue(), 2018);
                }

                assertEquals(assignment.get("assignmentType"), "HT");
            }

            assertEquals(assignments.get(0).getInteger("id").intValue(), 33443);
            assertEquals(assignments.get(0).getInteger("companionshipId").intValue(), 19604);
            assertEquals(assignments.get(0).getInteger("individualId").intValue(), 10555886);

            assertEquals(assignments.get(1).getInteger("id").intValue(), 33409);
            assertEquals(assignments.get(1).getInteger("companionshipId").intValue(), 19604);
            assertEquals(assignments.get(1).getInteger("individualId").intValue(), 28957833);

            assertEquals(assignments.get(2).getInteger("id").intValue(), 33440);
            assertEquals(assignments.get(2).getInteger("companionshipId").intValue(), 19604);
            assertEquals(assignments.get(2).getInteger("individualId").intValue(), 28992464);

            assertEquals(assignments.get(3).getInteger("id").intValue(), 33444);
            assertEquals(assignments.get(3).getInteger("companionshipId").intValue(), 19604);
            assertEquals(assignments.get(3).getInteger("individualId").intValue(), 16327354);

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
