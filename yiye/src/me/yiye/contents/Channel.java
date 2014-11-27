package me.yiye.contents;

import me.yiye.utils.MLog;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class Channel {
	private final static String TAG = "Channel";
	public String channelId;
	public String name;
	public String logo;
	public String type;
	public int news;
	public String lastTime;
	
	@Override
	public String toString() {
		return "[channelId:" + channelId + " name:" + name + " logo:" + logo + " type:" + type + " news:" + news + "lasttime:" + lastTime + "]";
	}
	
	public static Channel buildFromJosnObject(JSONObject o) throws JSONException {
		Channel c = new Channel();
		c.channelId = o.getString("channelId");
		c.name = o.getString("name");
		c.logo = o.getString("logo");
		c.type = o.getString("type");
		c.lastTime = o.getString("lastTime");
		c.news = o.getInt("news");
		MLog.d(TAG, "buildFromJosnObject### " + c.toString());
		return c;
	}
	
	public static Channel buildFromCursor(Cursor cur) {
		Channel c = new Channel();
		c.channelId = cur.getString(cur.getColumnIndex("channelId"));
		c.name = cur.getString(cur.getColumnIndex("name"));
		c.logo = cur.getString(cur.getColumnIndex("logo"));
		c.type = cur.getString(cur.getColumnIndex("type"));
		c.lastTime = cur.getString(cur.getColumnIndex("lastTime"));
		c.news = cur.getInt(cur.getColumnIndex("news"));
		MLog.d(TAG,"buildFromCursor### " + c.toString());
		return c;
	}
}
