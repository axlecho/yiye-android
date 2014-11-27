package me.yiye.utils;

import java.util.ArrayList;
import java.util.List;

import me.yiye.YiyeApplication;
import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.ChannelSet;
import me.yiye.contents.User;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class YiyeApiOfflineImp implements YiyeApi{

	private final static String TAG = "YiyeApiOfflineImp";
	private Context context;
	
	private String errorString = null;
	public YiyeApiOfflineImp(Context context) {
		this.context = context;
	}
	
	@Override
	public List<Channel> getBookedChannels() {
		List<Channel> channelList = new ArrayList<Channel>();
		if(YiyeApplication.user == null) {
			MLog.e(TAG,"getBookedChannels### user is null");
			errorString = YiyeApi.ERRORNOLOGIN;
			return null;
		} else {
			MLog.i(TAG, "getBookedChannels### user:" + YiyeApplication.user.toString());
		}
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("select * from channel where ownerId=?",new String[]{"" + YiyeApplication.user.id});
		YiyeApiHelper.addChannelToChannelSet(context, channelList, c);
		c.close();
		return channelList;
	}

	@Override
	public List<ChannelSet> getChannelSets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Channel> getChannelsByLabel(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BookMark> getBookMarksByChannel(Channel channel) {
		List<BookMark> bookmarkList = new ArrayList<BookMark>();
		SQLiteDatabase db = context.openOrCreateDatabase("yiye.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("select * from bookmark where channelId=?",new String[]{channel.channelId});
		YiyeApiHelper.addBookMarkToChannel(context, bookmarkList, c);
		c.close();
		return bookmarkList;
	}

	@Override
	public String login(String email, String keyword) {
		errorString = YiyeApi.ERRORNOLOGIN;
		return null;
	}

	public String logout() {
		errorString = YiyeApi.ERRORNOLOGIN;
		return null;
	}
	@Override
	public String getUserInfo() {
		errorString = YiyeApi.ERRORNOLOGIN;
		return null;
	}

	@Override
	public boolean isOnline(User user) {
		errorString = YiyeApi.ERRORNOLOGIN;
		return false;
	}

	@Override
	public String getError() {
		return errorString;
	}
}
