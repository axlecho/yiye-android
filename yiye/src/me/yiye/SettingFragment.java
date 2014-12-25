package me.yiye;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by axlecho on 14-12-25.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_setting, null, false);
        init(v);
        return v;
    }

    private void init(View v) {

        Button aboutBtn = (Button) v.findViewById(R.id.btn_setting_about);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.launch(SettingFragment.this.getActivity());
            }
        });
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
    }
}
