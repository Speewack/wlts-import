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

class TestCompanionship {

    @Test
    @DisplayName("Test JSON Parsing")
    void testAddressJSON() {
        try {
            Object obj = new JSONParser().parse(new InputStreamReader(ClassLoader.getSystemResourceAsStream("companionships.json")));
            List<Companionship> companionships = Companionship.fromArray( (JSONArray) obj );
            long ids[] = {9955936L, 9955937L, 9962044L, 9962108L, 9997163L, 9997171L, 9973146L, 9960418L};
            long districtIds[] = {9990178L, 9990178L, 9990178L, 9990178L, 9990178L, 9990178L, 9965289L, 9965289L};
            long startDates[] = {1418175119955L, 1418175239331L, 1418413012520L, 1418418036701L, 1420606243684L, 1420606216076L, 1470972058559L, 1470277682751L};

            assertEquals(companionships.size(), ids.length);
            assertEquals(districtIds.length, ids.length);
            assertEquals(startDates.length, ids.length);

            for (int i = 0; i < ids.length; i += 1) {
                assertEquals(companionships.get(i).getLong("id").longValue(), ids[i]);
                assertEquals(companionships.get(i).getLong("districtId").longValue(), districtIds[i]);
                assertEquals(companionships.get(i).getLong("startDate").longValue(), startDates[i]);
            }

            assertEquals(companionships.get(0).getTeachers().size(), 0);
            assertEquals(companionships.get(0).getAssignments().size(), 0);

            assertEquals(companionships.get(1).getTeachers().size(), 1);
            assertEquals(companionships.get(1).getTeachers().get(0).getLong("id").longValue(), 9936118L);
            assertEquals(companionships.get(1).getTeachers().get(0).getLong("companionshipId").longValue(), 9955937L);
            assertEquals(companionships.get(1).getTeachers().get(0).getLong("individualId").longValue(), 99908767812L);
            assertEquals(companionships.get(1).getAssignments().size(), 0);

            assertEquals(companionships.get(2).getTeachers().size(), 1);
            assertEquals(companionships.get(2).getTeachers().get(0).getLong("id").longValue(), 9970925L);
            assertEquals(companionships.get(2).getTeachers().get(0).getLong("companionshipId").longValue(), 9962044L);
            assertEquals(companionships.get(2).getTeachers().get(0).getLong("individualId").longValue(), 99058187748L);
            assertEquals(companionships.get(2).getAssignments().size(), 0);

            assertEquals(companionships.get(3).getTeachers().size(), 1);
            assertEquals(companionships.get(3).getTeachers().get(0).getLong("id").longValue(), 9957519L);
            assertEquals(companionships.get(3).getTeachers().get(0).getLong("companionshipId").longValue(), 9962108L);
            assertEquals(companionships.get(3).getTeachers().get(0).getLong("individualId").longValue(), 99069973910L);
            assertEquals(companionships.get(3).getAssignments().size(), 0);

            assertEquals(companionships.get(4).getTeachers().size(), 1);
            assertEquals(companionships.get(4).getTeachers().get(0).getLong("id").longValue(), 9970926L);
            assertEquals(companionships.get(4).getTeachers().get(0).getLong("companionshipId").longValue(), 9997163L);
            assertEquals(companionships.get(4).getTeachers().get(0).getLong("individualId").longValue(), 9995783329L);
            assertEquals(companionships.get(4).getAssignments().size(), 0);

            assertEquals(companionships.get(5).getTeachers().size(), 1);
            assertEquals(companionships.get(5).getTeachers().get(0).getLong("id").longValue(), 9970917L);
            assertEquals(companionships.get(5).getTeachers().get(0).getLong("companionshipId").longValue(), 9997171L);
            assertEquals(companionships.get(5).getTeachers().get(0).getLong("individualId").longValue(), 996253258L);
            assertEquals(companionships.get(5).getAssignments().size(), 0);

            assertEquals(companionships.get(6).getTeachers().size(), 1);
            assertEquals(companionships.get(6).getTeachers().get(0).getLong("id").longValue(), 9934585L);
            assertEquals(companionships.get(6).getTeachers().get(0).getLong("companionshipId").longValue(), 9973146L);
            assertEquals(companionships.get(6).getTeachers().get(0).getLong("individualId").longValue(), 9931140464L);
            assertEquals(companionships.get(6).getAssignments().size(), 0);

            assertEquals(companionships.get(7).getTeachers().size(), 2);
            assertEquals(companionships.get(7).getTeachers().get(0).getLong("id").longValue(), 9985403L);
            assertEquals(companionships.get(7).getTeachers().get(0).getLong("companionshipId").longValue(), 9960418L);
            assertEquals(companionships.get(7).getTeachers().get(0).getLong("individualId").longValue(), 9932901812L);
            assertEquals(companionships.get(7).getTeachers().get(1).getLong("id").longValue(), 9985404L);
            assertEquals(companionships.get(7).getTeachers().get(1).getLong("companionshipId").longValue(), 9960418L);
            assertEquals(companionships.get(7).getTeachers().get(1).getLong("individualId").longValue(), 9917406968L);
            assertEquals(companionships.get(7).getAssignments().size(), 2);

        } catch(IOException | ParseException e) {
            e.printStackTrace();
            fail("Exception parsing Address JSON: " + e.getMessage());
        }
    }

}
