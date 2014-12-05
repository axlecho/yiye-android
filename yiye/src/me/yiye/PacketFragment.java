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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.Channel;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;
import me.yiye.utils.YiyeApiOfflineImp;

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

	private ChannelsGridAdapter dataadpter;
	private PullToRefreshGridView pullableView;
	private GridView mainDataGridView;


    private static AsyncTask freshAsyncTask;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_packet, null, false);
		init(v);
		
		return v;
	}
	
	private void init(View v) {
		dataadpter = new ChannelsGridAdapter(this.getActivity());
		pullableView = (PullToRefreshGridView) v.findViewById(R.id.gridview_main_content);
		// pullableView.getLoadingLayoutProxy().setPullLabel("你妹的");
		pullableView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.ic_star));
		
		// 下拉刷新数据
		pullableView.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                MLog.i(TAG,"onRefresh### load data from network");
				freshdata(new YiyeApiImp(PacketFragment.this.getActivity()));
			}
		});
		mainDataGridView = pullableView.getRefreshableView();
		mainDataGridView.setBackgroundColor(getResources().getColor(R.color.activitybackgroud));
		mainDataGridView.setAdapter(dataadpter);
		mainDataGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,long id) {
				ChannelActivity.launch(PacketFragment.this.getActivity(),dataadpter.getItem(pos));
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
	
	class ChannelsGridAdapter extends BaseAdapter {
		private Context context;
		private List<Channel> channels = new ArrayList<Channel>();
		
		ChannelsGridAdapter(Context context) {
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
			Channel c = channels.get(pos);

			if (convertView == null) {
				v = View.inflate(context, R.layout.item_packet_style, null);
				v.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                        (int)(150 * context.getResources().getDisplayMetrics().density + 0.5f))); //px 转 dp
				v.setBackgroundResource(R.drawable.packetitem_style);
			} else {
				v = convertView;
			}
			
			channelLogoImageView = (RoundedImageView) v.findViewById(R.id.imageview_packet_item_background);
			channelLogoImageView.setAdjustViewBounds(false);
			channelLogoImageView.setCornerRadius(4.0f);
			ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + c.logo + YiyeApi.PICSCALEPARAM, channelLogoImageView,imageoptions);
			
			channelNameTextView = (TextView) v.findViewById(R.id.textview_over_item_notice);
			channelNameTextView.setText(c.name);
			newsTextView = (TextView) v.findViewById(R.id.textview_over_item_news);
			newsTextView.setText(c.news + "");
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
				pullableView.onRefreshComplete();
                freshAsyncTask = null;
                if(listener != null) {
                    listener.freshComplete(list);
                }
				super.onPostExecute(list);
			}
			
			@Override
			protected void onCancelled() {
				Toast.makeText(PacketFragment.this.getActivity(), api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
				pullableView.onRefreshComplete();
                freshAsyncTask = null;
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
