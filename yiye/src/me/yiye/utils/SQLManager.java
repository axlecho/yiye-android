package me.yiye.utils;

import me.yiye.YiyeApplication;
import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.User;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLManager {
	
	private final static String TAG = "SQLManager";
	
	public static void init(Context context) {
		SharedPreferences dbSharedPreferences= context.getSharedPreferences("db", Activity.MODE_PRIVATE);
		String isInit = dbSharedPreferences.getString("init", "no");
		
		if(isInit.equals("yes")) {  // 已经初始化过数据库
			MLog.i(TAG, "init###db has been init");
			return;
		}
		
		// 清理工作	
		MLog.d(TAG, "init### clear the old db");
		context.deleteDatabase("yiye.db");
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS bookmark");
		db.execSQL("DROP TABLE IF EXISTS channel");
		
		// 建表
		MLog.d(TAG, "init### create new table");
		db.execSQL("CREATE TABLE user " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT," + 
				"email VARCHAR UNIQUE," +
				"username VARCHAR," + 
				"password VARCHAR," +
				"avatar VARCHAR" + 
				")");
		db.execSQL("CREATE TABLE channel " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT," + 
				"channelId VARCHAR UNIQUE," + 
				"name VARCHAR," +
				"logo VARCHAR," +
				"type VARCHAR," +
				"news INTEGER," +
				"lastTime VARCHAR," +
				"ownerId INTEGER" +
				")");
		db.execSQL("CREATE TABLE bookmark " +
				"(id VARCHAR PRIMARY KEY," + 
				"title VARCHAR UNIQUE," + 
				"description VARCHAR," +
				"url VARCHAR," +
				"image VARCHAR," +
				"postUser VARCHAR," + 
				"likeNum INTEGER," +
				"postTime VARCHAR," +
				"channelId VARCHAR," +
				"comments VARCHAR" + 
				")");
		
		// 标记已经初始化
		SharedPreferences.Editor editor = dbSharedPreferences.edit();
		editor.putString("init", "yes"); 
		editor.commit();
		
		db.close();
		MLog.d(TAG, "init### init ok");
	}
	
	public static void saveuser(Context context,User user) {
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		ContentValues cv = new ContentValues();
		cv.put("email", user.email);
		cv.put("username", user.username);
		cv.put("password", user.password);
		cv.put("avatar", user.avatar);
		
		
		
		Cursor c = db.rawQuery("select id from user where email=?", new String[] { user.email });
		if(c.getCount() == 0) {
			long id = db.insert("user", null, cv);
			if(id == -1) {
				MLog.e(TAG, "saveuser### insert error");
			} else {
				user.id = id;
			}
		} else {
			if (c.moveToFirst()) {
				user.id = c.getLong(c.getColumnIndex("id"));
				String[] args = {String.valueOf(user.id)};
				db.update("user", cv, "id=?", args);
			}
		}
		db.close();
	}
	
	public static void saveChannel(Context context,Channel c) {
		MLog.d(TAG, "saveChannel###channel:" + c.toString());
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		
		ContentValues cv = new ContentValues();
		cv.put("channelId",c.channelId);
		cv.put("lastTime", c.lastTime);
		cv.put("logo", c.logo);
		cv.put("name", c.name);
		cv.put("news", c.news);
		cv.put("type", c.type);
		cv.put("ownerId", YiyeApplication.user.id);
		
		
		// 判断本地是否已有此频道，没有时插入，有就更新
		Cursor cur = db.rawQuery("select channelId from channel where channelId=?", new String[] { c.channelId });
		if(cur.getCount() == 0) {
			long id = db.insert("channel", null, cv);
			if(id == -1) {
				MLog.e(TAG, "saveChannel### insert error");
			}
		} else {
			MLog.d(TAG,"saveChannel### update channel:" + c.channelId);
			if(cur.moveToFirst()) {
				String[] args = {String.valueOf(c.channelId)};
				db.update("channel", cv, "channelId=?", args);
			}
		}
		db.close();
	}

	public static void saveBookMark(Context context, BookMark b) {
		MLog.d(TAG, "saveBookMark###bookmark:" + b.toString());
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		ContentValues cv = new ContentValues();
		cv.put("channelId",b.channelId);
		cv.put("comments", b.comments);
		cv.put("description", b.description);
		cv.put("image", b.image);
		cv.put("postTime", b.postTime);
		cv.put("postUser", b.postUser);
		cv.put("title",b.title);
		cv.put("url", b.url);
		cv.put("likeNum", b.likeNum);
		cv.put("postUser", b.postUser);
		cv.put("id", b.id);
		
		// 判断本地是否已有此频道，没有时插入，有就更新
		Cursor cur = db.rawQuery("select id from bookmark where id=?", new String[] { "" + b.id });
		if(cur.getCount() == 0) {
			long ret = db.insert("bookmark", null, cv);
			if(ret == -1) {
				MLog.e(TAG, "saveBookMark### insert error");
			}
		} else {
			MLog.d(TAG,"saveBookmarkl### update bookmark:" + b.id);
			if(cur.moveToFirst()) {
				String[] args = {String.valueOf(b.channelId)};
				db.update("bookmark", cv, "id=?", args);
			}
		}
		

		db.close();
	}

	public static User loaduser(Context context,String currentUserName) {
		User user = null;
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("select * from user where email=?", new String[] { currentUserName });
		if (c.moveToFirst()) {
			user = new User();
			user.avatar = c.getString(c.getColumnIndex("avatar"));
			user.email = c.getString(c.getColumnIndex("email"));
			user.id = c.getLong(c.getColumnIndex("id"));
			user.password = c.getString(c.getColumnIndex("password"));
			user.username = c.getString(c.getColumnIndex("username"));
			MLog.d(TAG, "loaduser### user:" + user.toString());
		}
		c.close();
		db.close();
		
		return user;
	}

}
