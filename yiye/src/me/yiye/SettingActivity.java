package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    private Button aboutBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        initActionbar(getResources().getString(R.string.setting_describe));
        setContentView(R.layout.activity_setting);
        aboutBtn = (Button)findViewById(R.id.btn_setting_about);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.launch(SettingActivity.this);
            }
        });

        super.onCreate(savedInstanceState);
    }


    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, SettingActivity.class);
        context.startActivity(i);
    }
}
