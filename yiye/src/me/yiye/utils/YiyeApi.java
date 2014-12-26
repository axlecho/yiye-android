package me.yiye.utils;

import java.util.List;

import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.ChannelEx;
import me.yiye.contents.User;

public interface YiyeApi {

    public List<Channel> getBookedChannels(); // 获取订阅的频道
    public List<BookMark> getBookMarksByChannelId(String channelId);
    public String login(String email, String keyword);
    public String logout();
    public String getUserInfo();
    public List<ChannelEx> search(String keyword);
    public List<ChannelEx> getChannelByPage(int i);
    public String bookChannel(ChannelEx c);
    public String unBookChannel(ChannelEx c);
    public boolean isOnline(User user);

    public final static String HOST = "http://axlecho.me/";
    public final static String PICCDN = "http://yiye.qiniudn.com/";
    public final static String PICSCALEPARAM = "?imageView2/0/w/400";

    public final static String LOGIN = "api/account/login";
    public final static String LOGOUT = "api/account/logout";
    public final static String GETUSERINFO = "api/user/me";

    public final static String GETBOOKEDCHANNELS = "api/channel/all";
    public final static String GETBOOKMARKINCHANNEL = "api/bookmarks/init";
    public final static String GETBOOKMARKSINCHANNELBYDATE = "api/bookmarks/oneDay";
    public final static String SEARCH = "api/discovery/";
    public final static String GETCHANNELBYPAGE = "api/home/discover"; // 按页获取书签
    public final static String BOOKCHANNEL = "channel/sub/";
    public final static String UNBOOKCHANNEL = "channel/noSub/";

    public String getError();

    public final static String ERRORNOLOGIN = "请先登录或注册";
    public final static String ERROROFFLINE = "请检查你的网络";
    public final static String ERRORURLDECODE = "Url解码错误";

}