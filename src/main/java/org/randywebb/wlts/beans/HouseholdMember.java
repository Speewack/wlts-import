package org.randywebb.wlts.beans;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HouseholdMember extends AbstractBean {
	private static Logger log = LoggerFactory.getLogger(HouseholdMember.class);

	private String fullName;
	private String preferredName;
	private String memberId;
	private String individualId;
	private String surname;
	private String givenName;
	private String phone;
	private String email;

	public static List<HouseholdMember> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<HouseholdMember>(), HouseholdMember.class);
	}

	public HouseholdMember() {
	}

	public HouseholdMember(JSONObject definition) {
		update(definition, new String[] {"fullName", "preferredName", "memberId", "individualId", "surname", "givenName", "phone", "email"});
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return get("fullName");
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		put("fullName", fullName);
	}
	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return get("preferredName");
	}
	/**
	 * @param preferredName the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		put("preferredName", preferredName);
	}
	/**
	 * @return the memberId
	 */
	public String getMemberId() {
		return get("memberId");
	}
	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(String memberId) {
		put("memberId", memberId);
	}
	/**
	 * @return the individualId
	 */
	public String getIndividualId() {
		return get("individualId");
	}
	/**
	 * @param individualId the individualId to set
	 */
	public void setIndividualId(String individualId) {
		put("individualId", individualId);
	}
	/**
	 * @return the surname
	 */
	public String getSurname() {
		return get("surname");
	}
	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		put("surname", surname);
	}
	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return get("givenName");
	}
	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName) {
		put("givenName", givenName);
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return get("phone");
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		put("phone", phone);
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return get("email");
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		put("email", email);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Member [" + super.toString() + "]";
	}


}
