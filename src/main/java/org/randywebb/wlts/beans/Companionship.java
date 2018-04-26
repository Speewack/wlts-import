package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Companionship extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(Companionship.class);

	private List<Teacher> teachers = new ArrayList<Teacher>();
	private List<Assignment> assignments = new ArrayList<Assignment>();

	public static List<Companionship> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Companionship>(), Companionship.class);
	}

	public Companionship() {
	}

	public Companionship(JSONObject definition) {
		update(definition, new String[] {"id", "districtId", "startDate", "teachers", "assignments"});
	}

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

	public void setId(String id) {
		put("id", id);
	}

	public String getId() {
		return get("id");
	}

	public void setDistrictId(String districtId) {
		put("districtId", districtId);
	}

	public String getDistrictId() {
		return get("districtId");
	}

	public void setStartDate(String startDate) {
		put("startDate", startDate);
	}

	public String getStartDate() {
		return get("startDate");
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers.clear();
		addTeachers(teachers);
	}

	public void addTeachers(List<Teacher> teachers) {
		this.teachers.addAll(teachers);
	}

	public List<Teacher> getTeachers() {
		return Collections.unmodifiableList(teachers);
	}

	public void setAssignments(List<Assignment> assignments) {
		this.assignments.clear();
		addAssignments(assignments);
	}

	public void addAssignments(List<Assignment> assignments) {
		this.assignments.addAll(assignments);
	}

	public List<Assignment> getAssignments() {
		return Collections.unmodifiableList(assignments);
	}

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
