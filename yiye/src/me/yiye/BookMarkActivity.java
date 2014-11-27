package me.yiye;

import me.yiye.contents.BookMark;
import me.yiye.customwidget.ConstomWebView;
import me.yiye.customwidget.SmoothProgressBar;
import me.yiye.utils.MLog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BookMarkActivity extends BaseActivity {
	private final static String TAG = "BookMarkActivity";
	
	private static BookMark bookmark;
	
	private ConstomWebView mWebView;
	private SmoothProgressBar loaddingProgressBar;
	private OnTouchListener webviewTouchListener;
	
	
	private View commentaryView;
	private View toolBarView;
	private ImageButton commentaryBtn;
	private ImageButton favourBtn;
	private ImageButton shareBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_bookmark);
		initActionbar(bookmark.title);
		initWebView();
		initBottomActionBar();
		mWebView.loadUrl(bookmark.url);
	}
	
	private void initWebView() {
		
		// 设置webview
		mWebView = (ConstomWebView) this.findViewById(R.id.webview_bookmark);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSupportZoom(true);	// 允许缩放
		webSettings.setBuiltInZoomControls(true);	// 使用内建的缩放手势
		webSettings.setLoadWithOverviewMode(true);	// 缩小以适应屏幕
		webSettings.setJavaScriptEnabled(true);	// 开启javascript（！！有问题 知乎的登陆按钮不起作用)
		webSettings.setUseWideViewPort(true);	// 双击变大变小
		webSettings.setDisplayZoomControls(false);	// 去除缩放时右下的缩放按钮
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 去除滚动条
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		
		// 防止启动外部浏览器
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		// 设置加载进度条
		loaddingProgressBar = (SmoothProgressBar) this.findViewById(R.id.progressbar_web);
		loaddingProgressBar.setMax(100);
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					loaddingProgressBar.setVisibility(View.GONE);
				} else {
					if (loaddingProgressBar.getVisibility() == View.GONE)
						loaddingProgressBar.setVisibility(View.VISIBLE);
					loaddingProgressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});	
	}

	private void initBottomActionBar() {
		commentaryView = (View) this.findViewById(R.id.view_bookmark_commentary);
		toolBarView = (View) this.findViewById(R.id.view_bookmark_toolbar);
		
		// 底部栏的三个按钮
		commentaryBtn = (ImageButton) toolBarView.findViewById(R.id.imagebutton_bookmark_commentary);
		favourBtn = (ImageButton) toolBarView.findViewById(R.id.imagebutton_bookmark_favour);
		shareBtn = (ImageButton) toolBarView.findViewById(R.id.imagebutton_bookmark_share);
		
		commentaryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commentaryView.setVisibility(View.VISIBLE);
				toolBarView.setVisibility(View.GONE);
				mWebView.setOnTouchListener(null);
			}
		});
		favourBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(BookMarkActivity.this, "收藏", Toast.LENGTH_LONG).show();
			}
		});
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(BookMarkActivity.this, "赞", Toast.LENGTH_LONG).show();
			}
		});
		
		// 评论面板上的关闭按钮
		TextView closeBtn = (TextView) commentaryView.findViewById(R.id.btn_bookmark_commentary_close);
		
		closeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				commentaryView.setVisibility(View.GONE);
				toolBarView.setVisibility(View.VISIBLE);
				mWebView.setOnTouchListener(webviewTouchListener);
			}
		});
		
		// 屏蔽评论面板滑动穿透到webview上
		commentaryView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
	}
	
	// 支持网页回退
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static void launch(Context context, BookMark bookmark) {
		if (bookmark == null) {
			MLog.e(TAG, "launch### bookmark is null");
			return;
		}
		BookMarkActivity.bookmark = bookmark;
		launch(context);
	}

	private static void launch(Context context) {
		Intent i = new Intent();
		i.setClass(context, BookMarkActivity.class);
		context.startActivity(i);
	}
}
