package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visit extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(Visit.class);

	public static List<Visit> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Visit>(), Visit.class);
	}

	public Visit() {
	}

	public Visit(JSONObject definition) {
		update(definition, new String[] {"id", "assignmentId", "visited", "year", "month"});
	}

	public void setId(String id) {
		put("id", id);
	}

	public String getId() {
		return get("id");
	}

	public void setAssignmentId(String assignmentId) {
		put("assignmentId", assignmentId);
	}

	public String getAssignmentId() {
		return get("assignmentId");
	}

	public void setVisited(String visited) {
		put("visited", visited);
	}

	public String getVisited() {
		return get("visited");
	}

	public void setYear(String year) {
		put("year", year);
	}

	public String getYear() {
		return get("year");
	}

	public void setMonth(String month) {
		put("month", month);
	}

	public String getMonth() {
		return get("month");
	}

	@Override
	public String toString() {
		return "Visit [" + super.toString() + "]";
	}

}
