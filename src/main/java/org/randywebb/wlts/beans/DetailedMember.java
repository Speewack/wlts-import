package org.randywebb.wlts.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.supercsv.cellprocessor.ift.CellProcessor;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Detailed member information for the WLTS report.
* @author randyw
*
*/
public class DetailedMember extends AbstractBean {

	/** For logging if needed during debugging */
	//private static Logger log = LoggerFactory.getLogger(DetailedMember.class);
	/** The date format for the report */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	/** Names of the fields that are boolean values in the report */
	private final static List<String> booleanFields = Arrays.asList(new String[] {
		"visible", "nonMember", "outOfUnitMember", "isHead", "isSpouse", "isAdult", "fullTimeMissionary", "setApart"});
	/** Names of the fields that are date values in the report */
	private final static List<String> dateFields = Arrays.asList(new String[] {"birthDate", "sustainedDate"});
	/** Names of the fields that are integers in the report */
	private final static List<String> integerFields = Arrays.asList(new String[] {
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
		@return The appropriate processor for each header field in the order they are found in headers
	*/
	public static CellProcessor[] csvProcessors(String[] headers,
			CellProcessor stringProcessor, CellProcessor booleanProcessor, CellProcessor integerProcessor, CellProcessor dateProcessor) {

		return csvProcessors(headers, booleanFields, dateFields, integerFields, stringProcessor, booleanProcessor, integerProcessor, dateProcessor);
	}

	/** Defaults constructor */
	public DetailedMember() {
		super(dateFormat, booleanFields, integerFields, dateFields);
	}

	/** Convert a detailed member JSON Object to a DetailedMember.
		@param definition A JSON detailed member Object
	*/
	public DetailedMember(JSONObject definition) {
		super(dateFormat, booleanFields, integerFields, dateFields);
		update(definition, new String[] {
			"name", "spokenName", "nameOrder", "birthDate", "birthDateSort", "formattedBirthDate",
			"gender", "genderCode", "mrn", "id", "email", "householdEmail", "phone", "householdPhone",
			"unitNumber", "unitName", "priesthood", "priesthoodCode", "priesthoodType", "age",
			"actualAge", "actualAgeInMonths", "genderLabelShort", "visible", "nonMember",
			"outOfUnitMember", "street", "city", "state", "zip", "givenName", "coupleName",
			"householdId", "isHead", "isSpouse", "isAdult", "fullTimeMissionary", "formattedMRN",
			"setApart", "formattedBirthDateFull", "sustainedDate", });
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

	/*
	* (non-Javadoc)
	* @see java.lang.Object#toString()
	*/
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("DetailedMember [");
		if (getName() != null) {
			builder.append("name=");
			builder.append(getName());
			builder.append(", ");
		}
		if (getSpokenName() != null) {
			builder.append("spokenName=");
			builder.append(getSpokenName());
			builder.append(", ");
		}
		if (getNameOrder() != null) {
			builder.append("nameOrder=");
			builder.append(getNameOrder());
			builder.append(", ");
		}
		if (getBirthDate() != null) {
			builder.append("birthDate=");
			builder.append(getBirthDate());
			builder.append(", ");
		}
		if (getBirthDateSort() != null) {
			builder.append("birthDateSort=");
			builder.append(getBirthDateSort());
			builder.append(", ");
		}
		if (getFormattedBirthDate() != null) {
			builder.append("formattedBirthdate=");
			builder.append(getFormattedBirthDate());
			builder.append(", ");
		}
		if (getGender() != null) {
			builder.append("gender=");
			builder.append(getGender());
			builder.append(", ");
		}
		if (getGenderCode() != null) {
			builder.append("genderCode=");
			builder.append(getGenderCode());
			builder.append(", ");
		}
		if (getMrn() != null) {
			builder.append("mrn=");
			builder.append(getMrn());
			builder.append(", ");
		}
		if (getId() != null) {
			builder.append("id=");
			builder.append(getId());
			builder.append(", ");
		}
		if (getEmail() != null) {
			builder.append("email=");
			builder.append(getEmail());
			builder.append(", ");
		}
		if (getHouseholdEmail() != null) {
			builder.append("householdEmail=");
			builder.append(getHouseholdEmail());
			builder.append(", ");
		}
		if (getPhone() != null) {
			builder.append("phone=");
			builder.append(getPhone());
			builder.append(", ");
		}
		if (getHouseholdPhone() != null) {
			builder.append("householdPhone=");
			builder.append(getHouseholdPhone());
			builder.append(", ");
		}
		if (getUnitNumber() != null) {
			builder.append("unitNumber=");
			builder.append(getUnitNumber());
			builder.append(", ");
		}
		if (getUnitName() != null) {
			builder.append("unitName=");
			builder.append(getUnitName());
			builder.append(", ");
		}
		if (getPriesthood() != null) {
			builder.append("priesthood=");
			builder.append(getPriesthood());
			builder.append(", ");
		}
		if (getPriesthoodCode() != null) {
			builder.append("priesthoodCode=");
			builder.append(getPriesthoodCode());
			builder.append(", ");
		}
		if (getPriesthoodType() != null) {
			builder.append("priesthoodType=");
			builder.append(getPriesthoodType());
			builder.append(", ");
		}
		if (getAge() != null) {
			builder.append("age=");
			builder.append(getAge());
			builder.append(", ");
		}
		if (getActualAge() != null) {
			builder.append("actualAge=");
			builder.append(getActualAge());
			builder.append(", ");
		}
		if (getActualAgeInMonths() != null) {
			builder.append("actualAgeInMonths=");
			builder.append(getActualAgeInMonths());
			builder.append(", ");
		}
		if (getGenderLabelShort() != null) {
			builder.append("genderLabelShort=");
			builder.append(getGenderLabelShort());
			builder.append(", ");
		}
		if (getVisible() != null) {
			builder.append("visible=");
			builder.append(getVisible());
			builder.append(", ");
		}
		if (getNonMember() != null) {
			builder.append("nonMember=");
			builder.append(getNonMember());
			builder.append(", ");
		}
		if (getOutOfUnitMember() != null) {
			builder.append("outOfUnitMember=");
			builder.append(getOutOfUnitMember());
			builder.append(", ");
		}
		if (getStreet() != null) {
			builder.append("street=");
			builder.append(getStreet());
			builder.append(", ");
		}
		if (getCity() != null) {
			builder.append("city=");
			builder.append(getCity());
			builder.append(", ");
		}
		if (getState() != null) {
			builder.append("state=");
			builder.append(getState());
			builder.append(", ");
		}
		if (getZip() != null) {
			builder.append("zip=");
			builder.append(getZip());
			builder.append(", ");
		}
		if (getGivenName() != null) {
			builder.append("givenName=");
			builder.append(getGivenName());
			builder.append(", ");
		}
		if (getCoupleName() != null) {
			builder.append("coupleName=");
			builder.append(getCoupleName());
			builder.append(", ");
		}
		if (getHouseholdId() != null) {
			builder.append("householdId=");
			builder.append(getHouseholdId());
			builder.append(", ");
		}
		if (getIsHead() != null) {
			builder.append("isHead=");
			builder.append(getIsHead());
			builder.append(", ");
		}
		if (getIsSpouse() != null) {
			builder.append("isSpouse=");
			builder.append(getIsSpouse());
			builder.append(", ");
		}
		if (getIsAdult() != null) {
			builder.append("isAdult=");
			builder.append(getIsAdult());
			builder.append(", ");
		}
		if (getFullTimeMissionary() != null) {
			builder.append("fullTimeMissionary=");
			builder.append(getFullTimeMissionary());
			builder.append(", ");
		}
		if (getFormattedMRN() != null) {
			builder.append("formattedMRN=");
			builder.append(getFormattedMRN());
			builder.append(", ");
		}
		if (getSetApart() != null) {
			builder.append("setApart=");
			builder.append(getSetApart());
			builder.append(", ");
		}
		if (getFormattedBirthDateFull() != null) {
			builder.append("formattedBirthDateFull=");
			builder.append(getFormattedBirthDateFull());
			builder.append(", ");
		}
		if (getFormattedBirthDateFull() != null) {
			builder.append("sustainedDate=");
			builder.append(getFormattedBirthDateFull());
		}
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
