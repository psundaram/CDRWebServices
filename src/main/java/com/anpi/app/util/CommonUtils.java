package com.anpi.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtils {

	public static String checkPhoneNumber(String stTempNumber) {
		if (stTempNumber.startsWith("+1")) {
			stTempNumber = stTempNumber.substring(2, stTempNumber.length());
		}
		if (stTempNumber.length() == 10) {
			// stTempNumber = stTempNumber.substring(0, 3) + "-" +
			// stTempNumber.substring(3, 6) + "-" + stTempNumber.substring(6,
			// 10);
		}
		return stTempNumber;
	}
	
	public static String getDate(long millSeconds) {
		Date date = new Date(millSeconds);
		DateFormat dateFormat = null;
		if (isToday(date)) {
			dateFormat = new SimpleDateFormat(" ; hh:mm a");
			return "Today " + dateFormat.format(date);
		} else {
			dateFormat = new SimpleDateFormat("MM/dd/yy ; hh:mm a");
			return dateFormat.format(date);
		}
	}
	
	public static boolean isToday(Date date) {
		return false;
	}
	
	public static String getTimeZoneDate(long timeMillSeconds, String stTimeKey) {
		String[] split = null;
		if (stTimeKey != null && !stTimeKey.trim().equals("")) {
			split = stTimeKey.split("---");
		} else {
			split = new String[2];
			split[0] = "GMT";
			split[1] = "GMT";
		}
		Date date = new Date(timeMillSeconds);
		DateFormat dateFormat = null;
		if (isToday(date)) {
			dateFormat = new SimpleDateFormat(" ; hh:mm a");
			dateFormat.setTimeZone(TimeZone.getTimeZone(split[0]));
			return "Today " + dateFormat.format(date) + ";  " + split[1];
		} else {
			dateFormat = new SimpleDateFormat("MM/dd/yy ; hh:mm a");
			dateFormat.setTimeZone(TimeZone.getTimeZone(split[0]));
			return dateFormat.format(date) + ";  " + split[1];
		}
	}
	
	public static void main(String[] args) {
		long timeMillSeconds = 1435755240000l;
		System.out.println(getTimeZoneDate(timeMillSeconds,"AET---GMT"));
	}
	
	public static String getDuration(int milliTime){
			milliTime = milliTime/1000;
		  int hr = milliTime/3600;
		  int rem = milliTime%3600;
		  int mn = rem/60;
		  int sec = rem%60;
		  String hrStr = (hr<10 ? "0" : "")+hr ;
		  String mnStr = (mn<10 ? "0" : "")+mn ;
		  String secStr = (sec<10 ? "0" : "")+sec ; 
		  return hrStr+":"+mnStr+":"+ secStr;
		
	}   
}
