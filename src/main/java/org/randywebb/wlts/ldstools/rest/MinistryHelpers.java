package org.randywebb.wlts.ldstools.rest;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/** Helper functions for ministry endpoints. */
public final class MinistryHelpers {

    /** Default constructor. */
    private MinistryHelpers() {
    }

    /** Get the list of ministering brothers and sisters lists.
        Get the contents of the app property ministering-members-endpoint.
        @param client The LDS client to use
        @param htIds List of htAuxiliaries from the endpoint.
        @param vtIds List of vtAuxiliaries from the endpoint.
        @throws IOException on io error
        @throws ParseException on JSON error
    */
    public static void getAuxiliaries(LdsToolsClient client, List<String> htIds,
                                List<String> vtIds) throws IOException, ParseException {
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

    /** Find htAuxiliaries and vtAuxiliaries in teacherAuxIds.
        @param person JSON object that represents a person from ministering-members-endpoint.
        @param htIds List of htAuxiliaries from the endpoint.
        @param vtIds List of vtAuxiliaries from the endpoint.
    */
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
