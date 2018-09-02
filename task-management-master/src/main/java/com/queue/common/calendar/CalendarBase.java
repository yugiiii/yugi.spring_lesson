package com.queue.common.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarBase {
	
	public static long getUTC(Integer year, Integer month, Integer date, Integer hour, Integer minute, Integer sec) {
		Calendar utc = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		utc.set(year, month, date, hour, minute, sec);
		return utc.getTimeInMillis();
	}
	
	public static Long getUTCFromDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar utc = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		utc.setTime(date);
		return utc.getTimeInMillis();
	}
}