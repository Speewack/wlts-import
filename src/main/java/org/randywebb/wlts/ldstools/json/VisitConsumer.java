package org.randywebb.wlts.ldstools.json;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.Visit;
import org.json.simple.JSONObject;

public class VisitConsumer extends AbstractConsumer {

	private List<Visit> visits;

	public VisitConsumer(List<Visit> visits) {
		this.visits = visits;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		Visit visit = bindVisit(jo);

		if (null != visits) {
			visits.add(visit);
		}
	}

	public static Visit bindVisit(JSONObject jo) {

		Visit visit = null;

		if (jo != null) {
			visit = new Visit();

			visit.setId(convert(jo.get("id")));
			visit.setAssignmentId(convert(jo.get("assignmentId")));
			visit.setVisited(convert(jo.get("visited")));
			visit.setYear(convert(jo.get("year")));
			visit.setMonth(convert(jo.get("month")));

		}

		return visit;
	}

}
