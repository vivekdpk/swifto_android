package com.haski.swifto.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.haski.swifto.model.EnumAsyncTasksErrors;
import com.haski.swifto.model.vo.BaseJSONObject;

public class TaskErrorChecker {

	/**
	 * </ul>
	 * */
	public static String getErrorInString(String toCheck)
	{
		if(toCheck == null)
		{
			return "isNull";
		}
		
		if(EnumAsyncTasksErrors.supportsException(toCheck))
		{

			if(toCheck.equals(EnumAsyncTasksErrors.IO_EXCEPTION) || toCheck.equals(EnumAsyncTasksErrors.HTTP_CONNECT_TIMEOUT) )
				return "No response from server due to slow internet connection";

			return toCheck;
		}
		try
		{
			BaseJSONObject jObj = new Gson().fromJson(toCheck, BaseJSONObject.class);

			if(jObj.success == false)
			{
				return jObj.errDesc;
			}
		}
		catch(JsonSyntaxException e)
		{
			return "JSONSyntaxError in " + toCheck;
		}
		
		return null;
	}
}
