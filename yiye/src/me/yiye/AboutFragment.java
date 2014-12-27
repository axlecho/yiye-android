package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;

import me.yiye.contents.BookMark;
import me.yiye.utils.MLog;


public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_about, null, false);
        init(v);
        return v;
    }

    private void init(View v) {
        v.findViewById(R.id.textview_about_bug).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BookMark b = new BookMark();
                b.url = getResources().getString(R.string.bug_url);
                b.title = getResources().getString(R.string.bug_feedback_decribe);
                BookMarkActivity.launch(AboutFragment.this.getActivity(), b);
            }
        });

        v.findViewById(R.id.btn_about_delectupdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MLog.d(TAG, "onClick### update");
                UmengUpdateAgent.forceUpdate(AboutFragment.this.getActivity());
            }
        });

        // 设置版本号粗体
        TextView versionTextView = (TextView) v.findViewById(R.id.textview_about_version);
        versionTextView.getPaint().setFakeBoldText(true);
    }
}

