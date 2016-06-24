/**
 * 
 */
package org.randywebb.wlts.ldstools.json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.simple.JSONObject;
import org.randywebb.wlts.beans.DetailedMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class DetailedMemberConsumer extends AbstractConsumer {

	private static Logger log = LoggerFactory.getLogger(DetailedMemberConsumer.class);
	private List<DetailedMember> members = null;
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	public DetailedMemberConsumer(List<DetailedMember> members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(Object obj) {
		
		JSONObject jo = (JSONObject) obj;
		members.add(bindMember(jo));

	}
	
	public static DetailedMember bindMember(JSONObject jo) {
		
		DetailedMember member = null;
		
		if (jo != null) {
			member = new DetailedMember();
			
			member.setActualAge(Integer.valueOf(convert(jo.get("actualAge"))));
			member.setActualAgeInMonths(Integer.valueOf(convert(jo.get("actualAgeInMonths"))));
			member.setAddress(convert(jo.get("address")));
			member.setAge(Integer.valueOf(convert(jo.get("age"))));
			member.setBirthDateSort(Integer.valueOf(convert(jo.get("birthDateSort"))));
			member.setCoupleName(convert(jo.get("coupleName")));
			member.setEmail(convert(jo.get("email")));
			member.setFormattedBirthDate(convert(jo.get("formattedBirthDate")));
			member.setFormattedBirthDateFull(convert(jo.get("formattedBirthDateFull")));
			member.setFormattedMRN(convert(jo.get("formattedMrn")));
			member.setFullTimeMissionary(Boolean.valueOf(convert(jo.get("fullTimeMissionary"))));
			member.setGender(convert(jo.get("gender")));
			member.setGenderCode(convert(jo.get("genderCode")));
			member.setGenderLabelShort(convert(jo.get("genderLabelShort")));
			member.setGivenName(convert(jo.get("givenName")));
			member.setHouseholdEmail(convert(jo.get("householdEmail")));
			member.setHouseholdId(convert(jo.get("householdId")));
			member.setHouseholdPhone(convert(jo.get("householdPhone")));
			member.setId(convert(jo.get("id")));
			member.setIsAdult(Boolean.valueOf(convert(jo.get("isAdult"))));
			member.setIsHead(Boolean.valueOf(convert(jo.get("isHead"))));
			member.setIsSpouse(Boolean.valueOf(convert(jo.get("isSpouse"))));
			member.setMrn(convert(jo.get("mrn")));
			member.setName(convert(jo.get("name")));
			member.setNameOrder(Integer.valueOf(convert(jo.get("nameOrder"))));
			member.setNonMember(Boolean.valueOf(convert(jo.get("nonMember"))));
			member.setOutOfUnitMember(Boolean.valueOf(convert(jo.get("outOfUnitMember"))));
			member.setPhone(convert(jo.get("phone")));
			member.setPriesthood(convert(jo.get("priesthood")));
			member.setPriesthoodCode(convert(jo.get("priesthoodCode")));
			member.setPriesthoodType(convert(jo.get("priesthoodType")));
			member.setSetApart(Boolean.valueOf(convert(jo.get("setApart"))));
			member.setSpokenName(convert(jo.get("spokenName")));
			member.setUnitName(convert(jo.get("unitName")));
			member.setUnitNumber(convert(jo.get("unitNumber")));
			member.setVisible(Boolean.valueOf(convert(jo.get("visible"))));
			
			
			// Parse Dates
			try {
				member.setBirthDate(dateFormat.parse(convert(jo.get("birthDate"))));
			} catch (ParseException e) {
				log.error("Error parsing BirthDate: ", e);
			}
			
			try {
				member.setSustainedDate(dateFormat.parse(convert(jo.get("sustainedDate"))));
			} catch (ParseException e) {
				log.error("Error parsing SustainedDate: ", e);
			} catch (NullPointerException e) {
				// no-op
			}
			
			
			
		}
		
		return member;
	}

}
