package com.haski.swifto.model.vo.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.haski.swifto.model.vo.GPSConfig;

public class GetGpsConfigParser {

	interface GPS_CONFIG {
		String GPS_SLEEP_TIME = "andGpsSleepTime";
		String MAX_ATTEMPTS = "andMaxAttempts";
		String THRESHOLD = "andThreshold";
		String S3_KEY = "s3Key";
		String S3_SECRET = "s3Secret";
	}
	
	public GPSConfig parseGpsConfig(String json) {
		GPSConfig toRet = new GPSConfig();
		
		JSONObject jRoot;
		
		try {
			jRoot = new JSONObject(json);
			
			if(jRoot.has(GPS_CONFIG.GPS_SLEEP_TIME)) {
				toRet.GpsSleepTime = jRoot.getInt(GPS_CONFIG.GPS_SLEEP_TIME);
			}
			
			if(jRoot.has(GPS_CONFIG.MAX_ATTEMPTS)) {
				toRet.MaxAttemptsToFindPoint = jRoot.getInt(GPS_CONFIG.MAX_ATTEMPTS);
			}
			
			if(jRoot.has(GPS_CONFIG.THRESHOLD)) {
				toRet.Threshold = (float) jRoot.getDouble(GPS_CONFIG.THRESHOLD);
			}
			
			if(jRoot.has(GPS_CONFIG.S3_KEY)) {
				toRet.s3Key = jRoot.getString(GPS_CONFIG.S3_KEY);
			}
			
			if(jRoot.has(GPS_CONFIG.S3_SECRET)) {
				toRet.s3Secret = jRoot.getString(GPS_CONFIG.S3_SECRET);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return toRet;
	}
}
