package org.randywebb.wlts.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import org.supercsv.cellprocessor.ift.CellProcessor;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/** Detailed information for ministered csv report. */
public class DetailedMinistered extends AbstractBean {

    // To use for debugging
    //private static Logger log = LoggerFactory.getLogger(DetailedMinistered.class);

    /** Helper method to copy a field name from one object to another.
        @param from The object to copy from
        @param fromName The name of the key in from to copy into to
        @param to The object to receive the value for key in from
    */
    private static void copy(AbstractBean from, String fromName, AbstractBean to) {
        copy(from, fromName, to, fromName);
    }

    /** Helper method to copy a field name from one object to another with a different key.
        @param from The object to copy from
        @param fromName The name of the key in from to copy into to
        @param to The object to receive the value for key in from
        @param toName The name of the value in to
    */
    private static void copy(AbstractBean from, String fromName, AbstractBean to, String toName) {
        to.put(toName, from.get(fromName));
    }

    /** Gets the CellProcessors for CSV generation.
        @param headers The names of the fields, in the order they will be used.
        @param stringProcessor The processor to use for the values
        @return The appropriate processor for each header field
                    in the order they are found in headers
    */
    public static CellProcessor[] csvProcessors(String[] headers, CellProcessor stringProcessor) {
        ArrayList<String> none = new ArrayList<String>();

        return csvProcessors(headers, none, none, none, stringProcessor, null, null, null);
    }

    /** Converts a List of District to a List of DetailedMinistered.
        @param districts The list of ministering districts
        @param households The households to look up names and addresses
        @param relocation The data used to redirect the location for specified households
        @return The DetailedMinistered List
    */
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

                    companionshipName += companionshipName.length() == 0 ? "" : " & ";
                    companionshipName += teacherMember.getPreferredName();
                }

                for (Assignment assignment : companionship.getAssignments()) {
                    DetailedMinistered ministered = new DetailedMinistered();
                    String individualId = assignment.get("individualId");
                    Household household = idToHousehold.get(individualId);
                    Household nearest = household.nearest(households, relocation);
                    HouseholdMember member = household.getMember(individualId);
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
                    ministered.put("districtLeaderName",
                        null == districtLeader ? null : districtLeader.getPreferredName());
                    ministered.put("nearestHouseholdName", null == nearest ? null : nearest.get("householdName"));

                    results.add(ministered);

                }

            }

        }

        return results;
    }

    /** Default constructor. */
    public DetailedMinistered() {
    }

    /**
        @return A textual representation of the DetailedMinistered
    */
    @Override
    public String toString() {
        return "DetailedMinistered [" + super.toString() + "]";
    }

}
