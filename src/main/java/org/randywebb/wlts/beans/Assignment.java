package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Visit;
import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assignment extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(Assignment.class);

	private List<Visit> visits = new ArrayList<Visit>();

	public static List<Assignment> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Assignment>(), Assignment.class);
	}

	public Assignment() {
	}

	public Assignment(JSONObject definition) {
		String[] fields = {"id", "companionshipId", "individualId", "assignmentType", "visits"};

		update(definition, fields);
	}

	@Override
	protected void setFromJSON(JSONObject definition, String key) {

		if (key.equals("visits")) {
			addVisits(Visit.fromArray( (JSONArray) definition.get(key)));
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

	public void setCompanionshipId(String companionshipId) {
		put("companionshipId", companionshipId);
	}

	public String getCompanionshipId() {
		return get("companionshipId");
	}

	public void setIndividualId(String individualId) {
		put("individualId", individualId);
	}

	public String getIndividualId() {
		return get("individualId");
	}

	public void setAssignmentType(String assignmentType) {
		put("assignmentType", assignmentType);
	}

	public String getAssignmentType() {
		return get("assignmentType");
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
		String value = "";

		for (Visit visit : visits) {
			value += (value.length() == 0 ? "" : ", ") + visit.toString();
		}

		return "Assignment [" + super.toString() + ", visits = [" + value + "] ]";
	}

}
