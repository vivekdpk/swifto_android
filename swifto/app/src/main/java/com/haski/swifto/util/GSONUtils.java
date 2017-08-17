package com.haski.swifto.util;

import com.google.gson.Gson;
import com.haski.swifto.model.vo.BaseJSONObject;
import com.haski.swifto.model.vo.Walker;

public class GSONUtils {

	private static Gson gson;
	
	public static boolean wasError(String json) {
		initGson();
		
		BaseJSONObject obj = gson.fromJson(json, BaseJSONObject.class);
		
		return obj.success == false;
	}
	
	private static void initGson() {
		if(gson == null) {
			gson = new Gson();
		}
	}
	
	public static Walker parseWalker(String json) {
		initGson();
		
		Walker walker = gson.fromJson(json, Walker.class);
		return walker;
	}
}
