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
	private Member headOfHousehold;
	private Member spouse;
	private List<Member> children = new ArrayList<Member>();
	private Address householdAddress;


	public String getHouseholdName() {
		return householdName;
	}

	public void setHouseholdName(String householdName) {
		this.householdName = householdName;
	}

	public Member getHeadOfHousehold() {
		return headOfHousehold;
	}

	public void setHeadOfHousehold(Member headOfHousehold) {
		this.headOfHousehold = headOfHousehold;
	}

	public Member getSpouse() {
		return spouse;
	}

	public void setSpouse(Member spouse) {
		this.spouse = spouse;
	}

	
	/**
	 * @return Unmodifiable list of children
	 */
	public List<Member> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Add multiple children to the household
	 * @param children List<Member>
	 */
	public void addChildren(List<Member> children) {
		this.children.addAll(children);
	}
	
	/** Add a single child to the household
	 * @param child Member
	 */
	public void addChild(Member child) {
		this.children.add(child);
	}

	public Address getHouseholdAddress() {
		return householdAddress;
	}

	public void setHouseholdAddress(Address householdAddress) {
		this.householdAddress = householdAddress;
	}

}
