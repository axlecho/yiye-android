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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NetworkUtil {
    private final static String TAG = "NetworkUtil";

    private final static int HTTP_CONNECT_TIMEOUT = 10 * 1000;
    private final static int HTTP_SOCKET_TIMEOUT = 10 * 1000;

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
        MLog.i(TAG, "post### " + url);

        try {
            HttpPost httpRequest = new HttpPost(url);
            // 伪装成chrome
            httpRequest.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
            SharedPreferences share = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
            String headerCookie = share.getString("yiye", "");
            httpRequest.addHeader("Cookie", headerCookie);
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                MLog.d(TAG, "post###" + strResult);
                JSONObject retJson = new JSONObject(strResult);
                int code = retJson.getInt("code");
                if (code != 0) {
                    errorString = retJson.getString("msg");
                    return null;
                }

                String data = retJson.getString("data");
                Header cookieHeader = httpResponse.getFirstHeader("set-cookie");
                if(cookieHeader != null) {
                    String cookie = cookieHeader.getValue();
                    MLog.d("TAG", "post### cookie:" + cookie);

                    // 写入cookie
                    SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("yiye", cookie);
                    editor.commit();
                }
                return data;
            } else {
                MLog.e(TAG, "post### return status code:" + httpResponse.getStatusLine().getStatusCode());
                String retString = EntityUtils.toString(httpResponse.getEntity());
                MLog.e(TAG, "post### extra info:" + retString);
                errorString = "错误：" + httpResponse.getStatusLine().getStatusCode();
                return null;
            }
        } catch (ConnectTimeoutException e) {
            errorString = "连接超时";
            MLog.e(TAG, "post### connect time out");
        } catch (SocketTimeoutException e) {
            errorString = "连接超时";
            MLog.e(TAG, "post### socket time out");
        } catch (JSONException e) {
            errorString = "解析Json出错";
            MLog.e(TAG,"post###parse json error");
        } catch (Exception e) {
            errorString = "未知的错误";
            e.printStackTrace();
        }
        return null;
    }

    public static String get(Context context, String host, String extra) {
        String url = host + extra;
        MLog.i(TAG, "get### " + url);
        try {
            HttpGet httpget = new HttpGet(url);
            // 伪装成chrome
            httpget.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36"); // 伪装成chrome
            SharedPreferences share = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
            String cookie = share.getString("yiye", "");
            httpget.addHeader("Cookie", cookie);
            HttpResponse ret = httpClient.execute(httpget);
            if (ret.getStatusLine().getStatusCode() == 200) {
                String resultString = EntityUtils.toString(ret.getEntity(), "utf-8");
                MLog.d(TAG, "get###" + resultString);
                JSONObject retJson = new JSONObject(resultString);
                int code = retJson.getInt("code");
                if (code != 0) {
                    errorString = retJson.getString("msg");
                    return null;
                }
                return retJson.getString("data");

            } else {
                MLog.e(TAG, "get### return status code:" + ret.getStatusLine().getStatusCode());
                errorString = "错误:" + ret.getStatusLine().getStatusCode();
                return null;
            }

        } catch (ConnectTimeoutException e) {
            errorString = "连接超时";
            MLog.e(TAG, "post### connect time out");
        } catch (SocketTimeoutException e) {
            errorString = "连接超时";
            MLog.e(TAG, "post### socket time out");
        } catch (JSONException e) {
            errorString = "解析Json出错";
            MLog.e(TAG,"post###parse json error");
        } catch (Exception e) {
            errorString = "未知的错误";
            e.printStackTrace();
        }
        return null;
    }
}
