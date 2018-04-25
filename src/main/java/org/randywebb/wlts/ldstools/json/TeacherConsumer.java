package org.randywebb.wlts.ldstools.json;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.Teacher;
import org.json.simple.JSONObject;

public class TeacherConsumer extends AbstractConsumer {

	private List<Teacher> teachers;

	public TeacherConsumer(List<Teacher> teachers) {
		this.teachers = teachers;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		Teacher teacher = bindTeacher(jo);

		if (null != teachers) {
			teachers.add(teacher);
		}
	}

	public static Teacher bindTeacher(JSONObject jo) {

		Teacher teacher = null;

		if (jo != null) {
			teacher = new Teacher();

			teacher.setId(convert(jo.get("id")));
			teacher.setCompanionshipId(convert(jo.get("companionshipId")));
			teacher.setIndividualId(convert(jo.get("individualId")));

		}

		return teacher;
	}

}
