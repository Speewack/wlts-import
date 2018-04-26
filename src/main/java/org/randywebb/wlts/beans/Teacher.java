package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Teacher extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(Teacher.class);

	public static List<Teacher> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Teacher>(), Teacher.class);
	}

	public Teacher() {
	}

	public Teacher(JSONObject definition) {
		update(definition, new String[] {"id", "companionshipId", "individualId"});
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

	@Override
	public String toString() {
		return "Teacher [" + super.toString() + "]";
	}

}
