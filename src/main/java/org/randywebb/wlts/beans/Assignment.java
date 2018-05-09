package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Represents a ministering assignment. */
public class Assignment extends AbstractBean {

    /** In case we want to log something */
    //private static Logger log = LoggerFactory.getLogger(Assignment.class);

    /** List of visits to the assigned member(s). */
    private List<Visit> visits = new ArrayList<Visit>();

    /** Converts a JSON Array of assignments to a List of Assignment.
        @param array JSON Array of JSON assignment objects
        @return The Assignments from the JSON Array
    */
    public static List<Assignment> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<Assignment>(), Assignment.class);
    }

    /** Default Constructor. */
    public Assignment() {
    }

    /** Convert an assignment JSON Object to an Assignment.
        @param definition A JSON assignment Object
    */
    public Assignment(JSONObject definition) {
        update(definition, "id", "companionshipId", "individualId", "assignmentType", "visits");
    }

    /** Extract a specific key from an assignment JSON Object.
        We need to override since visits is not a string but an Array of Visits.
        For all other fields (simple strings), we just use the default behavior of AbstractBean.
        @param definition The JSON assignment object
        @param key The field of this Assignment to pull from definition
    */
    @Override
    protected void setFromJSON(JSONObject definition, String key) {

        if ("visits".equals(key)) {
            addVisits(Visit.fromArray((JSONArray) definition.get(key)));
        } else {
            super.setFromJSON(definition, key);
        }

    }

    /**
        @param id Set the id
    */
    public void setId(String id) {
        put("id", id);
    }

    /**
        @return the id
    */
    public String getId() {
        return get("id");
    }

    /**
        @param companionshipId Set the companionship id
    */
    public void setCompanionshipId(String companionshipId) {
        put("companionshipId", companionshipId);
    }

    /**
        @return the companionship id
    */
    public String getCompanionshipId() {
        return get("companionshipId");
    }

    /**
        @param individualId Set the individual id
    */
    public void setIndividualId(String individualId) {
        put("individualId", individualId);
    }

    /**
        @return the individual id
    */
    public String getIndividualId() {
        return get("individualId");
    }

    /**
        @param assignmentType Set the assignment type
    */
    public void setAssignmentType(String assignmentType) {
        put("assignmentType", assignmentType);
    }

    /**
        @return the assignment type
    */
    public String getAssignmentType() {
        return get("assignmentType");
    }

    /**
        @param visitsToSet Clears any existing visits and copies these
    */
    public void setVisits(List<Visit> visitsToSet) {
        visits.clear();
        addVisits(visitsToSet);
    }

    /**
        @param visitsToAdd Adds visits to existing visits
    */
    public void addVisits(List<Visit> visitsToAdd) {
        visits.addAll(visitsToAdd);
    }

    /**
        @return the list visists
    */
    public List<Visit> getVisits() {
        return Collections.unmodifiableList(visits);
    }

    /**
        @return A string representation of this Assignment, including visits
    */
    @Override
    public String toString() {
        String value = "";

        for (Visit visit : visits) {
            value += (value.length() == 0 ? "" : ", ") + visit.toString();
        }

        return "Assignment [" + super.toString() + ", visits = [" + value + "] ]";
    }

}
