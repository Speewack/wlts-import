/**
 * 
 */
package org.randywebb.wlts.ldstools.json;

import java.util.List;

import org.json.simple.JSONObject;
import org.randywebb.wlts.beans.HouseholdMember;

/**
 * @author randyw
 *
 */
public class HouseholdMemberConsumer extends AbstractConsumer {

	/**
	 * 
	 */
	private List<HouseholdMember> members;

	public HouseholdMemberConsumer(List<HouseholdMember> members) {
		this.members = members;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(Object obj) {

		JSONObject jo = (JSONObject) obj;
		members.add(bindMember(jo));

	}

	public static HouseholdMember bindMember(JSONObject jo) {
		HouseholdMember member = null;

		if (jo != null) {
			member = new HouseholdMember();

			member.setEmail(convert(jo.get("email")));
			member.setFullName(convert(jo.get("fullName")));
			member.setGivenName(convert(jo.get("givenName")));
			member.setIndividualId(convert(jo.get("individualId")));
			member.setMemberId(convert(jo.get("memberId")));
			member.setPhone(convert(jo.get("phone")));
			member.setPreferredName(convert(jo.get("preferredName")));
			member.setSurname(convert(jo.get("surname")));
		}
		return member;
	}

}
