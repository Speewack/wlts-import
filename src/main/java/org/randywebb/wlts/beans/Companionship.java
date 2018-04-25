package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.beans.Assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Companionship {

	private static Logger log = LoggerFactory.getLogger(Companionship.class);

	private String id;
	private String districtId;
	private String startDate;
	private List<Teacher> teachers = new ArrayList<Teacher>();
	private List<Assignment> assignments = new ArrayList<Assignment>();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartDate() {
		return startDate;
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

}
