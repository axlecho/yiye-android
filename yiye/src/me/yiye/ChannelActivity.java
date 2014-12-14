package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.yiye.contents.BookMark;
import me.yiye.contents.Channel;
import me.yiye.contents.ChannelEx;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;
import me.yiye.utils.YiyeApiOfflineImp;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ChannelActivity extends BaseActivity {
    private final static String TAG = "ChannelActivity";
    private ChannelAdapter bookMarkListViewAdapter;
    private ListView bookMarkListView;

    private static String title = null;
    private static String channelid = null;

    private View emptyInfoView;
    private static AsyncTask freshAsyncTask;
    private PullToRefreshLayout pullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_channel);
        initActionbar(title);
        initChannelListView();
    }

    private void initChannelListView() {
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pulltorefreshlayout_channel);
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        MLog.i(TAG, "onRefreshStarted### load data from network");
                        freshdata(new YiyeApiImp(ChannelActivity.this));
                    }
                })
                .setup(pullToRefreshLayout);

        bookMarkListView = (ListView) findViewById(R.id.listview_channel_bookmarks);
        bookMarkListView.setBackgroundColor(getResources().getColor(R.color.activitybackgroud));
        bookMarkListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                BookMarkActivity.launch(ChannelActivity.this, bookMarkListViewAdapter.getItem(pos));
            }
        });

        bookMarkListViewAdapter = new ChannelAdapter(this);
        bookMarkListView.setAdapter(bookMarkListViewAdapter);

        emptyInfoView = findViewById(R.id.imageview_channel_emptyinfo);

        MLog.i(TAG, "init### load data offline");
        freshdata(new YiyeApiOfflineImp(this), new OnFreshCompleteListener() {
            @Override
            public void freshComplete(List<BookMark> list) {
                if (list.size() == 0) { // 如果离线没有数据 ，加载网络数据
                    MLog.i(TAG, "freshComplete### load data from network");
                    pullToRefreshLayout.setRefreshing(true);
                    freshdata(new YiyeApiImp(ChannelActivity.this));
                }
            }
        });
    }

    private class ChannelAdapter extends BaseAdapter {
        private List<BookMark> itemList = new ArrayList<BookMark>();
        private Context context;

        public ChannelAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<BookMark> bookMarkList) {
            itemList = bookMarkList;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public BookMark getItem(int pos) {
            return itemList.get(pos);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            BookMark b = itemList.get(pos);
            View v;
            if (convertView == null) {
                v = View.inflate(context, R.layout.item_bookmark_style, null);
            } else {
                v = convertView;
            }

            ImageView contentImageView;
            TextView titleTextView;
            // TextView descriptionTextView;
            TextView praiseTextView;
            TextView uploaderTextView;
            TextView uploadTimeTextView;

            contentImageView = (ImageView) v.findViewById(R.id.imageview_bookmark_item);
            ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + b.image + YiyeApi.PICSCALEPARAM, contentImageView, YiyeApplication.imageoptions);

            titleTextView = (TextView) v.findViewById(R.id.textview_bookmark_item_title);
            titleTextView.setText(b.title);

            // descriptionTextView = (TextView)
            // v.findViewById(R.id.textview_bookmark_item_content);
            // descriptionTextView.setText(c.getBookmark().description);

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append('\uFFFC'); // 替换字符
            ssb.setSpan(new ImageSpan(ChannelActivity.this, R.drawable.ic_good), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" " + b.likeNum);
            praiseTextView = (TextView) v.findViewById(R.id.textview_bookmark_item_good);
            praiseTextView.setText(ssb);

            ssb = new SpannableStringBuilder();
            ssb.append('\uFFFC'); // 替换字符
            ssb.setSpan(new ImageSpan(ChannelActivity.this, R.drawable.ic_uploader), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" " + b.postUser);
            uploaderTextView = (TextView) v.findViewById(R.id.textview_bookmark_item_uploader);
            uploaderTextView.setText(ssb);

            uploadTimeTextView = (TextView) v.findViewById(R.id.textview_bookmark_item_uploadtime);

            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
            DateFormat fmt2 = new SimpleDateFormat("MM-dd", Locale.CHINA);
            String timeString = b.postTime;
            try {
                Date time = fmt.parse(b.postTime);
                timeString = fmt2.format(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ssb = new SpannableStringBuilder();
            ssb.append('\uFFFC');
            ssb.setSpan(new ImageSpan(ChannelActivity.this, R.drawable.ic_clock), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" " + timeString);
            uploadTimeTextView.setText(ssb);
            return v;
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, ChannelActivity.class);
        context.startActivity(i);
    }

    public static void launch(Context context, Channel channel) {
        title = channel.name;
        channelid = channel.channelId;
        launch(context);
    }

    public static void launch(Context context, ChannelEx channel) {
        title = channel.name;
        channelid = channel.id;
        launch(context);
    }

    private void freshdata(final YiyeApi api) {
        freshdata(api, null);
    }

    private void freshdata(final YiyeApi api, final OnFreshCompleteListener listener) {

        // 获取书签数据
        new AsyncTask<Void, Void, List<BookMark>>() {
            @Override
            protected List<BookMark> doInBackground(Void... v) {
                List<BookMark> ret = api.getBookMarksByChannelId(channelid);
                if (ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }
                return ret;
            }

            @Override
            protected void onPostExecute(List<BookMark> list) {

                bookMarkListViewAdapter.setData(list);
                bookMarkListViewAdapter.notifyDataSetChanged();

                if (list.size() == 0) {
                    emptyInfoView.setVisibility(View.VISIBLE);
                } else {
                    emptyInfoView.setVisibility(View.GONE);
                }

                if (listener != null) {
                    listener.freshComplete(list);
                }
                pullToRefreshLayout.setRefreshComplete();
                super.onPostExecute(list);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(ChannelActivity.this, api.getError(), Toast.LENGTH_LONG).show();
                pullToRefreshLayout.setRefreshComplete();
                super.onCancelled();
            }
        }.execute();
    }

    private interface OnFreshCompleteListener {
        public void freshComplete(List<BookMark> list);
    }

    @Override
    public void onStop() {
        if (freshAsyncTask != null) {
            MLog.i(TAG, "onStop### cancel the fresh task");
            freshAsyncTask.cancel(true);
        }
        super.onStop();
    }
}
