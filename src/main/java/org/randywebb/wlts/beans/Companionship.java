package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Represents a Companionship from a District. */
public class Companionship extends AbstractBean {

    // In case we need to log anything, especially during debugging.
    //private static Logger log = LoggerFactory.getLogger(Companionship.class);

    /** List of Ministers in the companionship. */
    private List<Teacher> teachers = new ArrayList<Teacher>();

    /** List of families or individuals that are assigned to this companionship. */
    private List<Assignment> assignments = new ArrayList<Assignment>();

    /** Converts a JSON Array of companionships to a List of Companionship.
        @param array JSON Array of JSON companionship objects
        @return The Companionships from the JSON Array
    */
    public static List<Companionship> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<Companionship>(), Companionship.class);
    }

    /** Default constructor. */
    public Companionship() {
    }

    /** Convert an companionship JSON Object to an Companionship.
        @param definition A JSON companionship Object
    */
    public Companionship(JSONObject definition) {
        update(definition, "id", "districtId", "startDate", "teachers", "assignments");
    }

    /** Extract a specific key from an companionship JSON Object.
        We need to override since teachers and assignments are not string
            but an Array of Teachers and Assignments.
        For all other fields (simple strings), we just use the default behavior of AbstractBean.
        @param definition The JSON companionship object
        @param key The field of this Companionship to pull from definition
    */
    @Override
    protected void setFromJSON(JSONObject definition, String key) {

        if ("teachers".equals(key)) {
            addTeachers(Teacher.fromArray((JSONArray) definition.get(key)));
        } else if ("assignments".equals(key)) {
            addAssignments(Assignment.fromArray((JSONArray) definition.get(key)));
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
        @param districtId Set the district id
    */
    public void setDistrictId(String districtId) {
        put("districtId", districtId);
    }

    /**
        @return the district id
    */
    public String getDistrictId() {
        return get("districtId");
    }

    /**
        @param startDate Set the start date for the companionship
    */
    public void setStartDate(String startDate) {
        put("startDate", startDate);
    }

    /**
        @return the start date for the companionship
    */
    public String getStartDate() {
        return get("startDate");
    }

    /**
        @param teachersToSet Set the list of teachers. Any existing teachers are removed first.
    */
    public void setTeachers(List<Teacher> teachersToSet) {
        teachers.clear();
        addTeachers(teachersToSet);
    }

    /**
        @param teachersToAdd Adds teachers to existing teachers
    */
    public void addTeachers(List<Teacher> teachersToAdd) {
        teachers.addAll(teachersToAdd);
    }

    /**
        @return the teachers
    */
    public List<Teacher> getTeachers() {
        return Collections.unmodifiableList(teachers);
    }

    /**
        @param assignmentsToSet Set the assignments. Clears any existing assignments first.
    */
    public void setAssignments(List<Assignment> assignmentsToSet) {
        assignments.clear();
        addAssignments(assignmentsToSet);
    }

    /**
        @param assignmentsToAdd Adds assignments to existing assignments
    */
    public void addAssignments(List<Assignment> assignmentsToAdd) {
        assignments.addAll(assignmentsToAdd);
    }

    /**
        @return the assignments
    */
    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    /**
        @return A string representation of this Companionship, including teachers and assignments
    */
    @Override
    public String toString() {
        String valueTeacher = "";
        String valueAssignment = "";

        for (Teacher teacher : teachers) {
            valueTeacher += (valueTeacher.length() == 0) ? "" : ", ";
            valueTeacher += teacher.toString();
        }

        for (Assignment assignment : assignments) {
            valueAssignment += (valueAssignment.length() == 0) ? "" : ", ";
            valueAssignment += assignment.toString();
        }

        return "Companionship ["
                + super.toString()
                + ", teachers = [" + valueTeacher + "]"
                + ", assignments = [" + valueAssignment + "] ]";
    }

}
