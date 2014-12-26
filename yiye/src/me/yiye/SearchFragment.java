package me.yiye;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.yiye.contents.ChannelEx;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchActivity";
    private ListView channelsListView;
    private ChannelsListAdapter channelsListAdapter;

    private View v;
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_seach, null, false);
        initAllChannelList(v);
        return v;
    }

    private void initAllChannelList(View v) {
        channelsListView = (ListView) v.findViewById(R.id.listview_search_channelsets);
        channelsListAdapter = new ChannelsListAdapter(this.getActivity());
        channelsListView.setAdapter(channelsListAdapter);
        channelsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ChannelActivity.launch(SearchFragment.this.getActivity(), channelsListAdapter.getItem(pos));
            }
        });

        channelsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,final View view, final int pos, long l) {
                new AlertDialog.Builder(SearchFragment.this.getActivity())
                        .setTitle("取消订阅书签")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SearchFragment.unBookChannel(SearchFragment.this.getActivity() ,channelsListAdapter.getItem(pos),
                                        new YiyeApiImp(SearchFragment.this.getActivity()),channelsListAdapter);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return false;
            }
        });


        mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pulltorefreshlayout_seach);
        ActionBarPullToRefresh.from(this.getActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        freshdata(new YiyeApiImp(SearchFragment.this.getActivity()));
                    }
                })
                .setup(mPullToRefreshLayout);

        freshdata(new YiyeApiImp(this.getActivity()));
        mPullToRefreshLayout.setRefreshing(true);
    }

    public static void bookChannel(final Context context,final ChannelEx c, final YiyeApi api,final ChannelsListAdapter adapter) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... v) {

                String ret = api.bookChannel(c);
                if (ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }

                return ret;
            }

            @Override
            protected void onPostExecute(String ret) {
                Toast.makeText(context, "订阅成功", Toast.LENGTH_LONG).show(); // 显示成功信息
                c.isAttention = true;
                adapter.notifyDataSetChanged();
                super.onPostExecute(ret);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(context, api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                super.onCancelled();
            }
        }.execute();
    }

    public static void unBookChannel(final Context context,final ChannelEx c, final YiyeApi api,final ChannelsListAdapter adapter) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... v) {

                String ret = api.unBookChannel(c);
                if (ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }

                return ret;
            }

            @Override
            protected void onPostExecute(String ret) {
                Toast.makeText(context, "取消订阅成功", Toast.LENGTH_LONG).show(); // 显示成功信息
                c.isAttention = false;
                adapter.notifyDataSetChanged();
                super.onPostExecute(ret);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(context, api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                super.onCancelled();
            }
        }.execute();
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, SearchFragment.class);
        context.startActivity(i);
    }

    private void freshdata(final YiyeApi api) {
        // 获取频道数据
        new AsyncTask<Void, Void, List<ChannelEx>>() {
            @Override
            protected List<ChannelEx> doInBackground(Void... words) {
                List<ChannelEx> ret = api.getChannelByPage(1); // 服务器的页数从1开始的，真是奇葩的家伙
                if (ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }
                return ret;
            }

            @Override
            protected void onPostExecute(List<ChannelEx> list) {
                MLog.d(TAG, "onPostExecute### get channels");
                channelsListAdapter.setData(list);
                channelsListAdapter.notifyDataSetChanged();
                mPullToRefreshLayout.setRefreshComplete();
                super.onPostExecute(list);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(SearchFragment.this.getActivity(), api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                mPullToRefreshLayout.setRefreshComplete();
                super.onCancelled();
            }
        }.execute();
    }
}
