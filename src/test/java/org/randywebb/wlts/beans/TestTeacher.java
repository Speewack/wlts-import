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

class TestTeacher {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        Teacher teacher = new Teacher();

        teacher.setId("id");
        teacher.setCompanionshipId("cid");
        teacher.setIndividualId("iid");

        assertEquals(teacher.toString(), "Teacher [companionshipId = cid, id = id, individualId = iid]");

    }

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("teachers.json")));
            List<Teacher> teachers = Teacher.fromArray( (JSONArray) obj );

            assertEquals(teachers.size(), 8);
            int ids[] = {85354, 85355, 85356, 85357, 85341, 85342, 85392, 85505};
            int companionshipIds[] = {60466, 60466, 60467, 60467, 60487, 60487, 60498, 60498};
            long individualIds[] = {96905913L, 24930666L, 50848238L, 2977130L, 55588652L, 74390700L, 36357637L, 91205312L};

            assertEquals(ids.length, companionshipIds.length);
            assertEquals(ids.length, individualIds.length);

            for (int i = 0; i < ids.length; i += 1) {
                assertEquals(teachers.get(i).getId(), "" + ids[i]);
                assertEquals(teachers.get(i).getCompanionshipId(), "" + companionshipIds[i]);
                assertEquals(teachers.get(i).getIndividualId(), "" + individualIds[i]);
            }

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
