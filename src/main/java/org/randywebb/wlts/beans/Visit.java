package org.randywebb.wlts.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visit {

	private static Logger log = LoggerFactory.getLogger(Visit.class);

	private String id;
	private String assignmentId;
	private String visited;
	private String year;
	private String month;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setVisited(String visited) {
		this.visited = visited;
	}

	public String getVisited() {
		return visited;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonth() {
		return month;
	}

}
