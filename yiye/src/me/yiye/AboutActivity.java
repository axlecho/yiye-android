package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import me.yiye.contents.BookMark;


public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionbar(getResources().getString(R.string.about_describe));
        setContentView(R.layout.activity_about);

        findViewById(R.id.textview_about_bug).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BookMark b = new BookMark();
                b.url = getResources().getString(R.string.bug_url);
                b.title = getResources().getString(R.string.bug_feedback_decribe);
                BookMarkActivity.launch(AboutActivity.this, b);
            }
        });

        // 设置版本号粗体
        TextView versionTextView = (TextView) findViewById(R.id.textview_about_version);
        versionTextView.getPaint().setFakeBoldText(true);
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, AboutActivity.class);
        context.startActivity(i);
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

