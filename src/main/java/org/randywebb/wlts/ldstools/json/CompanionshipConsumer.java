package org.randywebb.wlts.ldstools.json;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.Companionship;
import org.randywebb.wlts.beans.Teacher;
import org.randywebb.wlts.beans.Assignment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CompanionshipConsumer extends AbstractConsumer {

	private List<Companionship> companionships;

	public CompanionshipConsumer(List<Companionship> companionships) {
		this.companionships = companionships;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		Companionship companionship = bindCompanionship(jo);

		if (null != companionships) {
			companionships.add(companionship);
		}
	}

	public static Companionship bindCompanionship(JSONObject jo) {

		Companionship companionship = null;

		if (jo != null) {
			companionship = new Companionship();

			companionship.setId(convert(jo.get("id")));
			companionship.setDistrictId(convert(jo.get("districtId")));
			companionship.setStartDate(convert(jo.get("startDate")));
			companionship.setTeachers(Teacher.fromArray((JSONArray) jo.get("teachers")));
			companionship.setAssignments(Assignment.fromArray((JSONArray) jo.get("assignments")));

		}

		return companionship;

	}

}
