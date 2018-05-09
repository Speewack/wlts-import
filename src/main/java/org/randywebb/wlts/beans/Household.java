package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Represents a household.
 * @author randyw
 *
 */
public class Household extends AbstractBean {

    // Can be used for logging debugging messages.
    //private static Logger log = LoggerFactory.getLogger(Household.class);

    /** estimated miles per degree of latitude for Pflugerville, TX. */
    private static final double MILES_PER_LAT = 68.9;
    /** estimated miles per degree of longitude for Pflugerville, TX. */
    private static final double MILES_PER_LON = 59.7;

    /** The head of household. */
    private HouseholdMember headOfHousehold;
    /** The spouse (may be null). */
    private HouseholdMember spouse;
    /** The children (may be an empty list). */
    private List<HouseholdMember> children = new ArrayList<HouseholdMember>();
    /** The address of the household. */
    private Address householdAddress;

    /** Converts a JSON Array of household to a List of Household.
        @param array JSON Array of JSON household objects
        @return The Households from the JSON Array
    */
    public static List<Household> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<Household>(), Household.class);
    }

    /** Given a list of households, get a map from individualId to the household.
        @param households The list of households
        @return Map of individualId for each member of the households to their respective household
    */
    public static Map<String, Household> mapIndividualIdsToHousehold(List<Household> households) {
        Map<String, Household> idToHousehold = new HashMap<String, Household>();

        for (Household household : households) {
            for (String individualId : household.getIndividualIds()) {
                idToHousehold.put(individualId, household);
            }
        }

        return idToHousehold;
    }

    /** default constructor. */
    public Household() {
    }

    /** Convert a household JSON Object to a Household.
        @param definition A JSON household Object
    */
    public Household(JSONObject definition) {
        update(definition, "householdName", "headOfHouse", "spouse", "children", "phone", "emailAddress", "coupleName", "headOfHouseholdIndividualID");
        householdAddress = new Address(definition);
    }

    /** Find the nearest household to this household.
        @param households The households to search
        @param relocation Information to relocate households if needed
        @return The Household in households that is closest,
                    but not within 0.01 miles, of this household.
    */
    public Household nearest(List<Household> households, JSONObject relocation) {
        // circumference of the earth is approximately 24,900
        final double biggerThanTheEarth = 100000000.0;
        double min = biggerThanTheEarth;
        final double hundrethOfAMile = 0.01;
        Household found = null;

        for (Household household : households) {
            double d = distance(household, relocation);

            // within hundreth of a mile is the same place
            if (hundrethOfAMile < d && d < min) {
                min = d;
                found = household;
            }
        }

        return found;
    }

    /** Find the furthest household from this household.
        @param households The households to search
        @param relocation Information to relocate households if needed
        @return The Household in households that is furthest from this household.
    */
    public Household furthest(List<Household> households, JSONObject relocation) {
        double max = 0.0;
        Household found = null;

        for (Household household : households) {
            double d = distance(household, relocation);

            if (d > max) {
                max = d;
                found = household;
            }
        }

        return found;
    }

    /** Get the approximate distance in miles from one household to another.
        @param other The household to measure against
        @param relocation The information to relocate households, if need be
        @return The approximate miles between the two households
    */
    public double distance(Household other, JSONObject relocation) {
        Address me = relocate(relocation);
        Address them = other.relocate(relocation);

        if (null == other
                || null == me.getLatitude() || null == me.getLongitude()
                || null == them.getLatitude() || null == them.getLongitude()) {
            return 0.0;
        }

        return Math.sqrt(
            Math.pow(MILES_PER_LAT * (me.getLatitudeValue() - them.getLatitudeValue()), 2)
            + Math.pow(MILES_PER_LON * (me.getLongitudeValue() - them.getLongitudeValue()), 2));
    }

    /** Given relocation information, return the location of this household.
        Relocation can be useful when incorrect or missing information is retrieved.
        @param relocation A JSON object mapping coupleName to fields to update for the address
        @return The relocated address if there is relocation data for the coupleName,
                    otherwise the actual address
    */
    public Address relocate(JSONObject relocation) {
        JSONObject relocatable = (null == relocation)
                                    ? null
                                    : (JSONObject) relocation.get(getCoupleName());
        Address previous = getHouseholdAddress();

        if (null != relocatable) {
            Address address = new Address();

            address.setLattitude((null == relocatable.get("latitude"))
                                    ? previous.getLattitude()
                                    : relocatable.get("latitude").toString());
            address.setLongitude((null == relocatable.get("longitude"))
                                    ? previous.getLongitude()
                                    : relocatable.get("longitude").toString());
            address.setPostalCode((null == relocatable.get("postalCode"))
                                    ? previous.getPostalCode()
                                    : relocatable.get("postalCode").toString());
            address.setState((null == relocatable.get("state"))
                                    ? previous.getState()
                                    : relocatable.get("state").toString());
            if (null == relocatable.get("address")) {
                address.setStreetAddress(null == relocatable.get("desc1")
                                        || null == relocatable.get("desc2")
                                        ? previous.getLattitude()
                                        : relocatable.get("desc1").toString() + ", "
                                            + relocatable.get("desc2").toString());
            } else {
                address.setStreetAddress(relocatable.get("address").toString());
            }

            return address;

        }

        return previous;
    }

    /** Extract a specific key from an assignment JSON Object.
        We need to override since headOfHouse, spouse, and children are
            not strings but HouseholdMember, HouseholdMember,
            and an Array of HouseholdMember.
        For all other fields (simple strings), we just use the default behavior of AbstractBean.
        @param definition The JSON household object
        @param key The field of this Household to pull from definition
    */
    @Override
    protected void setFromJSON(JSONObject definition, String key) {

        if ("headOfHouse".equals(key)) {
            setHeadOfHousehold((null == definition.get(key))
                                ? null
                                : new HouseholdMember((JSONObject) definition.get(key)));
        } else if ("spouse".equals(key)) {
            setSpouse((null == definition.get(key))
                            ? null
                            : new HouseholdMember((JSONObject) definition.get(key)));
        } else if ("children".equals(key)) {
            addChildren(HouseholdMember.fromArray((JSONArray) definition.get(key)));
        } else {
            super.setFromJSON(definition, key);
        }

    }

    /** Get the list of individualId that are represented in this household.
        This would include head of household, spouse, and children.
        @return The individualId of each member of the household
    */
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

    /** Gets the household member for an individualId.
        @param individualId The individualId to look up
        @return The HouseholdMember for the individualId, if it is in the household, null otherwise
    */
    public HouseholdMember getMember(String individualId) {

        if (individualId.equals(headOfHousehold.getIndividualId())) {
            return headOfHousehold;
        }

        if (null != spouse && individualId.equals(spouse.getIndividualId())) {
            return spouse;
        }

        for (HouseholdMember child : children) {
            if (individualId.equals(child.getIndividualId())) {
                return child;
            }
        }

        return null;
    }

    /**
        @return the name of the household
    */
    public String getHouseholdName() {
        return get("householdName");
    }

    /**
        @param householdName Set the name for the household
    */
    public void setHouseholdName(String householdName) {
        put("householdName", householdName);
    }

    /**
        @return the head of household
    */
    public HouseholdMember getHeadOfHousehold() {
        return headOfHousehold;
    }

    /**
        @param headOfHouseholdToSet Set the head of the household
    */
    public void setHeadOfHousehold(HouseholdMember headOfHouseholdToSet) {
        headOfHousehold = headOfHouseholdToSet;
    }

    /**
        @return the spouse of the head of household, or null if none
    */
    public HouseholdMember getSpouse() {
        return spouse;
    }

    /**
        @param spouseToSet Set the spouse of the head of the household
    */
    public void setSpouse(HouseholdMember spouseToSet) {
        spouse = spouseToSet;
    }

    /**
     * @return Unmodifiable list of children
     */
    public List<HouseholdMember> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Sets the list of Children, overwriting any existing children.
     * @param childrenToSet the children to set
     */
    public void setChildren(List<HouseholdMember> childrenToSet) {
        children.clear();
        addChildren(childrenToSet);
    }

    /**
     * Add multiple children to the household.
     *
     * @param childrenToAdd The children to add to the existing children
     *
     */
    public void addChildren(List<HouseholdMember> childrenToAdd) {
        children.addAll(childrenToAdd);
    }

    /**
     * Add a single child to the household.
     *
     * @param child
     *            Member
     */
    public void addChild(HouseholdMember child) {
        children.add(child);
    }

    /**
     * @return The address of the household
     */
    public Address getHouseholdAddress() {
        return householdAddress;
    }

    /**
     * Sets the list of Children, overwriting any existing children.
     * @param householdAddressToSet The address of the household
     */
    public void setHouseholdAddress(Address householdAddressToSet) {
        householdAddress = householdAddressToSet;
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

        return "Household ["
                + super.toString()
                + ", headOfHousehold = " + headOfHousehold
                + ", spouse = " + spouse
                + ", householdAddress = " + householdAddress
                + ", children = [" + value + "] ]";

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
