package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.yiye.contents.ChannelEx;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

public class ResultActivity extends BaseActivity {


    private final static String TAG = "ResultActivity";
    private ListView resultListView;
    private ChannelsListAdapter dataadpter;

    private static String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_result);
        initActionbar("关于\"" + title + "\"的频道");

        resultListView = (ListView) this.findViewById(R.id.listview_result);
        dataadpter = new ChannelsListAdapter(this);
        resultListView.setAdapter(dataadpter);
        resultListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ChannelActivity.launch(ResultActivity.this,dataadpter.getItem(pos));
            }
        });

        freshdata(new YiyeApiImp(this));
    }

    private void freshdata(final YiyeApi api) {
        // 获取频道数据
        new AsyncTask<String, Void,  List<ChannelEx>>() {
            @Override
            protected List<ChannelEx> doInBackground(String... words) {
                List<ChannelEx> ret = api.search(words[0]);
                if(ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }
                return ret;
            }

            @Override
            protected void onPostExecute(List<ChannelEx> list) {
                MLog.d(TAG, "onPostExecute### get search channels");
                dataadpter.setData(list);
                dataadpter.notifyDataSetChanged();
                super.onPostExecute(list);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(ResultActivity.this, api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                super.onCancelled();
            }
        }.execute(title);
    }


    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, ResultActivity.class);
        context.startActivity(i);
    }

    public static void launch(Context context, String title) {
        ResultActivity.title = title;
        launch(context);
    }
}
