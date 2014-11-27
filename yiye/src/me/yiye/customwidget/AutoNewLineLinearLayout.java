package me.yiye.customwidget;

import java.util.Hashtable;

import me.yiye.utils.MLog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AutoNewLineLinearLayout extends RelativeLayout {
	// private final static String TAG = "AutoNewLineLinearLayout";
	private final static String TAG = null;
	Hashtable<View, Position> map = new Hashtable<View, AutoNewLineLinearLayout.Position>();
	
	public AutoNewLineLinearLayout(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);

	}

	public AutoNewLineLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public AutoNewLineLinearLayout(Context context) {
		super(context);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = this.getChildCount();
		MLog.d(TAG, "onLayout### child count:" + count);
		
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			Position pos = map.get(child);
			if (pos != null) {
				MLog.d(TAG, "onLayout### child pos:" + pos);
				child.layout(pos.left, pos.top, pos.right, pos.bottom);
			} else {
				MLog.e(TAG, "onLayout### child has no position");
			}
		}
	}

	public int getPosition(int IndexInRow, int childIndex) {
		if (IndexInRow > 0) {
			return getPosition(IndexInRow - 1, childIndex - 1)
					+ getChildAt(childIndex - 1).getMeasuredWidth() + 10;
		}
		return 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		MLog.d(TAG, "onMeasure### measure width:" + width);
		MLog.d(TAG, "onMeasure### measure height:" + height);
		
		int mLeft = 0;
		int mRight = 0; 
		int mTop= 0;
		int mBottom = 0;
		
		int j = 0;
		int count = getChildCount();
		MLog.d(TAG, "onMeasure### child view count:" + count);
		
		for (int i = 0; i < count; i++) {
			Position position = new Position();
			View view = getChildAt(i);
			mLeft = getPosition(i - j, i);
			mRight = mLeft + view.getMeasuredWidth();
			if (mRight >= width) {
				j = i;
				mLeft = getPosition(i - j, i);
				mRight = mLeft + view.getMeasuredWidth();
				mTop += getChildAt(i).getMeasuredHeight() + 5;
			}
			mBottom = mTop + view.getMeasuredHeight();
			position.left = mLeft;
			position.top = mTop;
			position.right = mRight;
			position.bottom = mBottom;
			MLog.d(TAG,"onMeasure### child pos:" + position);
			map.put(view, position);
		}
		
		this.setLayoutParams(new LinearLayout.LayoutParams(width, mBottom));
		
	}

	private class Position {
		int left, top, right, bottom;
		
		@Override
		public String toString() {
			return "left " + left + " top " + top + " right " + right + " bottom " + bottom;
		}
	}

}