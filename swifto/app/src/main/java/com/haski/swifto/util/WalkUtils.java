package com.haski.swifto.util;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import com.haski.swifto.model.vo.dog.Dog;
import com.haski.swifto.model.vo.walk.Walk;

public class WalkUtils {
	
	public static String STATUS_SCHEDULED = "scheduled";
	public static String STATUS_STARTED = "started";
	public static String STATUS_COMPLETED = "completed";

	/**startTime - */ 
	public static String getIdentifier(Walk walk)
	{
		Calendar cal1 = CalendarUtils.MIDNIGHT_FROM_SECONDS(walk.StartTime);
		Calendar cal2 = CalendarUtils.D23_59_FROM_SECONDS(walk.StartTime);
		
		long sec1 = cal1.getTimeInMillis() / 1000;
		long sec2 = cal2.getTimeInMillis() / 1000;
		
		String id = String.format("%d-%d", sec1, sec2);
		
		return id;
	}
	
	public static String getIdentifier(long seconds)
	{
		Calendar cal1 = CalendarUtils.MIDNIGHT_FROM_SECONDS(seconds);
		Calendar cal2 = CalendarUtils.D23_59_FROM_SECONDS(seconds);
		
		long sec1 = cal1.getTimeInMillis() / 1000;
		long sec2 = cal2.getTimeInMillis() / 1000;
		
		String id = String.format("%d-%d", sec1, sec2);
		
		return id;
	}
	
	public static String getDogIDs(Walk walk)
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < walk.Dogs.size(); i++)
		{
			Dog dog = walk.Dogs.get(i);
			builder.append(dog._id);
			
			if(i < walk.Dogs.size() - 1)
			{
				builder.append(",");
			}
		}
		
		return builder.toString();
	}
	
	public static String getStatus(JSONArray statuses)
	{
		String toRet = "";
		
		if(statuses.length() > 0)
		{
			try {
				toRet = (String) statuses.get(statuses.length()-1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		/*
		if(toRet.equals("started"))
		{
			//int z = 0;
		}
		*/
		return toRet;
	}
	
}
