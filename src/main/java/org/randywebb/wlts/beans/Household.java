/**
 * 
 */
package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class Household {

	private static Logger log = LoggerFactory.getLogger(Household.class);

	private String householdName;
	private HouseholdMember headOfHousehold;
	private HouseholdMember spouse;
	private List<HouseholdMember> children = new ArrayList<HouseholdMember>();
	private Address householdAddress;
	private String phone;
	private String emailAddress;
	private String coupleName;
	private String headOfHouseholdIndividualID;

	public String getHouseholdName() {
		return householdName;
	}

	public void setHouseholdName(String householdName) {
		this.householdName = householdName;
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
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the coupleName
	 */
	public String getCoupleName() {
		return coupleName;
	}

	/**
	 * @param coupleName
	 *            the coupleName to set
	 */
	public void setCoupleName(String coupleName) {
		this.coupleName = coupleName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Household [householdName=" + householdName + ", headOfHousehold=" + headOfHousehold + ", spouse="
				+ spouse + ", children=" + children + ", householdAddress=" + householdAddress + ", phone=" + phone
				+ ", emailAddress=" + emailAddress + ", coupleName=" + coupleName + "]";
	}

	/**
	 * @return the headOfHouseholdIndividualID
	 */
	public String getHeadOfHouseholdIndividualID() {
		return headOfHouseholdIndividualID;
	}

	/**
	 * @param headOfHouseholdIndividualID the headOfHouseholdIndividualID to set
	 */
	public void setHeadOfHouseholdIndividualID(String headOfHouseholdIndividualID) {
		this.headOfHouseholdIndividualID = headOfHouseholdIndividualID;
	}

}
