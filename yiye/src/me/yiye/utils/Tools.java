package me.yiye.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

public class Tools {

    public static void cleanCurrentUserFromSharedPreferences(Context context) {
        SharedPreferences userSharedPreferences = context.getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSharedPreferences.edit();
        editor.putString("currentuser", null); // 标记已经初始化
        editor.commit();
    }

    public static void cleanCookies(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("yiye", null);
        editor.commit();
    }

    // 关闭所有打开的activity
    private List<Activity> mActivityList = new LinkedList<Activity>();
    private static Tools instance;

    private Tools() {
    }

    public synchronized static Tools getInstance() {
        if (null == instance) {
            instance = new Tools();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void cleanActivityStack() {
        try {
            for (Activity activity : mActivityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
