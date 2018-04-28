package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.randywebb.wlts.beans.AbstractBean;
import org.randywebb.wlts.beans.Companionship;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Represents a ministering district
public class District extends AbstractBean {

	/// To be used for debugging
	private static Logger log = LoggerFactory.getLogger(District.class);

	/// Companionships in the district
	private List<Companionship> companionships = new ArrayList<Companionship>();

	/** Converts a JSON Array of districts to a List of District.
		@param array JSON Array of JSON districts objects
		@return The Districts from the JSON Array
	*/
	public static List<District> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<District>(), District.class);
	}

	/// Default constructor
	public District() {
	}

	/** Convert a district JSON Object to an District.
		@param definition A JSON district Object
	*/
	public District(JSONObject definition) {
		update(definition, new String[] {"id", "auxiliaryId", "districtLeaderId", "districtLeaderIndividualId", "name", "companionships"});
	}

	/** Extract a specific key from a district JSON Object.
		We need to override since visits is not a string but an Array of Visits.
		For all other fields (simple strings), we just use the default behavior of AbstractBean.
		@param defintion The JSON assignment object
		@param key The field of this Assignment to pull from definition
	*/
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
