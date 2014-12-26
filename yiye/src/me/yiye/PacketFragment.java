package me.yiye;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.yiye.contents.Channel;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;
import me.yiye.utils.YiyeApiOfflineImp;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class PacketFragment extends Fragment {
	private final static String TAG = "PacketFragment";
	private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.img_loading)
		.showImageForEmptyUri(R.drawable.img_empty)
		.showImageOnFail(R.drawable.img_failed)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();

	private ChannelsListAdapter dataadpter;
    private PullToRefreshLayout mPullToRefreshLayout;
	private ListView mainDataListView;
    private View emptyInfoView;

    private static AsyncTask freshAsyncTask;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_packet, null, false);
		init(v);
		
		return v;
	}
	
	private void init(View v) {
        emptyInfoView = v.findViewById(R.id.imageview_packet_emptyinfo);
		dataadpter = new ChannelsListAdapter(this.getActivity());
        mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pulltorefreshlayout_channel);
        ActionBarPullToRefresh.from(this.getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        MLog.i(TAG,"onRefresh### load data from network");
                        freshdata(new YiyeApiImp(PacketFragment.this.getActivity()));
                    }
                })
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);


		mainDataListView =  (ListView)v.findViewById(R.id.listview_main_content);
		mainDataListView.setBackgroundColor(getResources().getColor(R.color.activitybackgroud));
		mainDataListView.setAdapter(dataadpter);
		mainDataListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ChannelActivity.launch(PacketFragment.this.getActivity(), dataadpter.getItem(pos));
            }
        });
		
		// 加载离线数据
        MLog.i(TAG,"init### load data offline");
		freshdata(new YiyeApiOfflineImp(this.getActivity()),new OnFreshCompleteListener() {
            @Override
            public void freshComplete(List<Channel> list) {
                if(list.size() == 0) { // 如果离线没有数据 ，加载网络数据
                    MLog.i(TAG,"freshComplete### load data from network");
                    freshdata(new YiyeApiImp(PacketFragment.this.getActivity()));
                }

            }
        });
	}
	
	class ChannelsListAdapter extends BaseAdapter {
		private Context context;
		private List<Channel> channels = new ArrayList<Channel>();
		
		ChannelsListAdapter(Context context) {
			this.context = context;
			channels = new ArrayList<Channel>();
		}

		@Override
		public int getCount() {
			return channels.size();
		}

		@Override
		public Channel getItem(int item) {
			return channels.get(item);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {

			View v;
			RoundedImageView channelLogoImageView;
			TextView channelNameTextView;
			TextView newsTextView;
            TextView timeTextView;
			Channel c = channels.get(pos);

			if (convertView == null) {
				v = View.inflate(context, R.layout.item_packet_style, null);
				v.setBackgroundResource(R.drawable.packetitem_style);
			} else {
				v = convertView;
			}
			
			channelLogoImageView = (RoundedImageView) v.findViewById(R.id.imageview_packet_item_background);
			channelLogoImageView.setAdjustViewBounds(false);
			channelLogoImageView.setCornerRadius(4.0f);
			ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + c.logo + YiyeApi.PICSCALEPARAM, channelLogoImageView,imageoptions);
			
			channelNameTextView = (TextView) v.findViewById(R.id.textview_packet_item_notice);
			channelNameTextView.setText(c.name);
			newsTextView = (TextView) v.findViewById(R.id.textview_packet_item_news);
			newsTextView.setText(c.news + "");
//            if(c.news > 0) {
//                newsTextView.setVisibility(View.VISIBLE);
//            } else {
//                newsTextView.setVisibility(View.GONE);
//            }
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
            DateFormat fmt2 = new SimpleDateFormat("MM-dd", Locale.CHINA);
            String timeString = c.lastTime;
            try {
                Date time = fmt.parse(c.lastTime);
                timeString = fmt2.format(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timeTextView = (TextView) v.findViewById(R.id.textview_packet_item_time);
            timeTextView.setText(timeString);
			return v;
		}
		
		public void setData(List<Channel> channels){
			// 保证channels不为空引用 防止加载时出现NullPointer
			if(channels == null) {
				return;
			}
			
			this.channels = channels;
		}
	}

    private void freshdata(final  YiyeApi api) {
        freshdata(api,null);
    }

	private void freshdata(final YiyeApi api, final OnFreshCompleteListener listener) {

		// 获取频道数据
        if(freshAsyncTask != null) {
            MLog.d(TAG,"freshdata### There been a fresh task.");
            return;
        }

        freshAsyncTask = new AsyncTask<Void, Void,  List<Channel>>() {
			@Override
			protected List<Channel> doInBackground(Void... v) {
				List<Channel> ret = api.getBookedChannels();
				if(ret == null) {
					cancel(false); // 网络异常 跳到onCancelled处理异常
				}
				return ret;
			}
			
			@Override
			protected void onPostExecute(List<Channel> list) {
				dataadpter.setData(list);
				dataadpter.notifyDataSetChanged();
                if (list.size() == 0) {
                    emptyInfoView.setVisibility(View.VISIBLE);
                } else {
                    emptyInfoView.setVisibility(View.GONE);
                }
                freshAsyncTask = null;
                if(listener != null) {
                    listener.freshComplete(list);
                }
                mPullToRefreshLayout.setRefreshComplete();
				super.onPostExecute(list);
			}
			
			@Override
			protected void onCancelled() {
				Toast.makeText(PacketFragment.this.getActivity(), api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                freshAsyncTask = null;
                mPullToRefreshLayout.setRefreshComplete();
				super.onCancelled();
			}
		}.execute();
	}

    private interface OnFreshCompleteListener{
        public void freshComplete(List<Channel> list);
    }

    @Override
    public void onStop() {
        if(freshAsyncTask != null) {
            MLog.i(TAG,"onStop### cancel the fresh task");
            freshAsyncTask.cancel(true);
        }
        super.onStop();
    }
}
