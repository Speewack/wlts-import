package org.randywebb.wlts.ldstools.rest;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class MinistryHelpers {

  public static void getAuxiliaries(LdsToolsClient client, List<String> htIds, List<String> vtIds) throws IOException, ParseException {
	JSONObject members = client.getAppPropertyEndpointInfo("ministering-members-endpoint");
	JSONArray families = (JSONArray) members.get("families");

	for (Object familyObject : families) {
		JSONObject family = (JSONObject) familyObject;
		JSONObject spouse = (JSONObject) family.get("spouse");
		JSONArray children = (JSONArray) family.get("children");

		addAuxiliaries((JSONObject) family.get("headOfHouse"), htIds, vtIds);

		if (null != spouse) {
			addAuxiliaries(spouse, htIds, vtIds);
		}

		if (null != children) {
			for (Object childObject : children) {
				addAuxiliaries((JSONObject) childObject, htIds, vtIds);
			}
		}

	}
  }

  private static void addAuxiliaries(JSONObject person, List<String> htIds, List<String> vtIds) {
	JSONObject teachers = (JSONObject) person.get("teacherAuxIds");
	JSONArray ht = (JSONArray) teachers.get("htAuxiliaries");
	JSONArray vt = (JSONArray) teachers.get("vtAuxiliaries");

	if (null != ht) {
		for (Object ministerObject : ht) {
			String value = Long.toString((Long) ministerObject);

			if (!htIds.contains(value)) {
				htIds.add(value);
			}
		}
	}

	if (null != vt) {
		for (Object ministerObject : vt) {
			String value = Long.toString((Long) ministerObject);

			if (!vtIds.contains(value)) {
				vtIds.add(value);
			}
		}
	}

  }

}
