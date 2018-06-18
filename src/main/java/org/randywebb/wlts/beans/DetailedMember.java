package org.randywebb.wlts.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.supercsv.cellprocessor.ift.CellProcessor;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Detailed member information for the WLTS report.
* @author randyw
*
*/
public class DetailedMember extends AbstractBean {

    // For logging if needed during debugging.
    //private static Logger log = LoggerFactory.getLogger(DetailedMember.class);

    /** The date format for the report. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    /** Names of the fields that are boolean values in the report. */
    private static final List<String> BOOLEAN_FIELDS = Arrays.asList(new String[] {
        "visible", "nonMember", "outOfUnitMember", "isHead", "isSpouse", "isAdult",
        "fullTimeMissionary", "setApart"});
    /** Names of the fields that are date values in the report. */
    private static final List<String> DATE_FIELDS = Arrays.asList(new String[] {"birthDate", "sustainedDate"});
    /** Names of the fields that are integers in the report. */
    private static final List<String> INTEGER_FIELDS = Arrays.asList(new String[] {
        "nameOrder", "birthDateSort", "age", "actualAge", "actualAgeInMonths"});

    /** Converts a JSON Array of detailed member data to a List of DetailedMember.
        @param array JSON Array of JSON detailed member data objects
        @return The DetailedMembers from the JSON Array
    */
    public static List<DetailedMember> fromArray(JSONArray array) {
        return fromArray(array, new ArrayList<DetailedMember>(), DetailedMember.class);
    }

    /** Gets the CellProcessors for CSV generation.
        @param headers The names of the fields, in the order they will be used.
        @param stringProcessor The processor to use for string values
        @param booleanProcessor The processor to use for boolean values
        @param integerProcessor The processor to use for integer values
        @param dateProcessor The processor to use for date values
        @return The appropriate processor for each header field
                    in the order they are found in headers
    */
    public static CellProcessor[] csvProcessors(String[] headers,
            CellProcessor stringProcessor, CellProcessor booleanProcessor,
            CellProcessor integerProcessor, CellProcessor dateProcessor) {

        return csvProcessors(headers, BOOLEAN_FIELDS, DATE_FIELDS, INTEGER_FIELDS, stringProcessor, booleanProcessor, integerProcessor, dateProcessor);
    }

    /** Defaults constructor. */
    public DetailedMember() {
        super(DATE_FORMAT, BOOLEAN_FIELDS, INTEGER_FIELDS, DATE_FIELDS);
    }

    /** Convert a detailed member JSON Object to a DetailedMember.
        @param definition A JSON detailed member Object
    */
    public DetailedMember(JSONObject definition) {
        super(DATE_FORMAT, BOOLEAN_FIELDS, INTEGER_FIELDS, DATE_FIELDS);
        update(definition,
            "name", "spokenName", "nameOrder", "birthDate", "birthDateSort", "formattedBirthDate",
            "gender", "genderCode", "mrn", "id", "email", "householdEmail", "phone",
            "householdPhone", "unitNumber", "unitName", "priesthood", "priesthoodCode",
            "priesthoodType", "age", "actualAge", "actualAgeInMonths", "genderLabelShort",
            "visible", "nonMember", "outOfUnitMember", "street", "city", "state", "zip",
            "givenName", "coupleName", "householdId", "isHead", "isSpouse", "isAdult",
            "fullTimeMissionary", "formattedMRN", "setApart", "formattedBirthDateFull",
            "sustainedDate");

        //Handle the Address if address attribute is present
        String address = definition.get("address").toString();
        if (address != null) {
            this.setAddress(Address.toAddress(address));
        }

    }

    /**
    * @return the name
    */
    public String getName() {
        return get("name");
    }

    /**
    * @param name
    *          the name to set
    */
    public void setName(String name) {
        put("name", name);
    }

    /**
    * @return the spokenName
    */
    public String getSpokenName() {
        return get("spokenName");
    }

    /**
    * @param spokenName
    *          the spokenName to set
    */
    public void setSpokenName(String spokenName) {
        put("spokenName", spokenName);
    }

    /**
    * @return the nameOrder
    */
    public Integer getNameOrder() {
        return getInteger("nameOrder");
    }

    /**
    * @param nameOrder
    *          the nameOrder to set
    */
    public void setNameOrder(Integer nameOrder) {
        put("nameOrder", nameOrder);
    }

    /**
    * @return the birthDate
    */
    public Date getBirthDate() {
        return getDate("birthDate");
    }

    /**
    * @param birthDate
    *          the birthDate to set
    */
    public void setBirthDate(Date birthDate) {
        put("birthDate", birthDate);
    }

    /**
    * @return the birthDateSort
    */
    public Integer getBirthDateSort() {
        return getInteger("birthDateSort");
    }

    /**
    * @param birthDateSort
    *          the birthDateSort to set
    */
    public void setBirthDateSort(Integer birthDateSort) {
        put("birthDateSort", birthDateSort);
    }

    /**
    * @return the formattedBirthdate
    */
    public String getFormattedBirthDate() {
        return get("formattedBirthDate");
    }

    /**
    * @param formattedBirthdate
    *          the formattedBirthdate to set
    */
    public void setFormattedBirthDate(String formattedBirthdate) {
        put("formattedBirthDate", formattedBirthdate);
    }

    /**
    * @return the gender
    */
    public String getGender() {
        return get("gender");
    }

    /**
    * @param gender
    *          the gender to set
    */
    public void setGender(String gender) {
        put("gender", gender);
    }

    /**
    * @return the genderCode
    */
    public String getGenderCode() {
        return get("genderCode");
    }

    /**
    * @param genderCode
    *          the genderCode to set
    */
    public void setGenderCode(String genderCode) {
        put("genderCode", genderCode);
    }

    /**
    * @return the mrn
    */
    public String getMrn() {
        return get("mrn");
    }

    /**
    * @param mrn
    *          the mrn to set
    */
    public void setMrn(String mrn) {
        put("mrn", mrn);
    }

    /**
    * @return the id
    */
    public String getId() {
        return get("id");
    }

    /**
    * @param id
    *          the id to set
    */
    public void setId(String id) {
        put("id", id);
    }

    /**
    * @return the email
    */
    public String getEmail() {
        return get("email");
    }

    /**
    * @param email
    *          the email to set
    */
    public void setEmail(String email) {
        put("email", email);
    }

    /**
    * @return the householdEmail
    */
    public String getHouseholdEmail() {
        return get("householdEmail");
    }

    /**
    * @param householdEmail
    *          the householdEmail to set
    */
    public void setHouseholdEmail(String householdEmail) {
        put("householdEmail", householdEmail);
    }

    /**
    * @return the phone
    */
    public String getPhone() {
        return get("phone");
    }

    /**
    * @param phone
    *          the phone to set
    */
    public void setPhone(String phone) {
        put("phone", phone);
    }

    /**
    * @return the householdPhone
    */
    public String getHouseholdPhone() {
        return get("householdPhone");
    }

    /**
    * @param householdPhone
    *          the householdPhone to set
    */
    public void setHouseholdPhone(String householdPhone) {
        put("householdPhone", householdPhone);
    }

    /**
    * @return the unitNumber
    */
    public String getUnitNumber() {
        return get("unitNumber");
    }

    /**
    * @param unitNumber
    *          the unitNumber to set
    */
    public void setUnitNumber(String unitNumber) {
        put("unitNumber", unitNumber);
    }

    /**
    * @return the unitName
    */
    public String getUnitName() {
        return get("unitName");
    }

    /**
    * @param unitName
    *          the unitName to set
    */
    public void setUnitName(String unitName) {
        put("unitName", unitName);
    }

    /**
    * @return the priesthood
    */
    public String getPriesthood() {
        return get("priesthood");
    }

    /**
    * @param priesthood
    *          the priesthood to set
    */
    public void setPriesthood(String priesthood) {
        put("priesthood", priesthood);
    }

    /**
    * @return the priesthoodCode
    */
    public String getPriesthoodCode() {
        return get("priesthoodCode");
    }

    /**
    * @param priesthoodCode
    *          the priesthoodCode to set
    */
    public void setPriesthoodCode(String priesthoodCode) {
        put("priesthoodCode", priesthoodCode);
    }

    /**
    * @return the priesthoodType
    */
    public String getPriesthoodType() {
        return get("priesthoodType");
    }

    /**
    * @param priesthoodType
    *          the priesthoodType to set
    */
    public void setPriesthoodType(String priesthoodType) {
        put("priesthoodType", priesthoodType);
    }

    /**
    * @return the age
    */
    public Integer getAge() {
        return getInteger("age");
    }

    /**
    * @param age
    *          the age to set
    */
    public void setAge(Integer age) {
        put("age", age);
    }

    /**
    * @return the actualAge
    */
    public Integer getActualAge() {
        return getInteger("actualAge");
    }

    /**
    * @param actualAge
    *          the actualAge to set
    */
    public void setActualAge(Integer actualAge) {
        put("actualAge", actualAge);
    }

    /**
    * @return the actualAgeInMonths
    */
    public Integer getActualAgeInMonths() {
        return getInteger("actualAgeInMonths");
    }

    /**
    * @param actualAgeInMonths
    *          the actualAgeInMonths to set
    */
    public void setActualAgeInMonths(Integer actualAgeInMonths) {
        put("actualAgeInMonths", actualAgeInMonths);
    }

    /**
    * @return the genderLabelShort
    */
    public String getGenderLabelShort() {
        return get("genderLabelShort");
    }

    /**
    * @param genderLabelShort
    *          the genderLabelShort to set
    */
    public void setGenderLabelShort(String genderLabelShort) {
        put("genderLabelShort", genderLabelShort);
    }

    /**
    * @return the visible
    */
    public Boolean getVisible() {
        return getBoolean("visible");
    }

    /**
    * @param visible
    *          the visible to set
    */
    public void setVisible(Boolean visible) {
        put("visible", visible);
    }

    /**
    * @return the nonMember
    */
    public Boolean getNonMember() {
        return getBoolean("nonMember");
    }

    /**
    * @param nonMember
    *          the nonMember to set
    */
    public void setNonMember(Boolean nonMember) {
        put("nonMember", nonMember);
    }

    /**
    * @return the outOfUnitMember
    */
    public Boolean getOutOfUnitMember() {
        return getBoolean("outOfUnitMember");
    }

    /**
    * @param outOfUnitMember
    *          the outOfUnitMember to set
    */
    public void setOutOfUnitMember(Boolean outOfUnitMember) {
        put("outOfUnitMember", outOfUnitMember);
    }

    /**
    * @return the address
    */
    public Address getAddress() {
        Address address = new Address();
        address.setStreetAddress(this.getStreet());
        address.setCity(this.getCity());
        address.setState(this.getState());
        address.setPostalCode(this.getZip());
        return address;
    }

    /**
    * @param address
    *          the address to set
    */
    public void setAddress(Address address) {
        if (address != null) {
            this.setStreet(address.getStreetAddress());
            this.setCity(address.getCity());
            this.setState(address.getState());
            this.setZip(address.getPostalCode());
        }
    }

    /**
    * @return the givenName
    */
    public String getGivenName() {
        return get("givenName");
    }

    /**
    * @param givenName
    *          the givenName to set
    */
    public void setGivenName(String givenName) {
        put("givenName", givenName);
    }

    /**
    * @return the coupleName
    */
    public String getCoupleName() {
        return get("coupleName");
    }

    /**
    * @param coupleName
    *          the coupleName to set
    */
    public void setCoupleName(String coupleName) {
        put("coupleName", coupleName);
    }

    /**
    * @return the householdId
    */
    public String getHouseholdId() {
        return get("householdId");
    }

    /**
    * @param householdId
    *          the householdId to set
    */
    public void setHouseholdId(String householdId) {
        put("householdId", householdId);
    }

    /**
    * @return the isHead
    */
    public Boolean getIsHead() {
        return getBoolean("isHead");
    }

    /**
    * @param isHead
    *          the isHead to set
    */
    public void setIsHead(Boolean isHead) {
        put("isHead", isHead);
    }

    /**
    * @return the isSpouse
    */
    public Boolean getIsSpouse() {
        return getBoolean("isSpouse");
    }

    /**
    * @param isSpouse
    *          the isSpouse to set
    */
    public void setIsSpouse(Boolean isSpouse) {
        put("isSpouse", isSpouse);
    }

    /**
    * @return the isAdult
    */
    public Boolean getIsAdult() {
        return getBoolean("isAdult");
    }

    /**
    * @param isAdult
    *          the isAdult to set
    */
    public void setIsAdult(Boolean isAdult) {
        put("isAdult", isAdult);
    }

    /**
    * @return the fullTimeMissionary
    */
    public Boolean getFullTimeMissionary() {
        return getBoolean("fullTimeMissionary");
    }

    /**
    * @param fullTimeMissionary
    *          the fullTimeMissionary to set
    */
    public void setFullTimeMissionary(Boolean fullTimeMissionary) {
        put("fullTimeMissionary", fullTimeMissionary);
    }

    /**
    * @return the formattedMRN
    */
    public String getFormattedMRN() {
        return get("formattedMRN");
    }

    /**
    * @param formattedMRN
    *          the formattedMRN to set
    */
    public void setFormattedMRN(String formattedMRN) {
        put("formattedMRN", formattedMRN);
    }

    /**
    * @return the setApart
    */
    public Boolean getSetApart() {
        return getBoolean("setApart");
    }

    /**
    * @param setApart
    *          the setApart to set
    */
    public void setSetApart(Boolean setApart) {
        put("setApart", setApart);
    }

    /**
    * @return the formattedBirthDateFull
    */
    public String getFormattedBirthDateFull() {
        return get("formattedBirthDateFull");
    }

    /**
    * @param formattedBirthDateFull
    *          the formattedBirthDateFull to set
    */
    public void setFormattedBirthDateFull(String formattedBirthDateFull) {
        put("formattedBirthDateFull", formattedBirthDateFull);
    }

    /**
    * @return the sustainedDate
    */
    public Date getSustainedDate() {
        return getDate("sustainedDate");
    }

    /**
    * @param sustainedDate
    *          the sustainedDate to set
    */
    public void setSustainedDate(Date sustainedDate) {
        put("sustainedDate", sustainedDate);
    }

    /** Helper function to add "name = value, ".
        @param <Type> The type of the value to append
        @param builder the builder to append to
        @param name The name of the value
        @param value The value to add
        @param first true if this is the first non-null entry (no comma needed before it)
        @return true if first was true and the value is null, false otherwise
    */
    private static <Type> boolean appendIfNotNull(StringBuilder builder, String name, Type value, boolean first) {
        if (value != null) {

            if (!first) {
                builder.append(", ");
            }

            builder.append(name + "=");
            builder.append(value);
            return false;
        }

        return first;
    }

    /**
    *
    * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        builder.append("DetailedMember [");
        first = appendIfNotNull(builder, "name", getName(), first);
        first = appendIfNotNull(builder, "spokenName", getSpokenName(), first);
        first = appendIfNotNull(builder, "nameOrder", getNameOrder(), first);
        first = appendIfNotNull(builder, "birthDate", getBirthDate(), first);
        first = appendIfNotNull(builder, "birthDateSort", getBirthDateSort(), first);
        first = appendIfNotNull(builder, "formattedBirthdate", getFormattedBirthDate(), first);
        first = appendIfNotNull(builder, "gender", getGender(), first);
        first = appendIfNotNull(builder, "genderCode", getGenderCode(), first);
        first = appendIfNotNull(builder, "mrn", getMrn(), first);
        first = appendIfNotNull(builder, "id", getId(), first);
        first = appendIfNotNull(builder, "email", getEmail(), first);
        first = appendIfNotNull(builder, "householdEmail", getHouseholdEmail(), first);
        first = appendIfNotNull(builder, "phone", getPhone(), first);
        first = appendIfNotNull(builder, "householdPhone", getHouseholdPhone(), first);
        first = appendIfNotNull(builder, "unitNumber", getUnitNumber(), first);
        first = appendIfNotNull(builder, "unitName", getUnitName(), first);
        first = appendIfNotNull(builder, "priesthood", getPriesthood(), first);
        first = appendIfNotNull(builder, "priesthoodCode", getPriesthoodCode(), first);
        first = appendIfNotNull(builder, "priesthoodType", getPriesthoodType(), first);
        first = appendIfNotNull(builder, "age", getAge(), first);
        first = appendIfNotNull(builder, "actualAge", getActualAge(), first);
        first = appendIfNotNull(builder, "actualAgeInMonths", getActualAgeInMonths(), first);
        first = appendIfNotNull(builder, "genderLabelShort", getGenderLabelShort(), first);
        first = appendIfNotNull(builder, "visible", getVisible(), first);
        first = appendIfNotNull(builder, "nonMember", getNonMember(), first);
        first = appendIfNotNull(builder, "outOfUnitMember", getOutOfUnitMember(), first);
        first = appendIfNotNull(builder, "street", getStreet(), first);
        first = appendIfNotNull(builder, "city", getCity(), first);
        first = appendIfNotNull(builder, "state", getState(), first);
        first = appendIfNotNull(builder, "zip", getZip(), first);
        first = appendIfNotNull(builder, "givenName", getGivenName(), first);
        first = appendIfNotNull(builder, "coupleName", getCoupleName(), first);
        first = appendIfNotNull(builder, "householdId", getHouseholdId(), first);
        first = appendIfNotNull(builder, "isHead", getIsHead(), first);
        first = appendIfNotNull(builder, "isSpouse", getIsSpouse(), first);
        first = appendIfNotNull(builder, "isAdult", getIsAdult(), first);
        first = appendIfNotNull(builder, "fullTimeMissionary", getFullTimeMissionary(), first);
        first = appendIfNotNull(builder, "formattedMRN", getFormattedMRN(), first);
        first = appendIfNotNull(builder, "setApart", getSetApart(), first);
        first = appendIfNotNull(builder, "formattedBirthDateFull", getFormattedBirthDateFull(), first);
        first = appendIfNotNull(builder, "sustainedDate", getFormattedBirthDateFull(), first);
        builder.append("]");
        return builder.toString();
    }

    /**
    * @return the street
    */
    public String getStreet() {
        return get("street");
    }

    /**
    * @param street
    *          the street to set
    */
    public void setStreet(String street) {
        put("street", street);
    }

    /**
    * @return the city
    */
    public String getCity() {
        return get("city");
    }

    /**
    * @param city
    *          the city to set
    */
    public void setCity(String city) {
        put("city", city);
    }

    /**
    * @return the state
    */
    public String getState() {
        return get("state");
    }

    /**
    * @param state
    *          the state to set
    */
    public void setState(String state) {
        put("state", state);
    }

    /**
    * @return the zip
    */
    public String getZip() {
        return get("zip");
    }

    /**
    * @param zip
    *          the zip to set
    */
    public void setZip(String zip) {
        put("zip", zip);
    }

}
