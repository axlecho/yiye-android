package me.yiye;

import org.json.JSONException;
import org.json.JSONObject;

import me.yiye.contents.User;
import me.yiye.utils.MLog;
import me.yiye.utils.SQLManager;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalFragment extends Fragment{
	private final static String TAG = "PersonalFragment";
	
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
	
	private Button findBtn;
	
	private View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.fragment_personal, null, false);
		init(v);
		return v;
	}
	
	private void init(View v) {
		
		
		loginBtn =  (Button) v.findViewById(R.id.btn_personal_login);
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginManagerActivity.launch(PersonalFragment.this.getActivity());
				
			}
		});
		
		aboutBtn = (Button) v.findViewById(R.id.btn_personal_about);
		aboutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				new AlertDialog.Builder(PersonalFragment.this.getActivity())
					.setTitle("关于一叶")
					.setMessage(PersonalFragment.this.getActivity().getResources().getString(R.string.notdone_decribe))
					.setPositiveButton("确定", null)
					.show();
			}
		});
		
		findBtn = (Button) v.findViewById(R.id.btn_personal_discover);
		findBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchActivity.launch(getActivity());
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
			loginBtn.setText(getActivity().getResources().getString(R.string.logout_describe));
			loginBtn.setTextColor(getActivity().getResources().getColor(R.color.Purple500));
			loginBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new AsyncTask<Void, Void, String>() {
						private YiyeApi api;
						private ProgressDialog logoutingDialog; 
						@Override
						protected void onPreExecute() {
							api = new YiyeApiImp(PersonalFragment.this.getActivity());
							logoutingDialog = new ProgressDialog(PersonalFragment.this.getActivity());
							logoutingDialog.setMessage("注销中...");
							logoutingDialog.show();
						}

						@Override
						protected String doInBackground(Void... v) {
							String ret = api.logout();
							if(ret == null) {
								cancel(false); // 网络异常 跳到onCancelled处理异常
								return null;
							}
							
							MLog.d(TAG, "doInBackground### logout ret:" + ret);
							String result = null;
							
							try {
								JSONObject o = new JSONObject(ret);
								result = o.getString("result");
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
							setUserInfo();
						}

						@Override
						protected void onCancelled() {
							logoutingDialog.dismiss();
							Toast.makeText(PersonalFragment.this.getActivity(), api.getError(), Toast.LENGTH_LONG).show();
							super.onCancelled();
						}
						
						
					}.execute();
				}
			});
		} else {
			userimageView.setImageResource(R.drawable.ic_launcher);
			usernameTextView.setText(this.getActivity().getResources().getString(R.string.username_no_authentication));
			loginBtn.setText(this.getActivity().getResources().getString(R.string.login_describe));
			loginBtn.setTextColor(this.getActivity().getResources().getColor(R.color.Grey700));
			loginBtn.setOnClickListener(new OnClickListener() {				
				
				@Override
				public void onClick(View v) {
					LoginManagerActivity.launch(PersonalFragment.this.getActivity());
					
				}
			});
		}
	}

	private void cleanCurrentUserFromSharedPreferences() {
		SharedPreferences userSharedPreferences = this.getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = userSharedPreferences.edit();
		editor.putString("currentuser", null); // 标记已经初始化
		editor.commit();
	}

	private void cleanCookies() {
		// 写入cookie
		SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("yiye", null);
		editor.commit();
	}
}
