package me.yiye;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.ChannelEx;
import me.yiye.utils.YiyeApi;

class ChannelsListAdapter extends BaseAdapter {

    private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_loading)
            .showImageForEmptyUri(R.drawable.img_empty)
            .showImageOnFail(R.drawable.img_failed)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

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
        ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + c.logo, channelLogoImageView, imageoptions);

        channelNameTextView = (TextView) v.findViewById(R.id.textview_channel_item_title);
        channelNameTextView.setText(c.name);

        channelDescriptionTextView = (TextView) v.findViewById(R.id.textview_channel_item_content);
        channelDescriptionTextView.setText(c.description);

        return v;
    }

    public void setData(List<ChannelEx> channels) {
        // 保证channels不为空引用 防止加载时出现NullPointer
        if (channels == null) {
            return;
        }

        this.channels = channels;
    }
}