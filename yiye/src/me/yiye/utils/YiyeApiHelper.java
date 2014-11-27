package me.yiye.utils;

import java.util.List;

import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

public class YiyeApiHelper {
	private final static String TAG = "YiyeApiHelper";
	
	public static void addChannelToChannelSet(Context context, List<Channel> channelList, Cursor cur) {
		Channel c = new Channel();
		MLog.d(TAG, "addChannelToChannelSet### cursor count:" + cur.getCount());
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			c = Channel.buildFromCursor(cur);
			channelList.add(c);
		}
	}
	
	public static void addChannelToChannelSet(Context context, List<Channel> channelList, String result) {
		Channel c = new Channel();
		MLog.d(TAG, "addChannelToChannelSet### json:" + result);
		JSONArray channelArray = null;
		try {
			channelArray = new JSONArray(result);
			for (int i = 0; i < channelArray.length(); i++) {
				JSONObject o = channelArray.getJSONObject(i);
				c = Channel.buildFromJosnObject(o);
				channelList.add(c);
				SQLManager.saveChannel(context, c);
			}
		} catch (JSONException e) {
			MLog.e(TAG, "addChannelToChannelSet### 解析json失败");
			e.printStackTrace();
		}
	}
	
	public static void addBookMarkToChannel(Context context, List<BookMark> bookmarkList, Cursor cur) {
		BookMark b = new BookMark();
		MLog.d(TAG, "addChannelToChannelSet### cursor count:" + cur.getCount());

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			b = BookMark.buildFromCursor(cur);
			bookmarkList.add(b);
		}
	}
	
	public static void addBookMarkToBookMarkList(Context context, List<BookMark> bookmarkList, String result) {
		BookMark b = new BookMark();
		MLog.d(TAG, "addBookMarkToBookMarkList### json:" + result);
		JSONArray bookmarkArray = null;
		try {
			bookmarkArray = new JSONArray(result);
			for (int i = 0; i < bookmarkArray.length(); i++) {
				JSONObject o = bookmarkArray.getJSONObject(i);
				b = BookMark.buildBookMarkFromJsonObject(o);
				bookmarkList.add(b);
				SQLManager.saveBookMark(context, b);
			}
		} catch (JSONException e) {
			MLog.e(TAG, "addChannelToChannelSet### 解析json失败");
			e.printStackTrace();
		}
	}
}
