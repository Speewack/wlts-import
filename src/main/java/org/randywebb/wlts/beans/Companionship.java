package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.randywebb.wlts.beans.AbstractBean;
import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.Teacher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Represents a Companionship from a District
public class Companionship extends AbstractBean {

	/// In case we need to log anything, especially during debugging
	private static Logger log = LoggerFactory.getLogger(Companionship.class);

	/// List of Ministers in the companionship
	private List<Teacher> teachers = new ArrayList<Teacher>();

	/// List of families or individuals that are assigned to this companionship
	private List<Assignment> assignments = new ArrayList<Assignment>();

	/** Converts a JSON Array of companionships to a List of Companionship.
		@param array JSON Array of JSON companionship objects
		@return The Companionships from the JSON Array
	*/
	public static List<Companionship> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Companionship>(), Companionship.class);
	}

	/// Default constructor
	public Companionship() {
	}

	/** Convert an companionship JSON Object to an Companionship.
		@param definition A JSON companionship Object
	*/
	public Companionship(JSONObject definition) {
		update(definition, new String[] {"id", "districtId", "startDate", "teachers", "assignments"});
	}

	/** Extract a specific key from an companionship JSON Object.
		We need to override since teachers and assignments are not string but an Array of Teachers and Assignments.
		For all other fields (simple strings), we just use the default behavior of AbstractBean.
		@param definition The JSON companionship object
		@param key The field of this Companionship to pull from definition
	*/
	@Override
	protected void setFromJSON(JSONObject definition, String key) {

		if (key.equals("teachers")) {
			addTeachers(Teacher.fromArray( (JSONArray) definition.get(key)));
		} else if (key.equals("assignments")) {
			addAssignments(Assignment.fromArray( (JSONArray) definition.get(key)));
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
		@param teachers Set the list of teachers. Any existing teachers are removed first.
	*/
	public void setTeachers(List<Teacher> teachers) {
		this.teachers.clear();
		addTeachers(teachers);
	}

	/**
		@param teachers Adds teachers to existing teachers
	*/
	public void addTeachers(List<Teacher> teachers) {
		this.teachers.addAll(teachers);
	}

	/**
		@return the teachers
	*/
	public List<Teacher> getTeachers() {
		return Collections.unmodifiableList(teachers);
	}

	/**
		@param assignments Set the assignments. Clears any existing assignments first.
	*/
	public void setAssignments(List<Assignment> assignments) {
		this.assignments.clear();
		addAssignments(assignments);
	}

	/**
		@param assignments Adds assignments to existing assignments
	*/
	public void addAssignments(List<Assignment> assignments) {
		this.assignments.addAll(assignments);
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
		String value_teacher = "";
		String value_assignment = "";

		for (Teacher teacher : teachers) {
			value_teacher += (value_teacher.length() == 0 ? "" : ", ") + teacher.toString();
		}

		for (Assignment assignment : assignments) {
			value_assignment += (value_assignment.length() == 0 ? "" : ", ") + assignment.toString();
		}

		return "Companionship [" + super.toString() + ", teachers = [" + value_teacher + "], assignments = [" + value_assignment + "] ]";
	}

}
