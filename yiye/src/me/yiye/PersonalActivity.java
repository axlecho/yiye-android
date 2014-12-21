package me.yiye;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import me.yiye.utils.MLog;
import me.yiye.utils.Tools;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

public class PersonalActivity extends BaseActivity {
    private final static String TAG = "PersonalActivity";

    private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_loading)
            .showImageForEmptyUri(R.drawable.img_empty)
            .showImageOnFail(R.drawable.img_failed)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();
    private Button loginBtn;
    private Button aboutBtn;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getInstance().addActivity(this);
        initActionbar(getResources().getString(R.string.personal_describe));
        v = this.getLayoutInflater().inflate(R.layout.fragment_personal, null, false);
        init(v);
        setContentView(v);
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, PersonalActivity.class);
        context.startActivity(i);
    }

    private void init(View v) {
        loginBtn = (Button) v.findViewById(R.id.btn_personal_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginManagerActivity.launch(PersonalActivity.this);

            }
        });

        aboutBtn = (Button) v.findViewById(R.id.btn_personal_about);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.launch(PersonalActivity.this);
            }
        });

        MLog.d(TAG, "init### setting user info");
        setUserInfo();
    }

    private void setUserInfo() {

        // 设置头像
        ImageView userimageView = (ImageView) v.findViewById(R.id.imageview_personal_userimg);
        TextView usernameTextView = (TextView) v.findViewById(R.id.textview_personal_username);
        if (YiyeApplication.user != null) { // 若已经登陆，设置头像及姓名
            ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + YiyeApplication.user.avatar, userimageView, imageoptions);
            usernameTextView.setText(YiyeApplication.user.username);
            loginBtn.setText(this.getResources().getString(R.string.logout_describe));
            loginBtn.setTextColor(this.getResources().getColor(R.color.Purple500));
            loginBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, String>() {
                        private YiyeApi api;
                        private ProgressDialog logoutingDialog;

                        @Override
                        protected void onPreExecute() {
                            api = new YiyeApiImp(PersonalActivity.this);
                            logoutingDialog = new ProgressDialog(PersonalActivity.this);
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
                            return ret;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            logoutingDialog.dismiss();
                            Tools.cleanCurrentUserFromSharedPreferences(PersonalActivity.this);
                            Tools.cleanCookies(PersonalActivity.this);
                            YiyeApplication.user = null;
                            setUserInfo();
                            SplashScreen.launch(PersonalActivity.this);
                        }

                        @Override
                        protected void onCancelled() {
                            logoutingDialog.dismiss();
                            Toast.makeText(PersonalActivity.this, api.getError(), Toast.LENGTH_LONG).show();
                            super.onCancelled();
                        }
                    }.execute();
                }
            });
        } else {
            userimageView.setImageResource(R.drawable.ic_launcher);
            usernameTextView.setText(this.getResources().getString(R.string.username_no_authentication));
            loginBtn.setText(this.getResources().getString(R.string.login_describe));
            loginBtn.setTextColor(this.getResources().getColor(R.color.Grey700));
            loginBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LoginManagerActivity.launch(PersonalActivity.this);

                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
