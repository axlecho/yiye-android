package me.yiye.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
}
