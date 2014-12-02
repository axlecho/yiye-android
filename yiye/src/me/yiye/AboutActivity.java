package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionbar(getResources().getString(R.string.about_describe));
        setContentView(R.layout.activity_about);

    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, AboutActivity.class);
        context.startActivity(i);
    }

}

