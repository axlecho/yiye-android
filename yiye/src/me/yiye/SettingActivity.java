package me.yiye;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    private Button aboutBtn;
    private Button logoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initActionbar(getResources().getString(R.string.setting_describe));
        setContentView(R.layout.activity_setting);
        aboutBtn = (Button) findViewById(R.id.btn_setting_about);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.launch(SettingActivity.this);
            }
        });

        logoutBtn = (Button) findViewById(R.id.btn_setting_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    private YiyeApi api;
                    private ProgressDialog logoutingDialog;

                    @Override
                    protected void onPreExecute() {
                        api = new YiyeApiImp(SettingActivity.this);
                        logoutingDialog = new ProgressDialog(SettingActivity.this);
                        logoutingDialog.setMessage("注销中...");
                        logoutingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... v) {
                        String ret = api.logout();
                        if (ret == null) {
                            cancel(false); // 网络异常 跳到onCancelled处理异常
                            return null;
                        }

                        MLog.d(TAG, "doInBackground### logout ret:" + ret);
                        String result = null;

                        try {
                            JSONObject o = new JSONObject(ret);
                            result = o.getString("message");
                        } catch (JSONException e) {
                            MLog.e(TAG, "doInBackground### get result info failed");
                            e.printStackTrace();
                        }

                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        logoutingDialog.dismiss();
                        cleanCurrentUserFromSharedPreferences();
                        cleanCookies();
                        YiyeApplication.user = null;

                        MainActivity.launch(SettingActivity.this);
                        finish();
                    }

                    @Override
                    protected void onCancelled() {
                        logoutingDialog.dismiss();
                        Toast.makeText(SettingActivity.this, api.getError(), Toast.LENGTH_LONG).show();
                        super.onCancelled();
                    }

                }.execute();
            }
        });

        if (YiyeApplication.user == null) {
            logoutBtn.setEnabled(false);
        } else {
            logoutBtn.setEnabled(true);
        }


        super.onCreate(savedInstanceState);
    }

    private void cleanCurrentUserFromSharedPreferences() {
        SharedPreferences userSharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSharedPreferences.edit();
        editor.putString("currentuser", null); // 标记已经初始化
        editor.commit();
    }

    private void cleanCookies() {
        SharedPreferences sharedPreferences = getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("yiye", null);
        editor.commit();
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, SettingActivity.class);
        context.startActivity(i);
    }
}
