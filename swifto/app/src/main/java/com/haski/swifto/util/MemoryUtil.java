package com.haski.swifto.util;

import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;

public class MemoryUtil {

	public static String getMemoryInfo(Context context){
		try{
			MemoryInfo mi = new MemoryInfo();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			activityManager.getMemoryInfo(mi);
			long availableMegs = mi.availMem / 1048576L;
			
			String totalMegs = "not available";
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				long totalMegsVal = mi.totalMem / 1048576L;
				totalMegs = String.format(Locale.getDefault(),  "%d Mb", totalMegsVal);
			}
			
			long thresholdMegs = mi.threshold / 1048576L;
			
			String result = String.format(Locale.getDefault(), "Memory: available %d Mb, total: %s, threshold: %d Mb", availableMegs, totalMegs, thresholdMegs);
			return result;
		} catch (Exception e) {
			return "Error getting memory info: " + e.toString();
		}
	}
}
