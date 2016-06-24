/**
 * 
 */
package org.randywebb.wlts.ldstools.json;

import java.util.ArrayList;
import java.util.List;

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

	public HouseholdConsumer(List<Household> households) {
		this.households = households;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;

		households.add(bindHousehold(jo));
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

			Address address = new Address();
			address.setLattitude(convert(jo.get("lattitude")));
			address.setLongitude(convert(jo.get("longitude")));
			address.setPostalCode(convert(jo.get("postalCode")));
			address.setState(convert(jo.get("state")));
			address.setStreetAddress(convert(jo.get("desc1") + ", " + convert(jo.get("desc2"))));

			household.setHouseholdAddress(address);

			household.setHeadOfHousehold(HouseholdMemberConsumer.bindMember((JSONObject) jo.get("headOfHouse")));
			household.setSpouse(HouseholdMemberConsumer.bindMember((JSONObject) jo.get("spouse")));

			JSONArray childrenJSON = (JSONArray) jo.get("children");

			if (childrenJSON != null) {
				List<HouseholdMember> children = new ArrayList<HouseholdMember>();
				HouseholdMemberConsumer action = new HouseholdMemberConsumer(children);

				childrenJSON.forEach(action);

				household.setChildren(children);
			}

		}

		return household;
	}

}