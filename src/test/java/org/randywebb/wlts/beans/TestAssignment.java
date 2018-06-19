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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TestAssignment {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        Assignment assignment = new Assignment();

        assignment.setId("id");
        assignment.setCompanionshipId("cid");
        assignment.setIndividualId("iid");
        assignment.setAssignmentType("at");

        assignment.setVisits(new ArrayList<Visit>(Arrays.asList(new Visit(), new Visit())));

        assertEquals(assignment.toString(), "Assignment [assignmentType = at, companionshipId = cid, id = id, individualId = iid, visits = [Visit [], Visit []] ]");
    }

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("assignments.json")));
            List<Assignment> assignments = Assignment.fromArray( (JSONArray) obj );
            int assignmentIds[] = {33443, 33409, 33440, 33444};

            assertEquals(assignments.size(), assignmentIds.length);

            for (int j = 0; j < assignmentIds.length; j += 1) {
                Assignment assignment = assignments.get(j);
                List<Visit> visits = assignment.getVisits();
                int month = 5;

                for (int i = 0; i < 13; i += 1) {
                    assertEquals(visits.get(i).getAssignmentId(), "" + assignmentIds[j]);
                    assertEquals(visits.get(i).getMonth(), "" + month);
                    month = 12 == month ? 1 : month + 1;
                }

                for (int i = 0; i < 8; i += 1) {
                    assertEquals(visits.get(i).getYear(), "2017");
                }

                for (int i = 8; i < 13; i += 1) {
                    assertEquals(visits.get(i).getYear(), "2018");
                }

                for (int i = 10; i < 13; i += 1) {
                    assertNull(visits.get(i).getId());
                    assertNull(visits.get(i).getVisited());
                }

                assertEquals(assignment.getAssignmentType(), "HT");
            }

            int visitIds0[] = {811650, 837712, 871968, 891413, 922929, 954194, 967221, 993791, 1031876, 1064296};
            boolean visited0[] = {true, true, true, true, false, false, true, true, true, true};
            List<Visit> visits0 = assignments.get(0).getVisits();

            assertEquals(visitIds0.length, visited0.length);
            assertEquals(assignments.get(0).getId(), "33443");
            assertEquals(assignments.get(0).getCompanionshipId(), "19604");
            assertEquals(assignments.get(0).getIndividualId(), "10555886");
            for (int i = 0; i < visitIds0.length; i += 1) {
                assertEquals(visits0.get(i).getInteger("id").intValue(), visitIds0[i]);
                if (visited0[i]) {
                    assertTrue(visits0.get(i).getBoolean("visited"));
                } else {
                    assertFalse(visits0.get(i).getBoolean("visited"));
                }
            }

            int visitIds1[] = {841356, 841356, 867512, 893625, 922929, 954193, 972883, 1013759, 1031876, 1064296};
            boolean visited1[] = {false, false, true, true, true, false, true, true, true, true};
            List<Visit> visits1 = assignments.get(1).getVisits();

            assertEquals(visitIds1.length, visited1.length);
            assertEquals(assignments.get(1).getId(), "33409");
            assertEquals(assignments.get(1).getCompanionshipId(), "19604");
            assertEquals(assignments.get(1).getIndividualId(), "28957833");
            for (int i = 0; i < visitIds1.length; i += 1) {
                assertEquals(visits1.get(i).getId(), "" + visitIds1[i]);
                if (visited1[i]) {
                    assertTrue(visits1.get(i).getBoolean("visited"));
                } else {
                    assertFalse(visits1.get(i).getBoolean("visited"));
                }
            }

            int visitIds2[] = {841356, 841356, 867512, 893625, 922929, 954193, 972882, 1013759, 1031876, 1064296};
            boolean visited2[] = {false, false, true, true, true, false, true, true, true, true};
            List<Visit> visits2 = assignments.get(2).getVisits();

            assertEquals(visitIds2.length, visited2.length);
            assertEquals(assignments.get(2).getId(), "33440");
            assertEquals(assignments.get(2).getCompanionshipId(), "19604");
            assertEquals(assignments.get(2).getIndividualId(), "28992464");
            for (int i = 0; i < visitIds2.length; i += 1) {
                assertEquals(visits2.get(i).getId(), "" + visitIds2[i]);
                if (visited2[i]) {
                    assertTrue(visits2.get(i).getBoolean("visited"));
                } else {
                    assertFalse(visits2.get(i).getBoolean("visited"));
                }
            }

            int visitIds3[] = {811650, 837712, 867512, 893625, 922929, 954193, 972882, 993791, 1031876, 1064296};
            boolean visited3[] = {true, true, true, true, true, false, true, true, true, true};
            List<Visit> visits3 = assignments.get(3).getVisits();

            assertEquals(visitIds3.length, visited3.length);
            assertEquals(assignments.get(3).getId(), "33444");
            assertEquals(assignments.get(3).getCompanionshipId(), "19604");
            assertEquals(assignments.get(3).getIndividualId(), "16327354");
            for (int i = 0; i < visitIds3.length; i += 1) {
                assertEquals(visits3.get(i).getId(), "" + visitIds3[i]);
                if (visited3[i]) {
                    assertTrue(visits3.get(i).getBoolean("visited"));
                } else {
                    assertFalse(visits3.get(i).getBoolean("visited"));
                }
            }

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
