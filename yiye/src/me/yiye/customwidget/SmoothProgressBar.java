package me.yiye.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class SmoothProgressBar extends ProgressBar {

	public SmoothProgressBar(Context context) {
		super(context);
	}

	public SmoothProgressBar(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public SmoothProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public synchronized void setProgress(int progress) {
		int currentPress = this.getProgress();
		if (progress <= currentPress)
			return;
		for (int i = currentPress; i <= progress; ++i) {
			super.setProgress(i);
		}
	}
}