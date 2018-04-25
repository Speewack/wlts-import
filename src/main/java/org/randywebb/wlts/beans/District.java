package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Companionship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class District {

	private static Logger log = LoggerFactory.getLogger(District.class);

	private String id;
	private String auxiliaryId;
	private String districtLeaderId;
	private String districtLeaderIndividualId;
	private String name;
	private List<Companionship> companionships = new ArrayList<Companionship>();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setAuxiliaryId(String auxiliaryId) {
		this.auxiliaryId = auxiliaryId;
	}

	public String getAuxiliaryId() {
		return auxiliaryId;
	}

	public void setDistrictLeaderId(String districtLeaderId) {
		this.districtLeaderId = districtLeaderId;
	}

	public String getDistrictLeaderId() {
		return districtLeaderId;
	}

	public void setDistrictLeaderIndividualId(String districtLeaderIndividualId) {
		this.districtLeaderIndividualId = districtLeaderIndividualId;
	}

	public String getDistrictLeaderIndividualId() {
		return districtLeaderIndividualId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCompanionships(List<Companionship> companionships) {
		this.companionships.clear();
		addCompanionships(companionships);
	}

	public void addCompanionships(List<Companionship> companionships) {
		this.companionships.addAll(companionships);
	}

	public List<Companionship> getCompanionships() {
		return Collections.unmodifiableList(companionships);
	}

}
