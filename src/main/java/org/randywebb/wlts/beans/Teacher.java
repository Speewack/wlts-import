package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Represents a minister (previously home or visiting teacher) */
public class Teacher extends AbstractBean {

	/** Can be used for logging debugging messages */
	//private static Logger log = LoggerFactory.getLogger(Teacher.class);

	/** Converts a JSON Array of ministers to a List of Teacher.
		@param array JSON Array of JSON household objects
		@return The Teachers from the JSON Array
	*/
	public static List<Teacher> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Teacher>(), Teacher.class);
	}

	/** Default constructor */
	public Teacher() {
	}

	/** Convert a minister JSON Object to a Teacher.
		@param definition A JSON minister Object
	*/
	public Teacher(JSONObject definition) {
		update(definition, new String[] {"id", "companionshipId", "individualId"});
	}

	/**
		@param id The teacher id
	*/
	public void setId(String id) {
		put("id", id);
	}

	/**
		@return The teacher id
	*/
	public String getId() {
		return get("id");
	}

	/**
		@param companionshipId The id for the companionship (referenced from other objects)
	*/
	public void setCompanionshipId(String companionshipId) {
		put("companionshipId", companionshipId);
	}

	/**
		@return The id for the companionship (referenced from other objects)
	*/
	public String getCompanionshipId() {
		return get("companionshipId");
	}

	/**
		@param individualId The db id for the teacher
	*/
	public void setIndividualId(String individualId) {
		put("individualId", individualId);
	}

	/**
		@return The db id for the teacher
	*/
	public String getIndividualId() {
		return get("individualId");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Teacher [" + super.toString() + "]";
	}

}
