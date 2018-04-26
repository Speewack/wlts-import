package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.randywebb.wlts.beans.Companionship;
import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class District extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(District.class);

	private List<Companionship> companionships = new ArrayList<Companionship>();

	public static List<District> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<District>(), District.class);
	}

	public District() {
	}

	public District(JSONObject definition) {
		update(definition, new String[] {"id", "auxiliaryId", "districtLeaderId", "districtLeaderIndividualId", "name", "companionships"});
	}

	@Override
	protected void setFromJSON(JSONObject definition, String key) {

		if (key.equals("companionships")) {
			addCompanionships(Companionship.fromArray( (JSONArray) definition.get(key)));
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

	public void setAuxiliaryId(String auxiliaryId) {
		put("auxiliaryId", auxiliaryId);
	}

	public String getAuxiliaryId() {
		return get("auxiliaryId");
	}

	public void setDistrictLeaderId(String districtLeaderId) {
		put("districtLeaderId", districtLeaderId);
	}

	public String getDistrictLeaderId() {
		return get("districtLeaderId");
	}

	public void setDistrictLeaderIndividualId(String districtLeaderIndividualId) {
		put("districtLeaderIndividualId", districtLeaderIndividualId);
	}

	public String getDistrictLeaderIndividualId() {
		return get("districtLeaderIndividualId");
	}

	public void setName(String name) {
		put("name", name);
	}

	public String getName() {
		return get("name");
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

	@Override
	public String toString() {
		String value = "";

		for (Companionship companionship : companionships) {
			value += (value.length() == 0 ? "" : ", ") + companionship.toString();
		}

		return "District [" + super.toString() + ", companionships = [" + value + "] ]";
	}

}
