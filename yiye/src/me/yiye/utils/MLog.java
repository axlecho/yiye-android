package me.yiye.utils;

import android.util.Log;

public class MLog {
	
	public static void e(String tag,String msg) {
		if(tag == null) return;
		Log.e(tag, msg);
	}
	
	public static void d(String tag,String msg) {
		if(tag == null) return;
		Log.d(tag,msg);
	}
	
	public static void i(String tag,String msg) {
		if(tag == null) return;
		Log.i(tag,msg);
	}
	
	public static void w(String tag,String msg) {
		if(tag == null) return;
		Log.w(tag,msg);
	}
	
	public static void v(String tag,String msg) {
		if(tag == null) return ;
		Log.v(tag,msg);
	}
	
}
