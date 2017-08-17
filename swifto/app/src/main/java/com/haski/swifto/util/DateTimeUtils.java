package com.haski.swifto.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Months;

public class DateTimeUtils {

	public static final String PATERN_AM_PM = "hh:mm a";

	public static final String PATTERN_MONTH_NAME = "lll";

	public static final String PATTERN_DAY_NAME = "EEE";

	public static final String PATTERN_MONTH_DAY = "MM.dd";

	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";

	public static final String PATTERN_LOG_YYYY_MM_DD___HH_MM_SS = "yyyy_MM_dd___HH_mm_ss";

	/**
	 * <ul>
	 * <li>1 month old</li>
	 * <li>2 months old</li>
	 * <li>1 year old</li>
	 * <li>1 year and 1 month old</li>
	 * <li>1 year and 2 months old</li>
	 * <li>2 years and 1 month old</li>
	 * <li>2 years and 2 months old</li>
	 * </ul>
	 * */
	public static String getAge(Date d) {
		DateTime date = new DateTime(d);

		Months months = Months.monthsBetween(date, new DateTime());

		int monthsDiff = months.getMonths();

		StringBuilder builder = new StringBuilder();

		// months old
		if (monthsDiff < 12) {
			builder.append(monthsDiff);
			builder.append(monthsDiff == 1 ? " month" : " months");
		}
		// XX year(s) old
		else {
			int year = monthsDiff / 12;
			monthsDiff %= 12;

			builder.append(year);
			builder.append(year == 1 ? " year" : " years");

			if (monthsDiff > 0) {
				builder.append(" and ");
				builder.append(monthsDiff);
				builder.append(monthsDiff == 1 ? " month" : " months");
			}
		}
		builder.append(" old");
		return builder.toString();
	}
}
