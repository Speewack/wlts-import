package org.randywebb.wlts.ldstools.json;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.Assignment;
import org.randywebb.wlts.beans.Visit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AssignmentConsumer extends AbstractConsumer {

	private List<Assignment> assignments;

	public AssignmentConsumer(List<Assignment> assignments) {
		this.assignments = assignments;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		Assignment assignment = bindAssignment(jo);

		if (null != assignments) {
			assignments.add(assignment);
		}
	}

	public static Assignment bindAssignment(JSONObject jo) {

		Assignment assignment = null;

		if (jo != null) {
			assignment = new Assignment();

			assignment.setId(convert(jo.get("id")));
			assignment.setCompanionshipId(convert(jo.get("companionshipId")));
			assignment.setIndividualId(convert(jo.get("individualId")));
			assignment.setAssignmentType(convert(jo.get("assignmentType")));

			JSONArray visitsJSON = (JSONArray) jo.get("visits");

			if (null != visitsJSON) {
				List<Visit> visits = new ArrayList<Visit>();
				VisitConsumer action = new VisitConsumer(visits);

				visitsJSON.forEach(action);

				assignment.setVisits(visits);
			}

		}

		return assignment;
	}

}
