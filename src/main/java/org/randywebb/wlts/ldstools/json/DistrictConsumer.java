package org.randywebb.wlts.ldstools.json;

import java.util.List;
import java.util.ArrayList;

import org.randywebb.wlts.beans.District;
import org.randywebb.wlts.beans.Companionship;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DistrictConsumer extends AbstractConsumer {

	private List<District> districts;

	public DistrictConsumer(List<District> districts) {
		this.districts = districts;
	}

	@Override
	public void accept(Object obj) {
		JSONObject jo = (JSONObject) obj;
		District district = bindDistrict(jo);

		if (null != districts) {
			districts.add(district);
		}
	}

	public static District bindDistrict(JSONObject jo) {

		District district = null;

		if (jo != null) {
			district = new District();

			district.setId(convert(jo.get("id")));
			district.setAuxiliaryId(convert(jo.get("auxiliaryId")));
			district.setDistrictLeaderId(convert(jo.get("districtLeaderId")));
			district.setDistrictLeaderIndividualId(convert(jo.get("districtLeaderIndividualId")));
			district.setName(convert(jo.get("name")));
			district.setCompanionships(Companionship.fromArray((JSONArray) jo.get("companionships")));

		}

		return district;
	}

}
