package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import me.yiye.contents.BookMark;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionbar(getResources().getString(R.string.about_describe));
        setContentView(R.layout.activity_about);
        findViewById(R.id.textview_about_bug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookMark b = new BookMark();
                b.url = "http://yiye.me/our/team/#b";
                b.title = getResources().getString(R.string.bug_feedback_decribe);
                BookMarkActivity.launch(AboutActivity.this,b);
            }
        });

        // 设置版本号粗体
        TextView versionTextView = (TextView)findViewById(R.id.textview_about_version);
        TextPaint tp = versionTextView.getPaint();
        tp.setFakeBoldText(true);
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, AboutActivity.class);
        context.startActivity(i);
    }

}

