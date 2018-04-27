package org.randywebb.wlts.beans;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.randywebb.wlts.beans.AbstractBean;
import org.randywebb.wlts.beans.Household;
import org.randywebb.wlts.beans.District;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailedMinistered extends AbstractBean {

	private static Logger log = LoggerFactory.getLogger(DetailedMinistered.class);

	private static void copy(AbstractBean from, String fromName, AbstractBean to) {
		copy(from, fromName, to, fromName);
	}

	private static void copy(AbstractBean from, String fromName, AbstractBean to, String toName) {
		to.put(toName, from.get(fromName));
	}

	public static List<DetailedMinistered> fromDistricts(List<District> districts, List<Household> households, JSONObject relocation) {
		ArrayList<DetailedMinistered> results = new ArrayList<DetailedMinistered>();
		Map<String, Household> idToHousehold = Household.mapIndividualIdsToHousehold(households);

		for (District district : districts) {
			Household districtHousehold = null == district.getDistrictLeaderIndividualId() ? null : idToHousehold.get(district.getDistrictLeaderIndividualId());
			HouseholdMember districtLeader = null == districtHousehold ? null : districtHousehold.getMember(district.getDistrictLeaderIndividualId());

			for (Companionship companionship : district.getCompanionships()) {
				String companionshipName = "";

				for (Teacher teacher : companionship.getTeachers()) {
					Household teacherHousehold = null == teacher.getIndividualId() ? null : idToHousehold.get(teacher.getIndividualId());
					HouseholdMember teacherMember = null == teacherHousehold ? null : teacherHousehold.getMember(teacher.getIndividualId());

					companionshipName += (companionshipName.length() == 0 ? "" : " & ") + teacherMember.getPreferredName();
				}

				for (Assignment assignment : companionship.getAssignments()) {
					DetailedMinistered ministered = new DetailedMinistered();
					Household household = idToHousehold.get(assignment.get("individualId"));
					HouseholdMember member = household.getMember(assignment.get("individualId"));
					Address address = household.relocate(relocation);

					copy(assignment, "individualId", ministered);
					copy(assignment, "assignmentType", ministered);
					copy(assignment, "companionshipId", ministered, "ministeringCompanionshipId");

					copy(companionship, "auxiliaryId", ministered);
					copy(companionship, "districtLeaderId", ministered);
					copy(companionship, "districtLeaderIndividualId", ministered);
					copy(companionship, "name", ministered, "districtName");

					copy(companionship, "districtId", ministered);
					copy(companionship, "startDate", ministered, "ministerStartDate");

					copy(member, "fullName", ministered);
					copy(member, "preferredName", ministered);
					copy(member, "memberId", ministered);
					copy(member, "surname", ministered);
					copy(member, "givenName", ministered);
					copy(member, "phone", ministered);
					copy(member, "email", ministered);

					copy(address, "latitude", ministered);
					copy(address, "longitude", ministered);
					copy(address, "postalCode", ministered);
					copy(address, "state", ministered);
					copy(address, "desc1", ministered);
					copy(address, "desc2", ministered);
					copy(address, "desc3", ministered);

					copy(household, "householdName", ministered);
					copy(household, "phone", ministered, "householdPhone");
					copy(household, "emailAddress", ministered, "householdEmailAddress");
					copy(household, "coupleName", ministered, "householdCoupleName");

					ministered.put("ministers", companionshipName);
					ministered.put("districtLeaderName", districtLeader.getPreferredName());
					ministered.put("nearestHouseholdName", household.nearest(households, relocation).get("householdName"));

				}

			}

		}

		return results;
	}

	public DetailedMinistered() {
	}

	@Override
	public String toString() {
		return "DetailedMinistered [" + super.toString() + "]";
	}

}
