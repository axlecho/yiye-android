package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.ChannelEx;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

public class ResultActivity extends BaseActivity {
    private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_loading)
            .showImageForEmptyUri(R.drawable.img_empty)
            .showImageOnFail(R.drawable.img_failed)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

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

    class ChannelsListAdapter extends BaseAdapter {
        private Context context;
        private List<ChannelEx> channels = new ArrayList<ChannelEx>();

        ChannelsListAdapter(Context context) {
            this.context = context;
            channels = new ArrayList<ChannelEx>();
        }

        @Override
        public int getCount() {
            return channels.size();
        }

        @Override
        public ChannelEx getItem(int item) {
            return channels.get(item);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            View v;
            ImageView channelLogoImageView;
            TextView channelNameTextView;
            TextView channelDescriptionTextView;
            ChannelEx c = channels.get(pos);
            if (convertView == null) {
                v = View.inflate(context, R.layout.item_channel_style, null);
            } else {
                v = convertView;
            }

            channelLogoImageView = (ImageView) v.findViewById(R.id.imageview_channel_item);
            channelLogoImageView.setAdjustViewBounds(false);
            ImageLoader.getInstance().displayImage(c.logo, channelLogoImageView,imageoptions);

            channelNameTextView = (TextView) v.findViewById(R.id.textview_channel_item_title);
            channelNameTextView.setText(c.name);

            channelDescriptionTextView = (TextView)v.findViewById(R.id.textview_channel_item_content);
            channelDescriptionTextView.setText(c.description);

            return v;
        }

        public void setData(List<ChannelEx> channels){
            // 保证channels不为空引用 防止加载时出现NullPointer
            if(channels == null) {
                return;
            }

            this.channels = channels;
        }
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
