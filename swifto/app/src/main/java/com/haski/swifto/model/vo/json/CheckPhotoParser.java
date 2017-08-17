package com.haski.swifto.model.vo.json;

import com.haski.swifto.model.vo.CheckPhotoConfig;
import com.haski.swifto.model.vo.GPSConfig;
import com.haski.swifto.util.WalkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckPhotoParser {
	
	interface WALK_STATE {
		String SUCCESS = "success";
		String URLS = "urls";
	}

	public CheckPhotoConfig parseCheckPhoto(String json) {
		//String toRet = "";
		JSONObject jRoot;
		CheckPhotoConfig toRet = new CheckPhotoConfig();

		try {
			jRoot = new JSONObject(json);

			if(jRoot.has(WALK_STATE.SUCCESS)) {
				JSONArray jurl = jRoot.getJSONArray(WALK_STATE.URLS);
				if(jurl.length()>0)
				{
					toRet.successphoto = Boolean.TRUE;
					for (int i= 0; i< jurl.length();i++)
					{
						toRet.imageUrlList.add(jurl.get(i).toString());
					}
				}
				else
				{
					toRet.successphoto = Boolean.FALSE;
				}

			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return toRet;
	}
}
