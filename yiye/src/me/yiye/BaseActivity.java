package me.yiye;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockActivity;

class BaseActivity  extends SherlockActivity {
	
	// 自定义的Actionbar
	protected void initActionbar(String title) {
		View barview = View.inflate(this,R.layout.view_actionbar,null);
		getSupportActionBar().setCustomView(barview,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		// 标题
		TextView titleTextView = (TextView) barview.findViewById(R.id.textview_actionbar_title);
		titleTextView.setText(title);
	   
	   // 右边菜单
		ImageView menuBtn = (ImageView)barview.findViewById(R.id.imageview_actionbar_btn);
		menuBtn.setVisibility(View.GONE);
		
		// 左边导航按钮
		ImageView naviconImageView = (ImageView)barview.findViewById(R.id.imageview_actionbar_navicon);
		naviconImageView.setImageResource(R.drawable.ic_action_back);
		naviconImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
