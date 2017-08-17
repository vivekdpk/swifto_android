package com.haski.swifto.model.vo.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import com.haski.swifto.util.WalkUtils;

public class GetWalkStateParser {
	
	interface WALK_STATE {
		String WALK = "walk";
		String STATUS = "status";
	}

	public String parseGetWalkState(String json) {
		String toRet = "";
		JSONObject jRoot;
		
		try {
			jRoot = new JSONObject(json);

			if(jRoot.has(WALK_STATE.WALK)) {
				JSONObject jWalk = jRoot.getJSONObject(WALK_STATE.WALK);

				if(jWalk.has(WALK_STATE.STATUS)) {
					JSONObject jStatus = jWalk.getJSONObject(WALK_STATE.STATUS);

					if(jStatus.has(WALK_STATE.STATUS)) {
						JSONArray arrStatuses = jStatus.getJSONArray(WALK_STATE.STATUS);

						toRet = WalkUtils.getStatus(arrStatuses);
					} else {
						//Walk has no status
					}
				} else {
					//Walk has no status
				}
			}
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return toRet;
	}
}
