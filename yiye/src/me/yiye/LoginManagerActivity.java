package me.yiye;

import me.yiye.contents.User;
import me.yiye.utils.MLog;
import me.yiye.utils.SQLManager;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginManagerActivity extends BaseActivity {
	private final static String TAG = "LoginManagerActivity";
	// 判断email与password是否有输入，若都有启用登陆按钮，否则禁用登陆按钮
	private boolean emailflag = false;
	private boolean passwordflag = false;

	private User user = new User();

	private Button loginBtn;
	private EditText emailEditText;
	private EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		initActionbar("登陆");

		loginBtn = (Button) this.findViewById(R.id.btn_login);
		emailEditText = (EditText) this.findViewById(R.id.edittext_login_email);
		passwordEditText = (EditText) this.findViewById(R.id.edittext_login_password);

		passwordEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				Editable editable = passwordEditText.getText();
				passwordflag = editable.length() == 0 ? false : true;
				if (emailflag && passwordflag) {
					loginBtn.setEnabled(true);
				} else {
					loginBtn.setEnabled(false);
				}
			}

		});
		emailEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				Editable editable = emailEditText.getText();
				emailflag = editable.length() == 0 ? false : true;
				if (emailflag && passwordflag) {
					loginBtn.setEnabled(true);
				} else {
					loginBtn.setEnabled(false);
				}
			}
		});
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, String>() {
					private YiyeApi api;
					private ProgressDialog loginingDialog; 
					@Override
					protected void onPreExecute() {
						api = new YiyeApiImp(LoginManagerActivity.this);
						user.email = emailEditText.getText().toString();
						user.password = passwordEditText.getText().toString();
						loginingDialog = new ProgressDialog(LoginManagerActivity.this);
						loginingDialog.setMessage("登陆中...");
						loginingDialog.show();
					}

					@Override
					protected String doInBackground(Void... v) {
						MLog.d(TAG, "doInBackground### " + user.toString());
						String ret = api.login(user.email, user.password);
						
						if(ret == null) {
							cancel(false); // 网络异常 跳到onCancelled处理异常
							return ret;
						}
						
						MLog.d(TAG, "doInBackground### login ret:" + ret);
						ret = api.getUserInfo();
						MLog.d(TAG, "doInBackground### getuserinfo ret:" + ret);
						
						try {
							JSONObject o = new JSONObject(ret);
							user.avatar = o.getString("avatar");
							user.username = o.getString("username");
						} catch (JSONException e) {
							MLog.e(TAG, "get user info failed");
							e.printStackTrace();
						}

						return ret;
					}

					@Override
					protected void onPostExecute(String result) {
						// Note!! 此处的验证结果放到了NetworkUtil判断（因为返回码不一样），此处仅成功登录的执行流程
						// 错误处理均放到onCancelled里
						loginingDialog.dismiss();
						SQLManager.saveuser(LoginManagerActivity.this, user);
						setCurrentUserToSharedPreferences(user);
						YiyeApplication.user = user;
						MLog.d(TAG, "onPostExecute### saveuer:" + user.toString());
						// 关闭软键盘
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
						if (imm.isActive()) { 
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
						}				
						
						MainActivity.launch(LoginManagerActivity.this);
						finish();
					}

					@Override
					protected void onCancelled() {
						loginingDialog.dismiss();
						Toast.makeText(LoginManagerActivity.this, api.getError(), Toast.LENGTH_LONG).show();
						super.onCancelled();
					}
					
					
				}.execute();
			}
		});
	}

	private void setCurrentUserToSharedPreferences(User user) {
		SharedPreferences userSharedPreferences = this.getSharedPreferences("user", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = userSharedPreferences.edit();
		editor.putString("currentuser", user.email); // 标记已经初始化
		editor.commit();
	}

	public static void launch(Context context) {
		Intent i = new Intent();
		i.setClass(context, LoginManagerActivity.class);
		context.startActivity(i);
	}
}
