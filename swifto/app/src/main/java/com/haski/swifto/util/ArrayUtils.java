package com.haski.swifto.util;

import java.util.ArrayList;

public class ArrayUtils {

	public static ArrayList<String> splitString(String toSplit, String delimeter) {
		String[] splitted = toSplit.split(delimeter);
		ArrayList<String> toRet = new ArrayList<String>();
		
		for(int i = 0; i < splitted.length; i++) {
			toRet.add(splitted[i]);
		}
				
		return toRet;
	}
	
	public static String[] splitStringToArray(String toSplit, String delimeter) {
		return toSplit.split(delimeter);
	}
}
