package com.haski.swifto.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;

public class SharedPreferencesHelper {
	
	//Preferences file name
	public static final String SHARED_PREFERENCES_TAG = "SwiftoSharedPrefs";
	
	//names of preferences' keys
	public static final String PREF_STRING_CURRENT_WALK_ID = "CURRENT_WALK_ID";
	public static final String DEFAULT_CURRENT_WALK_ID = "CCCUUUQWERTYYTREWQ";

	public static final String PREF_STRING_STARTED_WALK_ID = "STARTED_WALK_ID";
	public static final String DEFAULT_STARTED_WALK_ID = "QWERTYYTREWQ";

	public static final String PREF_STRING_STARTED_WALK_UNIQUE_ID = "STARTED_WALK_UNIQUE_ID";
	public static final String DEFAULT_STARTED_WALK_UNIQUE_ID = "UNIQUEQWERTYYTREWQ";
	
	public static final String PREF_LONG_TIMESTAMP_LAST_LOCATION_TOKEN = "TIMESTAMP_LAST_LOCATION_TOKEN";
	public static final long DEFAULT_LONG_TIMESTAMP_LAST_LOCATION_TOKEN = 11;
	
	public static final String PREF_BOOL_WALK_IN_PROCESS = "WALK_IN_PROCESS";
	public static final boolean DEFAULT_BOOL_WALK_IN_PROCESS = false;	

	public static final String PREF_STRING_WALKER_ID = "WALKER_ID";
	
	public static final String IS_PHOTO_TAKEN = "IS_PHOTO_TAKEN";
	public static final String IS_GPS_OFF = "IS_GPS_OFF";
	
	public static final String GPS_OFF_TIME = "GPS_OFF_TIME";

	public static final String GPS_OFF_TIME_DURATION = "GPS_OFF_TIME_DURATION";

	public static final String DEVICE_OFF_TIME_DURATION = "DEVICE_OFF_TIME_DURATION";
	public static final String DEVICE_OFF_TIME_EXACT = "DEVICE_OFF_TIME_EXACT";

	public static final String WALK_OFF_TIME_EXACT = "WALK_OFF_TIME_EXACT";
	public static final String WALK_ON_TIME_EXACT = "WALK_ON_TIME_EXACT";


	public static final String WALK_MESSAGE = "WALK_MESSAGE";
	public static final String WALK_IMAGE = "WALK_IMAGE";

	//WALK_MESSAGE  WALK_IMAGE

	public static final String GPS_OFF_TIME_EXACT = "GPS_OFF_TIME_EXACT";
	
	public static final String DEFAULT_WALKER_ID = "dd";	

	public static final String PREF_INT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW = "PREF_INT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW";
	public static final int DEFAULT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW = -9999;	
	
	public static final String PREF_STRING_WEEK_LOAD_STATUS = "WEEK_LOAD_STATUS";
	public static final String DEFAULT_WEEK_LOAD_STATUS = "SwiftoDefaultWeekLoadStatus";
	
	public static final String PREF_INT_SELECTED_YEAR = "PREF_INT_SELECTED_YEAR";
	public static final int DEFAULT_SELECTED_YEAR = -9999;	
	
	public static final String PREF_INT_SELECTED_MONTH = "PREF_INT_SELECTED_MONTH";
	public static final int DEFAULT_SELECTED_MONTH = -9999;	
	
	public static final String PREF_INT_SELECTED_DAY = "PREF_INT_SELECTED_DAY";
	public static final int DEFAULT_SELECTED_DAY = -9999;	

	public static final String PREF_INT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE = "PREF_INT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE";
	public static final int DEFAULT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE = 0;	
	
	public static final String PREF_LONG_TIME_LAST_LOADING = "PREF_LONG_TIME_LAST_LOADING";

	public static final String MONTH_LOADING_RESPONCE = "MONTH_LOADING_RESPONCE";

	public static final long DEFAULT_LONG_TIME_LAST_LOADING = Long.MAX_VALUE;
	
	public static final String PREF_FLOAT_THRESHOLD = "PREF_FLOAT_THRESHOLD";
	public static final float DEFAULT_FLOAT_THRESHOLD = 30.0f;

	public static final String PREF_INT_GPS_SLEEP_TIME = "PREF_INT_GPS_SLEEP_TIME";
	public static final int DEFAULT_INT_GPS_SLEEP_TIME = 20;

	public static final String PREF_INT_MAX_ATTEMPTS_TO_FIND_POINTS = "PREF_INT_MAX_ATTEMPTS_TO_FIND_POINTS";
	public static final int DEFAULT_INT_MAX_ATTEMPTS_TO_FIND_POINTS = 20;
	
	public static final String PREF_STRING_LAST_MESSAGE = "LAST_MESSAGE";
	public static final String DEFAULT_LAST_MESSAGE = "";
	
	public static final String PREF_STRING_S3_KEY = "S3_KEY";
	public static final String PREF_STRING_S3_SECRET = "S3_SECRET";	
	
	
	public static final String PREF_LONG_CURRENT_POINT_LATITUDE = "PREF_LONG_CURRENT_POINT_LATITUDE";
	public static final String PREF_LONG_CURRENT_POINT_LONGITUDE = "PREF_LONG_CURRENT_POINT_LONGITUDE";
	public static final String PREF_FLOAT_CURRENT_POINT_ACCURACY = "PREF_FLOAT_CURRENT_POINT_ACCURACY";
	public static final String PREF_LONG_CURRENT_POINT_TIMESPAN = "PREF_LONG_CURRENT_POINT_TIMESPAN";
	
	
	
	
	public static void setStartedWalkId(String walkId, Context context) {
		saveString(PREF_STRING_STARTED_WALK_ID, walkId, context);
	}
	public static String getStartedWalkId(Context context) {
		return getString(PREF_STRING_STARTED_WALK_ID, DEFAULT_STARTED_WALK_ID, context);
	}
	public static void deleteStartedWalkId(Context context) {
		deletePreference(PREF_STRING_STARTED_WALK_ID, context);
	}

	public static void setStartedWalkUniqueId(String walkId, Context context) {
		saveString(PREF_STRING_STARTED_WALK_UNIQUE_ID, walkId, context);
	}
	public static String getStartedWalkUniqueId(Context context) {
		return getString(PREF_STRING_STARTED_WALK_UNIQUE_ID, DEFAULT_STARTED_WALK_UNIQUE_ID, context);
	}
	public static void deleteStartedWalkUniqueId(Context context) {
		deletePreference(PREF_STRING_STARTED_WALK_UNIQUE_ID, context);
	}
	
	public static void setTimestampLastLocationToken(Context context, long value) {
		saveLong(PREF_LONG_TIMESTAMP_LAST_LOCATION_TOKEN, value, context);
	}
	public static long getTimestampLastLocationToken(Context context) {
		return getLong(PREF_LONG_TIMESTAMP_LAST_LOCATION_TOKEN, DEFAULT_LONG_TIMESTAMP_LAST_LOCATION_TOKEN, context);
	}
	public static void deleteTimestampLastLocationtoken(Context context) {
		deletePreference(PREF_LONG_TIMESTAMP_LAST_LOCATION_TOKEN, context);
	}
	
	public static boolean getWalkIsInProcess(Context context) {
		return getBoolean(PREF_BOOL_WALK_IN_PROCESS, DEFAULT_BOOL_WALK_IN_PROCESS, context);
	}
	public static void setWalkInProcess(Context context, boolean value) {
		saveBoolean(PREF_BOOL_WALK_IN_PROCESS, value, context);
	}
	
	
	public static void saveWalkerId(Context context, String walkerId) {
		saveString(PREF_STRING_WALKER_ID, walkerId, context);
	}
	public static String getWalkerId(Context context) {
		return getString(PREF_STRING_WALKER_ID, DEFAULT_WALKER_ID, context);
	}
	public static void deleteWalkerId(Context context) {
		deletePreference(PREF_STRING_WALKER_ID, context);
	}
	
	
	public static void saveLastDayOffsetForMonthlyView(Context context, int value) {
		saveInteger(PREF_INT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW, value, context);
	}
	public static int getLastDayOffsetForMonthlyView(Context context) {
		return getInteger(PREF_INT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW, DEFAULT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW, context);
	}
	public static void deleteLastDayOffsetForMonthlyView(Context context) {
		deletePreference(PREF_INT_LAST_DAY_OFFSET_FOR_MONTHLY_VIEW, context);
	}
	
	
	public static void saveCurrentWalkId(Context context, String value) {
		saveString(PREF_STRING_CURRENT_WALK_ID, value, context);
	}
	public static String getCurrentWalkId(Context context) {
		return getString(PREF_STRING_CURRENT_WALK_ID, DEFAULT_CURRENT_WALK_ID, context);
	}
	public static void deleteCurrentWalkId(Context context) {
		deletePreference(PREF_STRING_CURRENT_WALK_ID, context);
	}
	
	public static void saveWeekLoadStatus(Context context, String value) {
		saveString(PREF_STRING_WEEK_LOAD_STATUS, value, context);
	}
	public static String getWeekLoadStatus(Context context) {
		return getString(PREF_STRING_WEEK_LOAD_STATUS, DEFAULT_WEEK_LOAD_STATUS, context);
	}
	public static void deleteWeekLoadStatus(Context context) {
		deletePreference(PREF_STRING_WEEK_LOAD_STATUS, context);
	}
	
	
	public static void saveSelectedYear(Context context, int value) {
		saveInteger(PREF_INT_SELECTED_YEAR, value, context);
	}
	public static int getSelectedYear(Context context) {
		return getInteger(PREF_INT_SELECTED_YEAR, DEFAULT_SELECTED_YEAR, context);
	}
	public static void deleteSelectedYear(Context context) {
		deletePreference(PREF_INT_SELECTED_YEAR, context);
	}
	
	public static void saveSelectedMonth(Context context, int value) {
		saveInteger(PREF_INT_SELECTED_MONTH, value, context);
	}
	public static int getSelectedMonth(Context context) {
		return getInteger(PREF_INT_SELECTED_MONTH, DEFAULT_SELECTED_MONTH, context);
	}
	public static void deleteSelectedMonth(Context context) {
		deletePreference(PREF_INT_SELECTED_MONTH, context);
	}
	
	public static void saveSelectedDay(Context context, int value) {
		saveInteger(PREF_INT_SELECTED_DAY, value, context);
	}
	public static int getSelectedDay(Context context) {
		return getInteger(PREF_INT_SELECTED_DAY, DEFAULT_SELECTED_DAY, context);
	}
	public static void deleteSelectedDay(Context context) {
		deletePreference(PREF_INT_SELECTED_DAY, context);
	}

	public static void saveSelectedFragmentIndexOnSchedule(Context context, int value) {
		saveInteger(PREF_INT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE, value, context);
	}
	public static int getSelectedFragmentIndexOnSchedule(Context context) {
		return getInteger(PREF_INT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE, DEFAULT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE, context);
	}
	public static void deleteSelectedFragmentIndexOnSchedule(Context context) {
		deletePreference(PREF_INT_SELECTED_FRAGMENT_INDEX_ON_SCHEDULE, context);
	}
	
	public static void saveTimeLastLoading(Context context, long value) {
		saveLong(PREF_LONG_TIME_LAST_LOADING, value, context);
	}


	public static void saveMonthLoadingResponce(Context context, int month,  String value) {
		saveString(MONTH_LOADING_RESPONCE + "_" + month, value, context);
	}

	public static String getMonthLoadingResponce(Context context, int month) {
		//saveLong(MONTH_LOADING_RESPONCE, value, context);
		return getString(MONTH_LOADING_RESPONCE + "_" + month, "nodata", context);

		//return getString(MONTH_LOADING_RESPONCE + "_" + month, "changedata", context);
	}
	/**
	 * Default value is Long.MAX_VALUE
	 * @param context
	 * @return
	 */
	public static long getTimeLastLoading(Context context) {
		return getLong(PREF_LONG_TIME_LAST_LOADING, DEFAULT_LONG_TIME_LAST_LOADING, context);
	}
	public static void deleteTimeLastLoading(Context context) {
		deletePreference(PREF_LONG_TIME_LAST_LOADING, context);
	}
	
	
	public static void saveGpsSleepTime(Context context, int value) {
		saveInteger(PREF_INT_GPS_SLEEP_TIME, value, context);
	}
	public static int getGpsSleepTime(Context context) {
		return getInteger(PREF_INT_GPS_SLEEP_TIME, DEFAULT_INT_GPS_SLEEP_TIME, context);
	}
	public static void deleteGpsSleepTime(Context context) {
		deletePreference(PREF_INT_GPS_SLEEP_TIME, context);
	}
	
	public static void saveMaxAttemptsToFindPoint(Context context, int value) {
		saveInteger(PREF_INT_MAX_ATTEMPTS_TO_FIND_POINTS, value, context);
	}
	public static int getMaxAttemptsToFindPoint(Context context) {
		return getInteger(PREF_INT_MAX_ATTEMPTS_TO_FIND_POINTS, DEFAULT_INT_MAX_ATTEMPTS_TO_FIND_POINTS, context);
	}
	public static void deleteMaxAttemptsToFindPoint(Context context) {
		deletePreference(PREF_INT_MAX_ATTEMPTS_TO_FIND_POINTS, context);
	}
	
	public static void saveThreshold(Context context, float value) {
		saveFloat(PREF_FLOAT_THRESHOLD, value, context);
	}
	public static float getThreshold(Context context) {
		return getFloat(PREF_FLOAT_THRESHOLD, DEFAULT_FLOAT_THRESHOLD, context);
	}
	public static void deleteThreshold(Context context) {
		deletePreference(PREF_FLOAT_THRESHOLD, context);
	}
	
	public static void saveLastMessage(Context context, String value) {
		saveString(PREF_STRING_LAST_MESSAGE, value, context);
	}
	public static String getLastMessage(Context context) {
		return getString(PREF_STRING_LAST_MESSAGE, DEFAULT_LAST_MESSAGE, context);
	}
	public static void deleteLastMessage(Context context) {
		deletePreference(PREF_STRING_LAST_MESSAGE, context);
	}
	
	public static void saveS3Key(Context context, String key) {
		saveString(PREF_STRING_S3_KEY, key, context);
	}
	
	public static String getS3Key(Context context) {
		return getString(PREF_STRING_S3_KEY, "", context);
	}

	public static void saveS3Secret(Context context, String secret) {
		saveString(PREF_STRING_S3_SECRET, secret, context);
	}
	
	public static String getS3Secret(Context context) {
		return getString(PREF_STRING_S3_SECRET, "", context);
	}
	public static void savePhotoTaken(String walkid,Context context, boolean secret) {
		saveBoolean(IS_PHOTO_TAKEN + "_" + walkid, secret, context);
	}
	
	public static boolean gatPhotoTaken(String walkid,Context context) {
		
		return getBoolean(IS_PHOTO_TAKEN + "_" + walkid, false, context);
	}
	
	
	public static void saveGpsOffTime(String walkid,Context context, long offtime)
	{
		saveLong(GPS_OFF_TIME + "_" + walkid, offtime, context);
	}
	
	public static Long getGpsOffTime(String walkid,Context context)
	{
		return getLong(GPS_OFF_TIME + "_" + walkid, 0L, context);
	}
	
	public static void saveGpsOffTimeExact(String walkid,Context context, long offtime)
	{
		saveLong(GPS_OFF_TIME_EXACT+"_"+walkid, offtime, context);
	}

	public static Long getGpsOffTimeExact(String walkid,Context context)
	{
		return getLong(GPS_OFF_TIME_EXACT+"_"+walkid, System.currentTimeMillis(), context);
	}


	public static Long getGpsOffTimeDuration(String walkid,Context context)
	{
		return getLong(GPS_OFF_TIME_DURATION+"_"+walkid, 0L,context);
	}

	public static void saveGpsOffTimeDuration(String walkid,Context context, long offtime)
	{
		saveLong(GPS_OFF_TIME_DURATION+"_"+walkid, offtime, context);
	}


	// Device

	public static Long getDeviceOffTimeDuration(String walkid,Context context)
	{
		return getLong(DEVICE_OFF_TIME_DURATION+"_"+walkid, 0L,context);
	}

	public static void saveDeviceOffTimeDuration(String walkid,Context context, long offtime)
	{
		saveLong(DEVICE_OFF_TIME_DURATION+"_"+walkid, offtime, context);
	}

	public static void saveDeviceOffTimeExact(String walkid,Context context, long offtime)
	{
		saveLong(DEVICE_OFF_TIME_EXACT+"_"+walkid, offtime, context);
	}

	public static Long getDeviceOffTimeExact(String walkid,Context context)
	{
		return getLong(DEVICE_OFF_TIME_EXACT+"_"+walkid, 0L, context);
	}


	public static void saveWalkOffTimeExact(String walkid,Context context, long offtime)
	{
		saveLong(WALK_OFF_TIME_EXACT+"_"+walkid, offtime, context);
	}

	public static Long getWalkOffTimeExact(String walkid,Context context)
	{
		return getLong(WALK_OFF_TIME_EXACT+"_"+walkid, 0L, context);
	}


	public static void saveWalkOnTimeExact(Context context, long offtime)
	{
		saveLong(WALK_ON_TIME_EXACT, offtime, context);
	}

	public static Long getWalkOnTimeExact(Context context)
	{
		return getLong(WALK_ON_TIME_EXACT, 0L, context);
	}

	//  massage

	public static void saveWalkMSG(String walkid,Context context, String offtime)
	{
		saveString(WALK_MESSAGE + "_" + walkid, offtime, context);
	}

	public static String getWalkMSG(String walkid,Context context)
	{
		return getString(WALK_MESSAGE + "_" + walkid, "no", context);
	}

	//WALK_MESSAGE  WALK_IMAGE

	//  image

	public static void saveWalkImage(Context context, String offtime)
	{
		saveString(WALK_IMAGE , offtime, context);
	}

	public static String getWalkImage(Context context)
	{
		return getString(WALK_IMAGE , "no", context);
	}


	public static void saveWalkImageByDog(Context context, String walkid,String dogid)
	{
		saveString(WALK_IMAGE+"_"+walkid+"_"+dogid , dogid, context);
	}

	public static String getWalkImageByDog(Context context,  String walkid,String dogid)
	{
		return getString(WALK_IMAGE+"_"+walkid+"_"+dogid, "no", context);
	}



	public static void saveWalkImageProcess(Context context, String offtime)
	{
		saveString(WALK_IMAGE+"_Process" , offtime, context);
	}

	public static String getWalkImageProcess(Context context)
	{
		return getString(WALK_IMAGE+"_Process" , "no", context);
	}


	public static void saveWalkImageIsCompressError(Context context, String offtime)
	{
		saveString(WALK_IMAGE+"_IsCompressError" , offtime, context);
	}

	public static String getWalkImageIsCompressError(Context context)
	{
		return getString(WALK_IMAGE+"_IsCompressError" , "no", context);
	}

	//

	
	public static void saveIsGpsOff(Context context, boolean secret) {
		saveBoolean(IS_GPS_OFF, secret, context);
	}
	
	public static boolean getIsGpsOff(Context context) {
		
		return getBoolean(IS_GPS_OFF, false, context);
	}
	
	/*
	 	 Strings
	 */
	
	private static String getString(String preferenceName, String defaultValue, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		//Log.i("defaultValue currWalkId 4", ""+defaultValue);
		Log.i("return currWalkId 5", ""+sharedPrefs.getString(preferenceName, defaultValue));
		return sharedPrefs.getString(preferenceName, defaultValue);
	}
	
	private static void saveString(String preferenceName, String value, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.putString(preferenceName, value);
		editor.commit();
	}
	
	/*
	  	Booleans
	 */
	
	private static Boolean getBoolean(String preferenceName, Boolean defaultValue, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		
		return sharedPrefs.getBoolean(preferenceName, defaultValue);
	}
	private static void saveBoolean(String preferenceName, Boolean value, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.putBoolean(preferenceName, value);
		editor.commit();
	}
	
	/*
	  	Integers
	 */
	
	private static Integer getInteger(String preferenceName, Integer defaultValue, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		return sharedPrefs.getInt(preferenceName, defaultValue);
	}
	
	private static void saveInteger(String preferenceName, Integer value, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.putInt(preferenceName, value);
		editor.commit();
	}

	private static Long getLong(String preferenceName, Long defaultValue, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		return sharedPrefs.getLong(preferenceName, defaultValue);
	}
	
	private static void saveLong(String preferenceName, Long value, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.putLong(preferenceName, value);
		editor.commit();
	}
	
	
	
	private static Float getFloat(String preferenceName, Float defaultValue, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		return sharedPrefs.getFloat(preferenceName, defaultValue);
	}
	
	private static void saveFloat(String preferenceName, Float value, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.putFloat(preferenceName, value);
		editor.commit();
	}
	
	
	
	/*
	  	Commons
	 */
	
	private static void deletePreference(String preferenceName, Context context) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		editor.remove(preferenceName);
		editor.commit();
	}
	
	public static void deleteTemporaryPreferences(Context context) {
		deletePreference(PREF_STRING_STARTED_WALK_ID, context);
	}
	
	

}
