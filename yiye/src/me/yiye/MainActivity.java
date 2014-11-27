package me.yiye;

import me.yiye.customwidget.SwitchBar;
import me.yiye.utils.MLog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class MainActivity extends FragmentActivity {
	private final static String TAG = "MainActivity";
	
	private JazzyViewPager mViewPager;
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	private SwitchBar mSwitchBar;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initActionbar("一叶");
		
		mViewPager = (JazzyViewPager) findViewById(R.id.viewpager_main);
//		mViewPager.setFadeEnabled(true);
		mViewPager.setTransitionEffect(TransitionEffect.Standard);
		
		mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				mSwitchBar.setSelect(index);
			}
		});

		mSwitchBar = (SwitchBar) findViewById(R.id.switchbar_main);

		mSwitchBar.setOnClickLisener(0, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(0);
			}
		});
		
		mSwitchBar.setOnClickLisener(1, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(1);
			}
		});
	}


	// 由于Activity为Singletask启动，在登陆结束后跳转不会执行OnCreate，
	//所以onStart里刷新数据
	@Override
	protected void onStart() {
		MLog.d(TAG,"onCreate### new a pager adapter");
		mViewPager.setCurrentItem(0,false);
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		super.onStart();
	}


	public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new PacketFragment();
			default:
				MLog.d(TAG, "getItem### new a personalfragment");
				return new PersonalFragment();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return position + "";
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
		    Object obj = super.instantiateItem(container, position);
		    mViewPager.setObjectForPosition(obj, position);
		    return obj;
		}
	}

	public static void launch(Context context) {
		Intent i = new Intent();
		i.setClass(context, MainActivity.class);
		context.startActivity(i);
	}

	// 自定义的Actionbar
	protected void initActionbar(String title) {
		View barview = View.inflate(this,R.layout.view_main_actionbar,null);
		getActionBar().setCustomView(barview);
		getActionBar().setDisplayShowCustomEnabled(true);
		
		// 标题
		TextView titleTextView = (TextView) barview.findViewById(R.id.textview_actionbar_title);
		titleTextView.setText(title);
	}
}
