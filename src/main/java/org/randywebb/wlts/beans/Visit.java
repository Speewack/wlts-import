package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Represents a ministering visit. */
public class Visit extends AbstractBean {

    /** Can be used for logging debugging messages */
    //private static Logger log = LoggerFactory.getLogger(Visit.class);

    /** Converts a JSON Array of visits to a List of Visit.
        @param array JSON Array of JSON visit objects
        @return The Visits from the JSON Array
    */
    public static List<Visit> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<Visit>(), Visit.class);
    }

    /** Default constructor. */
    public Visit() {
    }

    /** Convert a visit JSON Object to a Visit.
        @param definition A JSON visit Object
    */
    public Visit(JSONObject definition) {
        update(definition, "id", "assignmentId", "visited", "year", "month");
    }

    /**
        @param id The visit id
    */
    public void setId(String id) {
        put("id", id);
    }

    /**
        @return The visit id
    */
    public String getId() {
        return get("id");
    }

    /**
        @param assignmentId The assignment id
    */
    public void setAssignmentId(String assignmentId) {
        put("assignmentId", assignmentId);
    }

    /**
        @return The assignment id
    */
    public String getAssignmentId() {
        return get("assignmentId");
    }

    /**
        @param visited true or false, was this person visited
    */
    public void setVisited(String visited) {
        put("visited", visited);
    }

    /**
        @return true or false, was this person visited
    */
    public String getVisited() {
        return get("visited");
    }

    /**
        @param year The year of the visit
    */
    public void setYear(String year) {
        put("year", year);
    }

    /**
        @return The year of the visit
    */
    public String getYear() {
        return get("year");
    }

    /**
        @param month The month of the visit
    */
    public void setMonth(String month) {
        put("month", month);
    }

    /**
        @return The month of the visit
    */
    public String getMonth() {
        return get("month");
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Visit [" + super.toString() + "]";
    }

}
