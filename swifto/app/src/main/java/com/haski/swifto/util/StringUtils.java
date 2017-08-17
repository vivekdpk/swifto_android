package com.haski.swifto.util;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class StringUtils {

	public static String replaceSpaces(String toReplace)
	{
		return toReplace.replace(" ", "%20");
	}
	
	private static String[] NUMBERS = {"Zero", "One", "Two", "Three", "Four"};
	/**<ul>
	 * 	<li>0 - Zero</li>
	 * 	<li>1 - One</li>
	 * 	<li>...</li>
	 * 	<li>4 - Four</li>
	 * </ul>
	 * */
	public static String NUMBER_AS_TEXT(int number)
	{
		return NUMBERS[number];
	}
	
	private static String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	public static String MONTH_NAME(int index)
	{
		return MONTHS[index];
	}
	
	/**"6th August" ==> "08.06"*/
	public static String getFormattedMonthDay(Calendar c)
	{
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		
		return String.format("%02d.%02d", month, day);
	}
	
	public static String connectDoublesWith(double first, double second, String connector)
	{
		return String.format("%f-%f", first, second);
	}
	
	public static String capitalizeFirstLettersOfEachWord(String toCapitalize)
	{
		String[] words = toCapitalize.split(" ");
		
		String toRet = "";
		
		for(int i = 0 ; i < words.length; i++)
		{
			String oneWord = words[i];
			
			toRet += oneWord.substring(0, 1).toUpperCase() + oneWord.substring(1);
			
			if(i != words.length-1)
			{
				toRet += " ";
			}
		}
		
		return toRet;
	}
	
	public static String httpsToHttp(String urlToClear)
	{
		String toRet = urlToClear.replaceFirst("https", "http");
		
		return toRet;
	}
	
	public static String removeDuplicateSpaces(String toTrim)
	{
		String toRet = toTrim.trim().replaceAll("\\s+", " ");
		
		return toRet;
	}
	
	@SuppressLint("NewApi")
	public static String changeUnm(String init)
	{
		if (init==null)
	        return null;

	    final StringBuilder ret = new StringBuilder(init.length());

	    for (final String word : init.split(" ")) {
	        if (!word.isEmpty()) {
	            ret.append(word.substring(0, 1).toUpperCase());
	            //ret.append(word.substring(1));
	            ret.append(word.substring(1).toLowerCase());
	        }
	        if (!(ret.length()==init.length()))
	            ret.append(" ");
	    }

	    return ret.toString();
		
		
		//return "";
	}
	
	public static String generateCommaSeparatedZeros(int numberToGenerate)
	{
		String toRet = "";
		
		for(int i = 0; i < numberToGenerate; i++)
		{
			toRet += "0" + ",";
		}

		toRet = toRet.substring(0, toRet.length()-1);
		
		return toRet;
	}
	
	private static final DecimalFormat df = new DecimalFormat("0.#####");
	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
	public static DecimalFormat GET_DECIMAL_FORMAT()
	{
		//if(df == null)
		//{
		if(symbols.getDecimalSeparator() != '.')
		{
			symbols.setDecimalSeparator('.');
		}
			
			df.setDecimalFormatSymbols(symbols);
		//}
		
		return df;
	}
}
