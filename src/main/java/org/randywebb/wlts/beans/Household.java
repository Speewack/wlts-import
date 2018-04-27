/**
 *
 */
package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.randywebb.wlts.beans.HouseholdMember;
import org.randywebb.wlts.beans.Address;
import org.randywebb.wlts.beans.AbstractBean;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
/**
 * @author randyw
 *
 */
public class Household extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(Household.class);

	private HouseholdMember headOfHousehold;
	private HouseholdMember spouse;
	private List<HouseholdMember> children = new ArrayList<HouseholdMember>();
	private Address householdAddress;

	public static List<Household> fromArray(JSONArray array) {
		return fromArray(array, new ArrayList<Household>(), Household.class);
	}

	public Household() {
	}

	public Household(JSONObject definition) {
		update(definition, new String[] {"householdName", "headOfHouse", "spouse", "children", "phone", "emailAddress", "coupleName", "headOfHouseholdIndividualID"});
		householdAddress = new Address(definition);
	}

	public Address relocate(JSONObject relocation) {
		JSONObject relocatable = (null == relocation) ? null : (JSONObject) relocation.get(getCoupleName());
		Address previous = getHouseholdAddress();

		if (null != relocatable) {
			Address address = new Address();

			address.setLattitude( (null == relocatable.get("latitude")) ? previous.getLattitude() : relocatable.get("latitude").toString());
			address.setLongitude( (null == relocatable.get("longitude")) ? previous.getLongitude() : relocatable.get("longitude").toString());
			address.setPostalCode( (null == relocatable.get("postalCode")) ? previous.getPostalCode() : relocatable.get("postalCode").toString());
			address.setState( (null == relocatable.get("state")) ? previous.getState() : relocatable.get("state").toString());
			if (null == relocatable.get("address")) {
				address.setStreetAddress( (null == relocatable.get("desc1") || null == relocatable.get("desc2"))
										? previous.getLattitude()
										: relocatable.get("desc1").toString() + ", " + relocatable.get("desc2").toString());
			} else {
				address.setStreetAddress(relocatable.get("address").toString());
			}

			return address;

		}

		return previous;
	}

	@Override
	protected void setFromJSON(JSONObject definition, String key) {

		if (key.equals("headOfHouse")) {
			setHeadOfHousehold((null == definition.get(key)) ? null : new HouseholdMember((JSONObject) definition.get(key)));
		} else if (key.equals("spouse")) {
			setSpouse((null == definition.get(key)) ? null : new HouseholdMember((JSONObject) definition.get(key)));
		} else if (key.equals("children")) {
			addChildren(HouseholdMember.fromArray( (JSONArray) definition.get(key)));
		} else {
			super.setFromJSON(definition, key);
		}

	}

	public List<String> getIndividualIds() {
		List<String> ids = new ArrayList<String>();

		ids.add(headOfHousehold.getIndividualId());
		if (null != spouse) {
			ids.add(spouse.getIndividualId());
		}
		for (HouseholdMember child : children) {
			ids.add(child.getIndividualId());
		}
		return ids;
	}

	public HouseholdMember getMember(String individualId) {

		if (individualId.equals(headOfHousehold.getIndividualId())) {
			return headOfHousehold;
		}

		if ( (null != spouse) && individualId.equals(spouse.getIndividualId()) ) {
			return spouse;
		}

		for (HouseholdMember child : children) {
			if (individualId.equals(child.getIndividualId())) {
				return child;
			}
		}

		return null;
	}

	public String getHouseholdName() {
		return get("householdName");
	}

	public void setHouseholdName(String householdName) {
		put("householdName", householdName);
	}

	public HouseholdMember getHeadOfHousehold() {
		return headOfHousehold;
	}

	public void setHeadOfHousehold(HouseholdMember headOfHousehold) {
		this.headOfHousehold = headOfHousehold;
	}

	public HouseholdMember getSpouse() {
		return spouse;
	}

	public void setSpouse(HouseholdMember spouse) {
		this.spouse = spouse;
	}

	/**
	 * @return Unmodifiable list of children
	 */
	public List<HouseholdMember> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Sets the list of Children, overwriting any existing children
	 * @param children the children to set
	 */
	public void setChildren(List<HouseholdMember> children) {
		this.children.clear();
		addChildren(children);
	}

	/**
	 * Add multiple children to the household
	 *
	 * @param children
	 *            List<Member>
	 */
	public void addChildren(List<HouseholdMember> children) {
		this.children.addAll(children);
	}

	/**
	 * Add a single child to the household
	 *
	 * @param child
	 *            Member
	 */
	public void addChild(HouseholdMember child) {
		this.children.add(child);
	}

	public Address getHouseholdAddress() {
		return householdAddress;
	}

	public void setHouseholdAddress(Address householdAddress) {
		this.householdAddress = householdAddress;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return get("phone");
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		put("phone", phone);
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return get("emailAddress");
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		put("emailAddress", emailAddress);
	}

	/**
	 * @return the coupleName
	 */
	public String getCoupleName() {
		return get("coupleName");
	}

	/**
	 * @param coupleName
	 *            the coupleName to set
	 */
	public void setCoupleName(String coupleName) {
		put("coupleName", coupleName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String value = "";

		for (HouseholdMember child : children) {
			value += (value.length() == 0 ? "" : ", ") + child.toString();
		}

		return "Household [" + super.toString() + ", headOfHousehold = " + headOfHousehold + ", spouse = " + spouse + ", householdAddress = " + householdAddress + ", children = [" + value + "] ]";

	}

	/**
	 * @return the headOfHouseholdIndividualID
	 */
	public String getHeadOfHouseholdIndividualID() {
		return get("headOfHouseholdIndividualID");
	}

	/**
	 * @param headOfHouseholdIndividualID the headOfHouseholdIndividualID to set
	 */
	public void setHeadOfHouseholdIndividualID(String headOfHouseholdIndividualID) {
		put("headOfHouseholdIndividualID", headOfHouseholdIndividualID);
	}

}
