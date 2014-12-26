package me.yiye.utils;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.ChannelEx;
import me.yiye.contents.User;

public class YiyeApiImp implements YiyeApi {

    private final static String TAG = "YiyeApiImp";
    private Context context;
    private String errorString;


    public YiyeApiImp(Context context) {
        this.context = context;
    }

    @Override
    public List<Channel> getBookedChannels() {
        String ret = NetworkUtil.get(context, YiyeApi.HOST, YiyeApi.GETBOOKEDCHANNELS);

        if (ret == null) {
            MLog.e(TAG, "getBookedChannels### network return null");
            return null;
        }

        MLog.d(TAG, "getBookedChannels###network ret:" + ret);
        List<Channel> bookedChannels = new ArrayList<Channel>();
        YiyeApiHelper.addChannelToChannelSet(context, bookedChannels, ret);
        return bookedChannels;
    }


    @Override
    public List<BookMark> getBookMarksByChannelId(String channelId) {
        String ret = NetworkUtil.get(context, YiyeApi.HOST + YiyeApi.GETBOOKMARKSINCHANNELBYDATE, "?channelId=" + channelId);
        if (ret == null) {
            MLog.e(TAG, "getBookMarksByChannel### network return null");
            return null;
        }

        List<BookMark> bookmarkList = new ArrayList<BookMark>();

        MLog.d(TAG, "getBookMarksByChannel### ret:" + ret);
        try {
            ret = java.net.URLDecoder.decode(ret, "UTF-8");
            MLog.d(TAG, "getBookMarksByChannel### " + ret);
            YiyeApiHelper.addBookMarkToBookMarkList(context, bookmarkList, ret);
        } catch (UnsupportedEncodingException e) {
            errorString = YiyeApi.ERRORURLDECODE;
            MLog.e(TAG, "Url解码错误");
            e.printStackTrace();
        }
        return bookmarkList;
    }

    @Override
    public String login(String email, String keyword) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", keyword));
        return NetworkUtil.post(context, YiyeApi.HOST + YiyeApi.LOGIN, params);
    }

    @Override
    public String logout() {
        return NetworkUtil.get(context, YiyeApi.HOST, YiyeApi.LOGOUT);
    }


    @Override
    public String getUserInfo() {
        return NetworkUtil.get(context, YiyeApi.HOST, YiyeApi.GETUSERINFO);
    }

    @Override
    public List<ChannelEx> getChannelByPage(int i) {
        String ret = NetworkUtil.get(context, YiyeApi.HOST, YiyeApi.GETCHANNELBYPAGE + "?number=" + i);
        if (ret == null) {
            MLog.e(TAG, "getChannelByPage### return null");
            return null;
        }

        MLog.d(TAG, "getChannelByPage###network ret:" + ret);

        String dataString = null;
        try {
            JSONObject o = new JSONObject(ret);
            dataString = o.getString("list");
            MLog.d(TAG, "getChannelByPage### " + dataString);
        } catch (JSONException e) {
            errorString = "解析json出错";
            MLog.e(TAG,"getChannelByPage### 解析json出错");
            e.printStackTrace();
        }

        if (dataString == null) {
            return null;
        }

        List<ChannelEx> channels = new ArrayList<ChannelEx>();
        YiyeApiHelper.addChannelExToChannelSet(context, channels, dataString);
        return channels;
    }

    @Override
    public String bookChannel(ChannelEx c) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("channelId", c.id));
        String ret = NetworkUtil.post(context, YiyeApi.HOST + YiyeApi.BOOKCHANNEL,params);

        if (ret == null) {
            MLog.e(TAG, "bookChannel### return null");
            return null;
        }

        return ret;
    }

    @Override
    public String unBookChannel(ChannelEx c) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("channelId", c.id));
        String ret = NetworkUtil.post(context, YiyeApi.HOST + YiyeApi.UNBOOKCHANNEL,params);

        if (ret == null) {
            MLog.e(TAG, "bookChannel### return null");
            return null;
        }
        return ret;
    }

    @Override
    public List<ChannelEx> search(String keyword) {
        String ret = NetworkUtil.get(context, YiyeApi.HOST, YiyeApi.SEARCH + "?keyword=" + keyword);
        if (ret == null) {
            MLog.e(TAG, "search### return null");
            return null;
        }
        MLog.d(TAG, "getBookedChannels###network ret:" + ret);

        String dataString = null;
        try {
            JSONObject o = new JSONObject(ret);
            String resultString = o.getString("message");
            if (!resultString.equals("ok")) {
                errorString = resultString;
            } else {
                dataString = o.getString("data");
                MLog.d(TAG, "search### " + dataString);
            }
        } catch (JSONException e) {
            errorString = "解析json出错";
            e.printStackTrace();
        }

        if (dataString == null) {
            return null;
        }
        List<ChannelEx> foundChannels = new ArrayList<ChannelEx>();
        YiyeApiHelper.addChannelExToChannelSet(context, foundChannels, dataString);
        return foundChannels;
    }

    @Override
    public boolean isOnline(User user) {
        String ret = login(user.email, user.password);
        return (ret == null ? true : false);
    }

    @Override
    public String getError() {
        String ret = errorString;
        errorString = null;
        return ret != null ? ret : NetworkUtil.getError();
    }

}
