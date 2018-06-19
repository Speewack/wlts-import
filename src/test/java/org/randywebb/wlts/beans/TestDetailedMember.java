package org.randywebb.wlts.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestDetailedMember {

    @Test
    @DisplayName("Test JSON Parsing")
    void testJSON() {
	// Just verify that the parser is working at a basic level
	try {
	    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("detailedMember.json");
	    assertNotNull(in, "Unable to read json file");
	    InputStreamReader reader = new InputStreamReader(in);
	    JSONArray testData = (JSONArray) new JSONParser().parse(reader);

	    List<DetailedMember> members = DetailedMember.fromArray(testData);
	    assertFalse(null==members, "Members List is NULL");
	    assertFalse(members.isEmpty(), "Members List is Empty");
	    assertEquals(4, members.size(), "Members list is wrong size");

	} catch (IOException | ParseException e) {
	    fail("Failed to parse DetailedMember.json", e);
	}
	catch (NullPointerException e) {
	    fail("Failed to parse DetailedMember.json", e);
	}
    }
}
