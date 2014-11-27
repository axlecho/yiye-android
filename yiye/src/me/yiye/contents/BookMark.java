package me.yiye.contents;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import me.yiye.utils.MLog;


public class BookMark {
	private final static String TAG = "BookMark";
	public String title;
	public String description;
	public String url;
	public String image;
	public String postUser;
	public int likeNum;
	public String postTime;
	public String channelId;
	public String comments;

	public String id;
	@Override
	public String toString() {
		return "[id:" + id + " channelId:" + channelId + " title:" + title + " summary:" + description + " url:" + url + " image:" + image
				+ " postTime:" + postTime + " postUser:" + postUser + " likeNum:" + likeNum + "]";
	}
	
	public static BookMark buildBookMarkFromJsonObject(JSONObject o) throws JSONException {
		BookMark b = new BookMark();
		b.id = o.getString("_id");
		b.channelId = o.getString("channelId");
		b.title = o.getString("title");
		b.description = o.getString("description");
		b.url = o.getString("url");
		b.image = o.getString("image");
		b.likeNum = o.getInt("likeNum");
		b.postTime = o.getString("postTime");
		
		JSONObject user = o.getJSONObject("postUser");
		b.postUser = user.getString("username");
		MLog.d(TAG, "buildBookMarkFromJsonObject### " + b.toString());
		return b;
	}

	public static BookMark buildFromCursor(Cursor cur) {
		BookMark b = new BookMark();
		b.id = cur.getString(cur.getColumnIndex("id"));
		b.channelId = cur.getString(cur.getColumnIndex("channelId"));
		b.title = cur.getString(cur.getColumnIndex("title"));
		b.description = cur.getString(cur.getColumnIndex("description"));
		b.url = cur.getString(cur.getColumnIndex("url"));
		b.image = cur.getString(cur.getColumnIndex("image"));
		b.likeNum = cur.getInt(cur.getColumnIndex("likeNum"));
		b.postTime = cur.getString(cur.getColumnIndex("postTime"));
		b.postUser = cur.getString(cur.getColumnIndex("postUser"));
		MLog.d(TAG, "buildFromCursor### " + b.toString());
		return b;
	}
}
