package me.yiye.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	// private final static String TAG = "DateUtil";
	private final static String TAG = null;

	public static String timeStampToString(long time) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		
		Date target = new Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(target);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		MLog.d(TAG, "timeStampToString### getDate 0:" + getDate(0).getTime());
		MLog.d(TAG, "timeStampToString### getDate -1:" + getDate(-1).getTime());
		MLog.d(TAG, "timeStampToString### getDate -2:" + getDate(-2).getTime());
		MLog.d(TAG, "timeStampToString### target:" + calendar.getTime());
		
		
		if (calendar.compareTo(getDate(0)) == 0) {
			return "今天";
		} else if (calendar.compareTo(getDate(-1)) == 0) {
			return "昨天";
		} else if (calendar.compareTo(getDate(-2)) == 0) {
			return "前天";
		} else {
			MLog.d(TAG,"timeStampToString### " + "calendar.compareTo(getDate(0)):" + calendar.compareTo(getDate(0)));
			MLog.d(TAG,"timeStampToString### " + "calendar.compareTo(getDate(-1)):" + calendar.compareTo(getDate(-1)));
			MLog.d(TAG,"timeStampToString### " + "calendar.compareTo(getDate(-2)):" + calendar.compareTo(getDate(-2)));
			return format.format(target);
		}
	}

	private static Calendar getDate(int dayoffset) {
		Date date = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, dayoffset);

		return calendar;
	}

	public static long dateStringToTimeStamp(String date) {
		Date time;
		long timeStamp = -1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			time = format.parse(date);
			timeStamp = time.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return timeStamp;
		
	}
}
