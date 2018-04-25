package org.randywebb.wlts.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Teacher {

	private static Logger log = LoggerFactory.getLogger(Teacher.class);

	private String id;
	private String companionshipId;
	private String individualId;

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

	@Override
	public String toString() {
		return "Teacher [id=" + id + ", companionshipId=" + companionshipId + ", individualId="
				+ individualId + "]";
	}

}
