package me.yiye.utils;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NetworkUtil {
	private final static String TAG = "NetworkUtil";

	private final static int HTTP_CONNECT_TIMEOUT = 3 * 1000;
	private final static int HTTP_SOCKET_TIMEOUT = 3 * 1000;

	private static HttpClient httpClient;

	private static String errorString = "";
	
	public static String getError() {
		return errorString;
	}
	
	public static void init() {
		httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, HTTP_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_SOCKET_TIMEOUT);
	}

	public static String post(Context context, String url, List<NameValuePair> params) {
		MLog.d(TAG, "post### " + url);
		try {
			HttpPost httpRequest = new HttpPost(url);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				// 取出回应字串
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				Header header = httpResponse.getFirstHeader("Set-Cookie");
				String cookie = header.getValue();
				MLog.d("TAG", "post### cookie:" + cookie);

				// 写入cookie
				SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString("yiye", cookie);
				editor.commit();
				return strResult;
			} else {
				MLog.e(TAG, "post### return status code:" + httpResponse.getStatusLine().getStatusCode());
				String  retString = EntityUtils.toString(httpResponse.getEntity());
				MLog.e(TAG,"post### extra info:" + retString);
				if(httpResponse.getStatusLine().getStatusCode() == 401) {
					JSONObject jo = new JSONObject(retString);
					String info = jo.getString("message");
					errorString = info;
				}
				return null;
			}
		} catch (ConnectTimeoutException e) {
			errorString = "连接超时";
			MLog.e(TAG, "post### connect time out");
		} catch (SocketTimeoutException e) {
			errorString = "连接超时";
			MLog.e(TAG, "post### socket time out");
		} catch (Exception e) {
			errorString = "网络出错";
			e.printStackTrace();
		}
		return null;
	}

	public static String get(Context context, String host, String extra) {
		String url = host + extra;
		MLog.d(TAG, "get### " + url);
		try {
			HttpGet httpget = new HttpGet(url);
			SharedPreferences share = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
			String cookie = share.getString("yiye", "");
			httpget.addHeader("Cookie", cookie);
			HttpResponse ret = httpClient.execute(httpget);
			if(ret.getStatusLine().getStatusCode() == 200) { 
				return EntityUtils.toString(ret.getEntity(), "utf-8");
			} else {
				MLog.e(TAG, "get### return status code:" + ret.getStatusLine().getStatusCode());
				if(ret.getStatusLine().getStatusCode() == 401) {
					JSONObject jo = new JSONObject(EntityUtils.toString(ret.getEntity(), "utf-8"));
					String info = jo.getString("info");
					errorString = info;
				}
				return null;
			}
			
		} catch (ConnectTimeoutException e) {
			errorString = "连接超时";
			MLog.e(TAG, "post### connect time out");
		} catch (SocketTimeoutException e) {
			errorString = "连接超时";
			MLog.e(TAG, "post### socket time out");
		} catch (Exception e) {
			errorString = "网络出错";
			e.printStackTrace();
		}
		return null;
	}
}
