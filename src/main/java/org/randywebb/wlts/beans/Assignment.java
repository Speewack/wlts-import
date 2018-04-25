package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Visit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assignment {

	private static Logger log = LoggerFactory.getLogger(Assignment.class);

	private String id;
	private String companionshipId;
	private String individualId;
	private String assignmentType;
	private List<Visit> visits = new ArrayList<Visit>();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setCompanionshipId(String companionshipId) {
		this.companionshipId = companionshipId;
	}

	public String getCompanionshipId() {
		return companionshipId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;
	}

	public String getIndividualId() {
		return individualId;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setVisits(List<Visit> visits) {
		this.visits.clear();
		addVisits(visits);
	}

	public void addVisits(List<Visit> visits) {
		this.visits.addAll(visits);
	}

	public List<Visit> getVisits() {
		return Collections.unmodifiableList(visits);
	}

	@Override
	public String toString() {
		return "Assignment [companionshipId=" + companionshipId + ", individualId=" + individualId + ", assignmentType="
				+ assignmentType + "]";
	}

}
