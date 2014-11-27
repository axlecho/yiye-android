package me.yiye.utils;

import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.ChannelSet;
import me.yiye.contents.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class YiyeApiImp implements YiyeApi{
	
	private final static String TAG = "YiyeApiImp";
	private Context context;
	
	public YiyeApiImp(Context context) {
		this.context = context;
	}
	
	@Override
	public List<Channel> getBookedChannels() {
		String ret = NetworkUtil.get(context, YiyeApi.TESTHOST, YiyeApi.BOOKEDCHANNELS);
		
		if(ret == null) {
			MLog.e(TAG, "getBookedChannels### network return null");
			return null;
		}
		
		MLog.d(TAG, "getBookedChannels###network ret:" + ret);
		List<Channel> 		bookedChannels = new ArrayList<Channel>();
		YiyeApiHelper.addChannelToChannelSet(context, bookedChannels, ret);
		return bookedChannels;
	}

	@Override
	public List<ChannelSet> getChannelSets() {
		// TODO Auto-generated method stub 
		return new ArrayList<ChannelSet>();
	}

	@Override
	public List<Channel> getChannelsByLabel(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BookMark> getBookMarksByChannel(Channel channel) {
		
		String ret = NetworkUtil.get(context, YiyeApi.TESTHOST + YiyeApi.BOOKMARKINCHANNEL + channel.channelId, "");
		if(ret== null) {
			MLog.e(TAG, "getBookMarksByChannel### network return null");
			return null;
		}
		
		List<BookMark> bookmarkList = new ArrayList<BookMark>();
		
		MLog.d(TAG, "getBookMarksByChannel### ret:" + ret);
		try {
			JSONObject o = new JSONObject(ret);
			JSONArray list = o.getJSONArray("list");
			MLog.d(TAG, "getBookMarksByChannel### " + list.toString());
			YiyeApiHelper.addBookMarkToBookMarkList(context, bookmarkList, list.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bookmarkList;
	}

	@Override
	public String login(String email, String keyword) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", keyword));
		return NetworkUtil.post(context, YiyeApi.TESTHOST + YiyeApi.LOGIN, params);
	}

	@Override
	public String logout() {
		return NetworkUtil.get(context,YiyeApi.TESTHOST,YiyeApi.LOGOUT);
	}
	
	@Override
	public String getUserInfo() {
		return NetworkUtil.get(context, YiyeApi.TESTHOST, YiyeApi.USERINFO);
	}

	@Override
	public boolean isOnline(User user) {
		String ret = login(user.email,user.password);
		return (ret == null || ret.endsWith("error")? true : false);
	}

	@Override
	public String getError() {
		return NetworkUtil.getError();
	}
}
