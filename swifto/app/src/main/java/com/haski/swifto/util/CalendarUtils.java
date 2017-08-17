package com.haski.swifto.util;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {
	
	public static Calendar TODAY_MIDNIGHT() {
		Calendar toRet = Calendar.getInstance();
		toRet.set(Calendar.HOUR_OF_DAY, 0);
		toRet.set(Calendar.MINUTE, 0);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);
		
		return toRet;
	}

	public static Calendar TODAY_23_59() {
		Calendar toRet = Calendar.getInstance();
		toRet.set(Calendar.HOUR_OF_DAY, 23);
		toRet.set(Calendar.MINUTE, 59);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);
		
		return toRet;
	}
	
	public static Calendar MIDNIGHT_FROM_SECONDS(long seconds) {
		Calendar toRet = Calendar.getInstance();
		toRet.clear();
		toRet.setTimeInMillis(seconds * 1000);
		
		toRet.set(Calendar.HOUR_OF_DAY, 0);
		toRet.set(Calendar.MINUTE, 0);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);
		
		return toRet;
	}
	
	public static Calendar D23_59_FROM_SECONDS(long seconds) {
		Calendar toRet = Calendar.getInstance();
		toRet.clear();
		toRet.setTimeInMillis(seconds * 1000);
		
		toRet.set(Calendar.HOUR_OF_DAY, 23);
		toRet.set(Calendar.MINUTE, 59);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);
		
		return toRet;
	}
	
	public static Calendar DAY_MIDNIGHT(int differenceFromToday) {
		Calendar toRet = TODAY_MIDNIGHT();
		/*
		toRet.set(Calendar.HOUR_OF_DAY, 0);
		toRet.set(Calendar.MINUTE, 0);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);*/
		
		toRet.add(Calendar.DAY_OF_YEAR, differenceFromToday);
		
		return toRet;
	}

	public static Calendar DAY_23_59(int differenceFromToday) {
		Calendar toRet = TODAY_23_59();
		
		toRet.add(Calendar.DAY_OF_YEAR, differenceFromToday);
		
		/*
		toRet.set(Calendar.HOUR_OF_DAY, 23);
		toRet.set(Calendar.MINUTE, 59);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);
		*/
		return toRet;
	}
	
	
	
	//--------------------------------------------------------
	//
	//			to clean
	//
	//------------------
	
	public static int DIFFERENCE_IN_YEARS(int year) {
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		
		return currentYear - year;
	}
	
	public static int DIFFERENCE_FROM_MONDAY() {
		Calendar cal = NOW();
		
		int day = cal.get(Calendar.DAY_OF_WEEK);
		
		if(day == Calendar.SUNDAY) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		Days daysBetween = Days.daysBetween(new DateTime().toDateMidnight(), new DateTime(cal.getTime()).toDateMidnight());
		int days = daysBetween.getDays();
		
		return days;
	}
	
	
	
	public static double SECONDS(long milliseconds) {
		return milliseconds / 1000;
	}
	
	public static double SECONDS(Calendar calendar) {
		return SECONDS(MILLISECONDS_FROM_CALENDAR(calendar));
	}
	
	public static double MINUTES(long milliseconds) {
		return milliseconds / 1000 / 60;
	}
	
	public static double HOURS(long milliseconds) {
		return milliseconds / 1000 / 60 / 60;
	}

	
	public static Calendar NOW() {
		return Calendar.getInstance();
	}
	
	public static double NOW_IN_SECONDS() {
		return SECONDS(NOW());
	}
	
	
	
	public static Calendar CALENDAR_1970() {
		Calendar cal1970 = Calendar.getInstance();
		cal1970.clear();
		//cal1970.set(Calendar.YEAR, 1970);
		cal1970.set(1970, Calendar.JANUARY, 1);
		cal1970.set(Calendar.HOUR_OF_DAY, 0);
		cal1970.set(Calendar.MINUTE, 0);
		cal1970.set(Calendar.SECOND, 0);
		cal1970.set(Calendar.MILLISECOND, 0);
		
		return cal1970;
	}

	//nkp start
	public static Calendar TODAY_4PM_FROM_SECOND(long seconds) {
		Calendar cal4pm = Calendar.getInstance();
		cal4pm.clear();
		cal4pm.setTimeInMillis(seconds * 1000);

		cal4pm.set(Calendar.HOUR_OF_DAY, 16);
		cal4pm.set(Calendar.MINUTE, 0);
		cal4pm.set(Calendar.SECOND, 0);
		cal4pm.set(Calendar.MILLISECOND, 0);

		return cal4pm;
	}

	public static Calendar _6AM_AFTER_DAY_FROM_SECOND(long seconds) {
		Calendar toRet = Calendar.getInstance();
		toRet.clear();
		toRet.setTimeInMillis(seconds * 1000);

		toRet.set(Calendar.DAY_OF_MONTH, toRet.get(Calendar.DAY_OF_MONTH) + 1);
		toRet.set(Calendar.HOUR_OF_DAY, 6);
		toRet.set(Calendar.MINUTE, 0);
		toRet.set(Calendar.SECOND, 0);
		toRet.set(Calendar.MILLISECOND, 0);

		return toRet;
	}

	public static Calendar TODAY_23_59_59(long seconds) {
		Calendar toRet = Calendar.getInstance();
		toRet.clear();
		toRet.setTimeInMillis(seconds * 1000);

		toRet.set(Calendar.HOUR_OF_DAY, 23);
		toRet.set(Calendar.MINUTE, 59);
		toRet.set(Calendar.SECOND, 59);
		toRet.set(Calendar.MILLISECOND, 0);

		return toRet;
	}
	//nkp end
	
	public static double SECONDS_FROM_1970(Calendar calendar) {
		Calendar cal1970 = CALENDAR_1970();
		
		double diffFrom1970 = calendar.getTimeInMillis() - cal1970.getTimeInMillis();
		
		return diffFrom1970 / 1000;
	}
	
	
	
	public static long MILLISECONDS_FROM_CALENDAR(Calendar c) {
		return c.getTimeInMillis();
	}
	
	public static Calendar FROM_DOUBLE_PLUS_1970(double seconds) {
		String s = String.format(Locale.getDefault(), "%f", seconds);
		int dotIndex = s.indexOf(",");
		
		if(dotIndex != -1) {
			s = s.substring(0, dotIndex);
		}
		
		int lSeconds = Integer.parseInt(s);
		
		Calendar c = CALENDAR_1970();
		
		c.add(Calendar.SECOND, lSeconds);
		
		return c;
	}
	
	public static Calendar ROLL_TO_MIDNIGHT(Calendar toRoll) {
		toRoll.set(Calendar.HOUR_OF_DAY, 0);
		toRoll.set(Calendar.MINUTE, 0);
		toRoll.set(Calendar.SECOND, 0);
		toRoll.set(Calendar.MILLISECOND, 0);
		
		return toRoll;
	}
	
	public static Calendar ROLL_TO_23_59(Calendar toRoll) {
		toRoll.set(Calendar.HOUR_OF_DAY, 23);
		toRoll.set(Calendar.MINUTE, 59);
		toRoll.set(Calendar.SECOND, 0);
		toRoll.set(Calendar.MILLISECOND, 0);
		
		return toRoll;
	}
	
	/**<ul>
	 * 
	 * 	<li>Monday    =   0</li>
	 * 	<li>Tuesday   =   1</li>
	 * 	<li>Wednesday =   2</li>
	 * 	<li>Thursday  =   3</li>
	 * 	<li>Friday    =   4</li>
	 * 	<li>Saturday  =   5</li>
	 * 	<li>Sunday    =   6</li>
	 * 
	 * </ul>
	 * */
	public static int DIFFERENCE_IN_DAYS_FROM_START_OF_WEEK() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		
		if(day == Calendar.SUNDAY) {
			return 6;
		} else {
			return day-1;
		}
	}
	
	public static String TO_STRING(Calendar calendar) {
		StringBuilder builder = new StringBuilder();
		builder.append(calendar.get(Calendar.YEAR));
		builder.append("-");
		builder.append(calendar.get(Calendar.MONTH));
		builder.append("-");
		builder.append(calendar.get(Calendar.DAY_OF_MONTH));
		builder.append("---");
		builder.append(calendar.get(Calendar.HOUR_OF_DAY));
		builder.append("-");
		builder.append(calendar.get(Calendar.MINUTE));
		builder.append("-");
		builder.append(calendar.get(Calendar.SECOND));
		builder.append("-");
		builder.append(calendar.get(Calendar.MILLISECOND));
		
		return builder.toString();
	}
}
