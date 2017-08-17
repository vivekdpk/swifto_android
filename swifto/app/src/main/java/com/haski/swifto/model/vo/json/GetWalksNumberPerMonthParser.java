package com.haski.swifto.model.vo.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.haski.swifto.model.vo.WalksForMonth;

public class GetWalksNumberPerMonthParser {
	
	
	interface WALKS_PER_MONTH
	{
		String DAYS_IN_MONTH = "daysInMonth";
		String WALKS_IN_MONTH = "walksInMonth";
		String WALKS = "walks";
	}
	
	public WalksForMonth parseWalksForMonth(String json)
	{
		WalksForMonth walksForMonth = new WalksForMonth();
		
		try {
			JSONObject jWalksPerMonth = new JSONObject(json);
			
			if(jWalksPerMonth.has(WALKS_PER_MONTH.DAYS_IN_MONTH)) {
				int daysInMonth = jWalksPerMonth.getInt(WALKS_PER_MONTH.DAYS_IN_MONTH);
				walksForMonth.setDaysInMonth(daysInMonth);
			}
			
			if(jWalksPerMonth.has(WALKS_PER_MONTH.WALKS_IN_MONTH)) {
				int walksInMonth = jWalksPerMonth.getInt(WALKS_PER_MONTH.WALKS_IN_MONTH);
				walksForMonth.setWalksInMonth(walksInMonth);
			}
			
			if(jWalksPerMonth.has(WALKS_PER_MONTH.WALKS)) {
				JSONArray jWalks = jWalksPerMonth.getJSONArray(WALKS_PER_MONTH.WALKS);
				
				String numbers = "";
				
				if(jWalks.length() > 0) {
					for(int i = 0; i < jWalks.length(); i++) {
						String oneDay = jWalks.getString(i);
						numbers += oneDay + ",";
					}

					if(numbers.length() > 0) {
						numbers = numbers.substring(0, numbers.length()-1);
					}
				}
				
				walksForMonth.setNumbers(numbers);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return walksForMonth;
	}
}
