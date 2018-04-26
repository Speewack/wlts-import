/**
 *
 */
package org.randywebb.wlts.ldstools.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.randywebb.wlts.beans.Address;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.HouseholdMember;

/**
 * @author randyw
 *
 */
public class HouseholdConsumer extends AbstractConsumer {

	private List<Household> households;
	private Map<String,Household> idToHousehold;

	public HouseholdConsumer(List<Household> households) {
		this.households = households;
		this.idToHousehold = null;
	}

	public HouseholdConsumer(Map<String,Household> households) {
		this.households = null;
		this.idToHousehold = households;
	}

	public HouseholdConsumer(List<Household> households, Map<String,Household> householdMap) {
		this.households = households;
		this.idToHousehold = householdMap;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		Household household = bindHousehold(jo);

		if (null != households) {
			households.add(household);
		}
		if (null != idToHousehold) {
			for (String memberId : household.getIndividualIds()) {
				idToHousehold.put(memberId, household);
			}
		}
	}

	public static Household bindHousehold(JSONObject jo) {

		Household household = null;

		if (jo != null) {
			household = new Household();

			household.setCoupleName(convert(jo.get("coupleName")));
			household.setEmailAddress(convert(jo.get("emailAddress")));
			household.setHouseholdName(convert(jo.get("householdName")));
			household.setPhone(convert(jo.get("phone")));
			household.setHeadOfHouseholdIndividualID(convert(jo.get("headOfHouseholdIndividualId")));
			household.setHouseholdAddress(new Address(jo));
			household.setHeadOfHousehold((null == jo.get("headOfHouse")) ? null : new HouseholdMember((JSONObject) jo.get("headOfHouse")));
			household.setSpouse((null == jo.get("spouse")) ? null : new HouseholdMember((JSONObject) jo.get("spouse")));
			household.setChildren(HouseholdMember.fromArray((JSONArray) jo.get("children")));

		}

		return household;
	}

}
