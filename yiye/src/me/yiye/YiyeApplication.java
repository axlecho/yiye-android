package me.yiye;

import me.yiye.contents.User;
import me.yiye.utils.MLog;
import me.yiye.utils.NetworkUtil;
import me.yiye.utils.SQLManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class YiyeApplication 	extends Application{
	
	
	private final static String TAG = "YiyeApplication";
	public static DisplayImageOptions imageoptions;
	static {
		imageoptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.img_loading)
			.showImageForEmptyUri(R.drawable.img_empty)
			.showImageOnFail(R.drawable.img_failed)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.displayer(new FadeInBitmapDisplayer(1000))
			.build();
	}
	
	public static User user;
	
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
		
		// 初始化数据库
		SQLManager.init(getApplicationContext());
		
		// 初始化网络
		NetworkUtil.init();
		
		// 加载用户信息
		initUser(getApplicationContext());
	}
	
	private void initUser(Context context) {
		SharedPreferences userSharedPreferences= context.getSharedPreferences("user", Activity.MODE_PRIVATE);
		String currentUserName = userSharedPreferences.getString("currentuser", "**unknow**");
		if(currentUserName.equals("**unknow**")) {
			return;
		}
		
		// 从数据库加载用户信息
		user = SQLManager.loaduser(context,currentUserName);
		if(user == null) {
			MLog.e(TAG, "initUser### loaduser 失败");
			return;
		} else {
			MLog.i(TAG, "initUser### loaduser ok user:" + user.toString());
		}
		
		//TODO 验证是否用户是否合法
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}
