package ng.com.util;

import java.util.Calendar;
import java.util.Date;

public class Util_time {
	/**
	 * get the difference, measured in milliseconds, 
	 * between the current time and midnight, January 1, 1970 UTC.
	 * @return
	 */
	public static long get_current_time_stramp(){
		return System.currentTimeMillis();
	}
	/**
	 * get current time's Date
	 * @return
	 */
	public static Date get_current_Date(){
		return new Date();
	}
	/**
	 * get current time's Calendar
	 * @return
	 */
	public static Calendar get_current_Calendar(){
		return Calendar.getInstance();
	}
}
