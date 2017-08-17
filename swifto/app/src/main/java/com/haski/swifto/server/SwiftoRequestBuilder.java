package com.haski.swifto.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.os.Build;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.GeoPointExtWithData;
import com.haski.swifto.model.vo.GeoPointExt;
import com.haski.swifto.model.vo.WalkGPSPoint;
import com.haski.swifto.server.SwiftoRequestBuilder.API.ADD_POINTS;
import com.haski.swifto.server.SwiftoRequestBuilder.API.EVENT_LOG;
import com.haski.swifto.server.SwiftoRequestBuilder.API.GET_CONFIG;
import com.haski.swifto.server.SwiftoRequestBuilder.API.GET_ROUTE;
import com.haski.swifto.server.SwiftoRequestBuilder.API.GET_WALKER_ACTIVE_ROUTES;
import com.haski.swifto.server.SwiftoRequestBuilder.API.GET_WALKS_NUMBER_PER_MONTH;
import com.haski.swifto.server.SwiftoRequestBuilder.API.GET_WALK_STATE;
import com.haski.swifto.server.SwiftoRequestBuilder.API.SEND_MESSAGE;
import com.haski.swifto.util.CalendarUtils;
import com.haski.swifto.util.GeoUtils;
import com.haski.swifto.util.SharedPreferencesHelper;
import com.haski.swifto.util.StringUtils;

public class SwiftoRequestBuilder {
	
	
	//---------------------------------------------------------------------
	//
	//			API params and names
	//
	//------------------------------
	
	interface PORTS {
		String PRODUCTION = "7000";
		String STAGING = "7001";
		String DEVELOPMENT = "7002";
	}
	
	interface API {
		interface LOGIN {
			String NAME = "login";
			
			String USERNAME = "username";
			String PASSWORD = "password";
			String MODEL = "model";
			String OS_VERSION = "osVersion";
		}
		
		interface GET_WALKS {
			String NAME = "get_walks";
			
			String FROM = "from";
			String TO = "to";
		}
		
		interface START_WALK {
			String NAME = "start_walk";
		}
		
		interface STOP_WALK {
			String NAME = "stop_walk";
		}
		
		interface ADD_POOP {
			String NAME = "add_poop";
		}
		
		interface ADD_MEET {
			String NAME = "add_meet";
		}
		
		interface UPLOAD_PHOTO {
			String NAME = "upload_photo";
		}
		
		interface ADD_POINTS {
			String NAME = "add_points";
			String POINTS = "points";
		}
		
		interface SEND_MESSAGE {
			String NAME = "send_message";
			String CATEGORY = "category";
			String MESSAGE = "message";
		}
		
		interface RESET_WALK {
			String NAME = "restart_walk";
		}
		
		interface EVENT_LOG {
			String NAME = "event_log";
			String CATEGORY = "category";
			String MESSAGE = "message";
			String PRIORITY = "priority";
		}
		
		interface GET_WALK_STATE {
			String NAME = "get_walk_state";
		}
		
		interface GET_ROUTE {
			String NAME = "get_routes";
		}
		
		interface GET_WALKER_ACTIVE_ROUTES {
			String NAME = "get_walker_active_routes";
		}
		
		interface GET_WALKS_NUMBER_PER_MONTH {
			String NAME = "get_walks_number_for_month";
			String YEAR = "year";
			String MONTH = "month";
			String GMT_OFFSET = "GMTOffset";
		}
		
		interface GET_CONFIG {
			String NAME = "get_config";
			String MODEL = "model";
			String VERSION = "version";
		}
	}
	
	interface PARAMS {
		String TS = "ts";
		String WALKER_ID = "walkerId";
		String WALK_ID = "walkId";
		String DOG_ID = "dogId";
		String LAT = "lat";
		String LNG = "lng";
		String VERSION = "version";
	}
	
	
	
	private static final String PROTOCOL = "https://";

	//https://falling-wind-5637.herokuapp.com/photos/
	// walk_photos/key/walkId=56254fe47bbab103000012b0


	
	private static final String HOST_PROD = "swifto-production-api.herokuapp.com";
	private static final String HOST_STAGE = "swifto-staging-api.herokuapp.com";
	private static final String PHOTO_HOST_PROD = "falling-wind-5637.herokuapp.com/photos";
	private static final String PHOTO_HOST_STAGE = "swifto-photos-stage.herokuapp.com/photos";
	
	//TODO: change in release
	//  Mahalah Walker  	mahalah987
	
	private static final String HOST = HOST_PROD;
	private static final String PHOTO_HOST = PHOTO_HOST_PROD;
	
	//private static final String HOST = HOST_STAGE;
	//private static final String PHOTO_HOST = PHOTO_HOST_STAGE;
	
	private static final String APP_VERSION = "1.120.0";
	private static final String BASE_PATH = "0.1/app";
	private static final String KEY = "asd";
	
	private static String AMPERSAND = "&";
	//---------------------------------------------------------------------
	//
	//			Builders
	//
	//------------------------------
	
	
	/**https://api.swifto.com:7002/0.1/app/login/asd/username=fake%20dave%20walker&password=123456&model=Phone Model: xxx, Android ver.yyy&ts=123*/
	public static String buildLoginRequest(String username, String password) {
		StringBuilder builder = buildWithoutParams(API.LOGIN.NAME);

		password = password.replace("&","&enc=1");

		appendParams(builder, API.LOGIN.USERNAME, username);
		builder.append(AMPERSAND);
		appendParams(builder, API.LOGIN.PASSWORD, password);
		builder.append(AMPERSAND);
		try {
			appendParams(builder, API.LOGIN.MODEL, URLEncoder.encode(Build.MODEL, "UTF-8"));
			builder.append(AMPERSAND);
			appendParams(builder, API.LOGIN.OS_VERSION, URLEncoder.encode(Build.VERSION.RELEASE, "UTF-8"));
			builder.append(AMPERSAND);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		appendTimestamp(builder);
		
		String loginRequest = builder.toString();
		
		loginRequest = StringUtils.replaceSpaces(loginRequest);
		
		return loginRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/get_walks/asd/walkerId=4fd097179418e67e3a00000f&ts=123&from=2000&to=100000*/
	public static String buildGetWalks(String walkerId, double from, double to) {
		StringBuilder builder = buildWithoutParams(API.GET_WALKS.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		
		if(from != -1) {
			appendParams(builder, API.GET_WALKS.FROM, String.format("%f", from));
			builder.append(AMPERSAND);
		}

		if(to != -1) {
			appendParams(builder, API.GET_WALKS.TO, String.format("%f", to));
			builder.append(AMPERSAND);
		}
		
		appendTimestamp(builder);
		
		String getWalksRequest = builder.toString();
		getWalksRequest = StringUtils.replaceSpaces(getWalksRequest);
		return getWalksRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/start_walk/asd/walkerId=4fd097179418e67e3a00000f&ts=1&walkId=4fd09f6f9418e67e3a0000d2*/
	public static String buildStartWalk(String walkerId, String walkId) {
		StringBuilder builder = buildWithoutParams(API.START_WALK.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String startWalkRequest = builder.toString();
		startWalkRequest = StringUtils.replaceSpaces(startWalkRequest);
		return startWalkRequest;
	}

	/**https://api.swifto.com:7002/0.1/app/stop_walk/asd/walkerId=4fd097179418e67e3a00000f&ts=1&walkId=4fd09f6f9418e67e3a0000d2*/
	public static String buildStopWalk(String walkerId, String walkId , long walkOffTime) {
		StringBuilder builder = buildWithoutParams(API.STOP_WALK.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);

		//long gpsofftime = SharedPreferencesHelper.getDeviceOffTimeExact(walkId, mContext);
		//appendTimestamp(builder);

		appendTimestamp(builder , walkOffTime);

		String stopWalkRequest = builder.toString();
		stopWalkRequest = StringUtils.replaceSpaces(stopWalkRequest);
		return stopWalkRequest;
	}

	/**https://api.swifto.com:7002/0.1/app/restart_walk/asd/walkerId=4fd097179418e67e3a00000f&ts=1&walkId=4fd09f6f9418e67e3a0000d2*/
	public static String buildResetWalk(String walkerId, String walkId) {
		StringBuilder builder = buildWithoutParams(API.RESET_WALK.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String resetWalkRequest = builder.toString();
		resetWalkRequest = StringUtils.replaceSpaces(resetWalkRequest);
		return resetWalkRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/get_walk_state/asd/walkId=4fd09f6f9418e67e3a0000d2&ts=123*/
	public static String buildGetWalkState(String walkId) {
		StringBuilder builder = buildWithoutParams(GET_WALK_STATE.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String getWalkStateRequest = builder.toString();
		getWalkStateRequest = StringUtils.replaceSpaces(getWalkStateRequest);
		//nkp
		Log.d("getWalkStateRequest", getWalkStateRequest);
		return getWalkStateRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/get_routes/asd/walkId=4fd09f479418e67e3a000091&ts=123*/
	public static String buildGetRoute(String walkId, String dogId) {
		StringBuilder builder = buildWithoutParams(GET_ROUTE.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		if(dogId != "") {
			appendParams(builder, PARAMS.DOG_ID, dogId);
			builder.append(AMPERSAND);
		}
		
		appendTimestamp(builder);
		
		String getRouteRequest = builder.toString();
		getRouteRequest = StringUtils.replaceSpaces(getRouteRequest);
		return getRouteRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/get_walker_active_routes/asd/walkerId=4fd097179418e67e3a00000f&ts=1*/
	public static String buildGetWalkerActiveRoutes(String walkerId) {
		StringBuilder builder = buildWithoutParams(GET_WALKER_ACTIVE_ROUTES.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String getWalkerActiveRoutesRequest = builder.toString();
		getWalkerActiveRoutesRequest = StringUtils.replaceSpaces(getWalkerActiveRoutesRequest);
		return getWalkerActiveRoutesRequest;
	}
	
	
	/**https://api.swifto.com:7002/0.1/app/get_walks_number_for_month/asd/month=July&year=2012&walkerId=4fd097179418e67e3a00000f&ts=123*/
	public static String buildGetWalksNumberPerMonth(String walkerId, String monthName, int GMTOffset, int year) {
		StringBuilder builder = buildWithoutParams(GET_WALKS_NUMBER_PER_MONTH.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		
		appendParams(builder, GET_WALKS_NUMBER_PER_MONTH.MONTH, monthName);
		builder.append(AMPERSAND);
		
		if(year != -1) {
			appendParams(builder, GET_WALKS_NUMBER_PER_MONTH.YEAR, year);
			builder.append(AMPERSAND);
		}
		
		appendParams(builder, GET_WALKS_NUMBER_PER_MONTH.GMT_OFFSET, GMTOffset);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String getWalkesNumberPerMonthRequest = builder.toString();
		getWalkesNumberPerMonthRequest = StringUtils.replaceSpaces(getWalkesNumberPerMonthRequest);
		return getWalkesNumberPerMonthRequest;
	}
	
	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddPoop(String walkId, GeoPointExtWithData dogPoopData) {
		StringBuilder builder = buildWithoutParams(API.ADD_POOP.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.DOG_ID, dogPoopData.getData());
		builder.append(AMPERSAND);
		
		appendParams(builder, PARAMS.LAT, dogPoopData.getLatitudeE6());
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.LNG, dogPoopData.getLongitudeE6());
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String addPoopRequest = builder.toString();
		addPoopRequest = StringUtils.replaceSpaces(addPoopRequest);
		return addPoopRequest;
	}

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddPoopWithTimestamp(String walkId, WalkGPSPoint point, String dogId, long timestamp) {
		StringBuilder builder = buildWithoutParams(API.ADD_POOP.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.DOG_ID, dogId);
		builder.append(AMPERSAND);
		
		appendParams(builder, PARAMS.LAT, point.getLatitudeE6());
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.LNG, point.getLongitudeE6());
		builder.append(AMPERSAND);
		
		appendTimestamp(builder, timestamp);
		
		String addPoopRequest = builder.toString();
		addPoopRequest = StringUtils.replaceSpaces(addPoopRequest);
		return addPoopRequest;
	}

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddMeet(String walkId, GeoPointExtWithData dogPlayData) {
		StringBuilder builder = buildWithoutParams(API.ADD_MEET.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.DOG_ID, dogPlayData.getData());
		builder.append(AMPERSAND);
		
		appendParams(builder, PARAMS.LAT, dogPlayData.getLatitudeE6());
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.LNG, dogPlayData.getLongitudeE6());
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String addMeet = builder.toString();
		addMeet = StringUtils.replaceSpaces(addMeet);
		return addMeet;
	}

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddMeetWithTimestamp(String walkId, WalkGPSPoint point, String dogId, long timestamp) {
		StringBuilder builder = buildWithoutParams(API.ADD_MEET.NAME);
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.DOG_ID, dogId);
		builder.append(AMPERSAND);
		
		appendParams(builder, PARAMS.LAT, point.getLatitudeE6());
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.LNG, point.getLongitudeE6());
		builder.append(AMPERSAND);
		
		appendTimestamp(builder, timestamp);
		
		String addMeet = builder.toString();
		addMeet = StringUtils.replaceSpaces(addMeet);
		return addMeet;
	}

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddPoints(String walkerId, String walkId, ArrayList<GeoPointExt> points) {
		StringBuilder builder = buildWithoutParams(API.ADD_POINTS.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		//builder.append(ADD_POINTS.POINTS);
		//builder.append("=");
		String pointsArray = appendPointsArray(points);
		builder.append(pointsArray);
		
		builder.append(AMPERSAND);
		appendTimestamp(builder);
		
		String addPoints = builder.toString();
		addPoints = StringUtils.replaceSpaces(addPoints);
		return addPoints;
	}


	//https://falling-wind-5637.herokuapp.com/photos/walk_photos/key/walkId=56254fe47bbab103000012b0

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddPointsWalkGps(String walkerId, String walkId, ArrayList<WalkGPSPoint> points) {
		StringBuilder builder = buildWithoutParams(API.ADD_POINTS.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		//builder.append(ADD_POINTS.POINTS);
		//builder.append("=");
		String pointsArray = appendPointsArrayWalkGps(points);
		builder.append(pointsArray);
		
		builder.append(AMPERSAND);
		appendTimestamp(builder);
		
		String addPoints = builder.toString();
		addPoints = StringUtils.replaceSpaces(addPoints);
		
		//Log.d("Swifto", addPoints);
		
		return addPoints;
	}

	/**https://api.swifto.com:7002/0.1/app/add_poop/asd/dogId=4fd099ab9418e67e3a000021&ts=123*/
	public static String buildAddPointsWithOnePoint(String walkerId, String walkId, GeoPointExt point) {
		StringBuilder builder = buildWithoutParams(API.ADD_POINTS.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);
		
		//builder.append(ADD_POINTS.POINTS);
		//builder.append("=");
		//String pointsArray = appendPointsArray(points);
		String pointStr = appendPoint(point);
		builder.append(pointStr);
		
		builder.append(AMPERSAND);
		appendTimestamp(builder);
		
		String addPoints = builder.toString();
		addPoints = StringUtils.replaceSpaces(addPoints);
		
		return addPoints;
	}
	
	/**https://api.swifto.com:7002/0.1/app/send_message/asd/category=walkerNote&message=some%20messgae&walkerId=4fd097179418e67e3a00000f&ts=1&walkId=4fd09f6f9418e67e3a0000d2*/
	public static String buildSendMessage(String walkerId, String walkId, String category, String utfEncodedMessage) {
		StringBuilder builder = buildWithoutParams(SEND_MESSAGE.NAME);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);

		appendParams(builder, SEND_MESSAGE.CATEGORY, category);
		builder.append(AMPERSAND);
		appendParams(builder, SEND_MESSAGE.MESSAGE, utfEncodedMessage);
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String sendMessage = builder.toString();
		
		return sendMessage;
	}
	
	//https://api.swifto.com/photos/upload/android/key/dogId=xyz&walkerId=abc&filename=12123123.jpg
	public static String buildUploadedPhotoRequest(String walkId, String dogId, String walkerId, String fileName) {
		StringBuilder builder = new StringBuilder();
		builder.append(PROTOCOL + PHOTO_HOST + "/upload/android/");
		
		builder.append(KEY);
		builder.append("/");
		
		appendParams(builder, PARAMS.WALK_ID, walkId);
		builder.append(AMPERSAND);

		appendParams(builder, PARAMS.DOG_ID, dogId);
		builder.append(AMPERSAND);
		
		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);
		
		appendParams(builder, "filename", fileName);
		
		String toRet = builder.toString();
		
		return toRet;		
	}


	//https://falling-wind-5637.herokuapp.com/photos/walk_photos/key/walkId=56254fe47bbab103000012b0
	public static String buildCheckPhotoRequest(String walkId) {
		StringBuilder builder = new StringBuilder();
		builder.append(PROTOCOL + PHOTO_HOST + "/walk_photos/key/");

		//builder.append(KEY);
		//builder.append("/");

		appendParams(builder, PARAMS.WALK_ID, walkId);
		//builder.append(AMPERSAND);

		String toRet = builder.toString();

		return toRet;
	}
	
	//https://api.swifto.com:7002/0.1/app/event_log/asd/category=warning&walkerId=123&priority=low&message=this%20is%20a%20test%20log&ts=123
	public static String buildEventLogWarningRequest(String walkerId, String message) {
		StringBuilder builder = buildWithoutParams(EVENT_LOG.NAME);
		
		appendParams(builder, EVENT_LOG.CATEGORY, "warning");
		builder.append(AMPERSAND);

		appendParams(builder, PARAMS.WALKER_ID, walkerId);
		builder.append(AMPERSAND);

		appendParams(builder, EVENT_LOG.PRIORITY, "low");
		builder.append(AMPERSAND);

		try {
			appendParams(builder, SEND_MESSAGE.MESSAGE, URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		builder.append(AMPERSAND);
		
		appendTimestamp(builder);
		
		String toRet = builder.toString();
		toRet = StringUtils.replaceSpaces(toRet);
		return toRet;
	}
	
	public static String buildGetGpsConfigRequest(String phoneModel, String osVersion) {
		StringBuilder builder = buildWithoutParams(GET_CONFIG.NAME);
		
		try {
			appendParams(builder, GET_CONFIG.MODEL, URLEncoder.encode(phoneModel, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		builder.append(AMPERSAND);
		
		appendParams(builder, GET_CONFIG.VERSION, osVersion);
		
		builder.append(AMPERSAND);
		
	    appendTimestamp(builder);
		
		return builder.toString();
	}
	
	
	//---------------------------------------------------------------------
	//
	//			Private helpers
	//
	//------------------------------
	
	/**https://api.swifto.com:7001/app/API_NAME/KEY/*/
	private static StringBuilder buildWithoutParams(String apiName) {
		StringBuilder builder = new StringBuilder();
		builder.append(PROTOCOL);
		builder.append(HOST);
		builder.append("/");
		//builder.append(PORT);
		//builder.append("/");
		builder.append(BASE_PATH);
		builder.append("/");
		
		builder.append(apiName);
		builder.append("/");
		
		builder.append(KEY);
		builder.append("/");
		
		builder.append(PARAMS.VERSION);
		builder.append("=");
		builder.append(APP_VERSION);
		builder.append(AMPERSAND);
		
		return builder;
	}
	/**https://api.swifto.com:7001/app/API_NAME/KEY/*/

	private static void appendParams(StringBuilder appendee, String paramName, String value) {
		appendee.append(paramName + "=" + value);
	}

	private static void appendParams(StringBuilder appendee, String paramName, long value) {
		appendee.append(paramName + "=" + value);
	}
	
	private static void appendTimestamp(StringBuilder appendee) {
		appendee.append("ts=");
		appendee.append(StringUtils.GET_DECIMAL_FORMAT().format(CalendarUtils.NOW_IN_SECONDS()));
	}

	/**
	 * 
	 * @param appendee
	 * @param toAppend - milliseconds, which will be divided by 1000
	 */
	private static void appendTimestamp(StringBuilder appendee, long toAppend) {
		appendee.append("ts=");
		appendee.append(StringUtils.GET_DECIMAL_FORMAT().format(CalendarUtils.SECONDS(toAppend)));

		//appendee.append(StringUtils.GET_DECIMAL_FORMAT().format(toAppend));
	}
	
	/**[{"lat":37.33096, "lng":88.33881992},{"lat":39.33096, "lng":89.33881992}]*/
	private static String appendPointsArray(ArrayList<GeoPointExt> points) {
		StringBuilder appendee = new StringBuilder();
		appendee.append("[");

		for(GeoPoint point : points) {
			//{"lat":37.33096, "lng":88.33881992},
			
			appendee.append("{"); 												//{
			
			appendee.append("\"lat\":"); 										//{"lat:
			appendee.append(point.getLatitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096
			
			appendee.append(",\"lng\":");									//{"lat:37.33096, "lng":
			appendee.append(point.getLongitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096, "lng":88.33388282
			
			appendee.append("}");											//{"lat:37.33096, "lng":88.33388282}
			
			appendee.append(",");											//{"lat:37.33096, "lng":88.33388282},	
		}
		
		//remove last comma
		appendee.delete(appendee.length()-1, appendee.length());
		
		appendee.append("]");
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(ADD_POINTS.POINTS, appendee.toString()));
		
		String toRet = URLEncodedUtils.format(params, "utf-8");
		return toRet;
	}

	/**[{"lat":37.33096, "lng":88.33881992},{"lat":39.33096, "lng":89.33881992}]*/
	private static String appendPointsArrayWalkGps(ArrayList<WalkGPSPoint> points) {
		StringBuilder appendee = new StringBuilder();
		appendee.append("[");

		for(WalkGPSPoint point : points) {
			//{"lat":37.33096, "lng":88.33881992},
			
			appendee.append("{"); 												//{
			
			appendee.append("\"lat\":"); 										//{"lat:
			appendee.append(point.getLatitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096
			
			appendee.append(",\"lng\":");										//{"lat:37.33096, "lng":
			appendee.append(point.getLongitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096, "lng":88.33388282

			appendee.append(",\"acc\":");										//{"lat:37.33096, "lng":88.33388282, "acc":
			appendee.append(point.Accuracy);									//{"lat:37.33096, "lng":88.33388282, "acc":10

			appendee.append(",\"ts\":");										//{"lat:37.33096, "lng":88.33388282, "acc":
			appendee.append(StringUtils.GET_DECIMAL_FORMAT().format(CalendarUtils.SECONDS(point.getTimestamp())));								//{"lat:37.33096, "lng":88.33388282, "acc":10
			
			appendee.append("}");												//{"lat:37.33096, "lng":88.33388282, "acc":10}
			
			appendee.append(",");												//{"lat:37.33096, "lng":88.33388282, "acc":10},
		}
		
		//remove last comma
		appendee.delete(appendee.length()-1, appendee.length());
		
		appendee.append("]");
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(ADD_POINTS.POINTS, appendee.toString()));
		
		String toRet = URLEncodedUtils.format(params, "utf-8");
		return toRet;
	}

	/**[{"lat":37.33096, "lng":88.33881992},{"lat":39.33096, "lng":89.33881992}]*/
	private static String appendPoint(GeoPointExt point) {
		StringBuilder appendee = new StringBuilder();
		appendee.append("[");

		appendee.append("{"); 												//{

		appendee.append("\"lat\":"); 										//{"lat:
		appendee.append(point.getLatitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096

		appendee.append(",\"lng\":");										//{"lat:37.33096, "lng":
		appendee.append(point.getLongitudeE6() / GeoUtils.MULTIPLEXOR);		//{"lat:37.33096, "lng":88.33388282

		appendee.append("}");												//{"lat:37.33096, "lng":88.33388282}
			
		appendee.append("]");
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(ADD_POINTS.POINTS, appendee.toString()));
		
		String toRet = URLEncodedUtils.format(params, "utf-8");
		
		return toRet;
	}
}
