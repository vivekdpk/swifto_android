package com.haski.swifto.model.vo.json;

import com.google.android.maps.GeoPoint;
import com.haski.swifto.model.GeoPointExtWithData;
import com.haski.swifto.model.StartedWalk;
import com.haski.swifto.model.vo.EnumWalkActivityType;
import com.haski.swifto.model.vo.GeoPointExt;
import com.haski.swifto.model.vo.WalkerActiveRouteInfo;
import com.haski.swifto.util.GeoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetWalkerActiveRoutesParser  {
	
	interface COMMON {
		String TS = "ts";
	}
	
	interface ROUTES {
		String NAME = "routes";
		String WALK_ID = "_walkId";
	}
	
	interface ACTIVITY {
		String NAME = "activity";
		String ROUTE = "route";
		String TYPE = "type";
		String POINT = "point";
	}
	
	interface ROUTE {
		String NAME = "route";
		String LOC = "loc";
	}
	
	public ArrayList<WalkerActiveRouteInfo> parseGetWalkerActiveRoutes(String toParse) {
		ArrayList<WalkerActiveRouteInfo> routesInfo = new ArrayList<WalkerActiveRouteInfo>();
		
		try {
			JSONObject root = new JSONObject(toParse);
			
			if(root.has(ROUTES.NAME)) {
				JSONArray routes = root.getJSONArray(ROUTES.NAME);
				
				for(int i = 0; i < routes.length(); i++) {
					routesInfo.add(parseOneRouteInfo(routes.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return routesInfo;
	}
	
	private WalkerActiveRouteInfo parseOneRouteInfo(JSONObject jRoute) {
		WalkerActiveRouteInfo route = new WalkerActiveRouteInfo();
		
		route.StartedWalkWithoutRelatedWalk = new StartedWalk();
		
		try {
			if(jRoute.has(ROUTES.WALK_ID)) {
				route.WalkId = jRoute.getString(ROUTES.WALK_ID);
			}

			if(jRoute.has(ACTIVITY.NAME)) {
				JSONObject jActivity = jRoute.getJSONObject(ACTIVITY.NAME);

				if(jActivity.has(ROUTE.NAME)) {
					JSONArray jPoints = jActivity.getJSONArray(ROUTE.NAME);
					JSONObject jPoint;
					long timespan;
					GeoPoint gp;
					GeoPointExt geoPointExt;
					
					for(int iPoint = 0; iPoint < jPoints.length(); iPoint++) {
						jPoint = jPoints.getJSONObject(iPoint);
						
						if(jPoint.has(COMMON.TS) && jPoint.has(ROUTE.LOC)) {
							timespan = jPoint.getLong(COMMON.TS);
							
							//?? ????? timespan - ? ????????
							timespan = timespan * 1000;
							gp = parseLocationFromArray(jPoint.getJSONArray(ROUTE.LOC), false);
							
							geoPointExt = new GeoPointExt(gp.getLatitudeE6(), gp.getLongitudeE6(), timespan);
							
							route.StartedWalkWithoutRelatedWalk.getPoints().add(geoPointExt);
						}
					}
				}
				
				int numberOfPoints = route.StartedWalkWithoutRelatedWalk.getPoints().size();
				
				//...?????? ????? ???????? "?????" ?????? "????????", ?.?. ? ?????? "????????" ???? ????? ???????? ?????? ?? "?????"
				//????? ????? ???? ??????? - ??? ??????, ??? ???????? ?????? ???? ??????, ?? ?????????? GPS ?? ???? ?? ???? ?????????? ?? ??????
				if(jActivity.has(ACTIVITY.NAME)) {
					JSONArray jActivities = jActivity.getJSONArray(ACTIVITY.NAME);
					JSONObject jAct;
					String activityType;
					long timespanActivity;
					int indexPoint;

					for(int iAct = 0; iAct < jActivities.length(); iAct++) {
						jAct = jActivities.getJSONObject(iAct);

						if(jAct.has(ACTIVITY.TYPE) && jAct.has(ACTIVITY.POINT) && jAct.has(COMMON.TS)) {
							//in seconds
							timespanActivity = jAct.getLong(COMMON.TS);

							activityType = jAct.getString(ACTIVITY.TYPE);

							indexPoint = jAct.getInt(ACTIVITY.POINT);

							if(activityType.equals(EnumWalkActivityType.STARTED)) {
								route.StartedWalkWithoutRelatedWalk.setStartTime(timespanActivity * 1000);
							}
							
							if(numberOfPoints > indexPoint) {
								if(activityType.equals(EnumWalkActivityType.MEET)) {
									GeoPointExt gpe = route.StartedWalkWithoutRelatedWalk.getPoints().get(indexPoint);
									GeoPointExtWithData gpeMeet = new GeoPointExtWithData(gpe.getLatitudeE6(), gpe.getLongitudeE6());
									gpeMeet.setData("");

									route.StartedWalkWithoutRelatedWalk.getPointsMeet().add(gpeMeet);
								} else if(activityType.equals(EnumWalkActivityType.POOP)) {
									GeoPointExt gpe = route.StartedWalkWithoutRelatedWalk.getPoints().get(indexPoint);
									GeoPointExtWithData gpePoop = new GeoPointExtWithData(gpe.getLatitudeE6(), gpe.getLongitudeE6());
									gpePoop.setData("");

									route.StartedWalkWithoutRelatedWalk.getPointsPoo().add(gpePoop);
								} else {
									//UNSUPPORTED ACTIVITY TYPE received from server
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return route;
	}
	
	private GeoPoint parseLocationFromArray(JSONArray jLocation, boolean swapLatLng) {
		if(jLocation != null && jLocation.length() > 0) {
			double lat;
			double lng;
			try {
				//TODO: ??????????? ? ???????? ? ???????
				
				if(!swapLatLng)
				{
					lat = jLocation.getDouble(0);
					lng = jLocation.getDouble(1);
				} else {
					lat = jLocation.getDouble(1);
					lng = jLocation.getDouble(0);
				}
				return new GeoPoint((int)(lat * GeoUtils.MULTIPLEXOR), (int)(lng * GeoUtils.MULTIPLEXOR));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
