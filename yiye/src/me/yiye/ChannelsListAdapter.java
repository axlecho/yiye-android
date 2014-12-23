package me.yiye;


import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.yiye.contents.Channel;
import me.yiye.contents.ChannelEx;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

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
        TextView channelBookBtn;
        ChannelEx c = channels.get(pos);
        if (convertView == null) {
            v = View.inflate(context, R.layout.item_channel_style, null);
        } else {
            v = convertView;
        }

        channelLogoImageView = (ImageView) v.findViewById(R.id.imageview_channel_item);
        ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + c.logo, channelLogoImageView, imageoptions);

        channelNameTextView = (TextView) v.findViewById(R.id.textview_channel_item_title);
        channelNameTextView.setText(c.name);

        channelDescriptionTextView = (TextView) v.findViewById(R.id.textview_channel_item_content);
        channelDescriptionTextView.setText(c.description);

        channelBookBtn = (TextView) v.findViewById(R.id.btn_packet_item_book);
        if (c.isAttention) {
            channelBookBtn.setText(context.getResources().getString(R.string.booked_decribe));
            channelBookBtn.setEnabled(false);
        }
        channelBookBtn.setOnClickListener(new ItemOnClickListener(this.getItem(pos)));
        return v;
    }

    public void setData(List<ChannelEx> channels) {
        // 保证channels不为空引用 防止加载时出现NullPointer
        if (channels == null) {
            return;
        }

        this.channels = channels;
    }

    class ItemOnClickListener implements View.OnClickListener {

        public ItemOnClickListener(ChannelEx c) {
            this.c = c;

        }

        private ChannelEx c;

        @Override
        public void onClick(View view) {
            SearchFragment.bookChannel(context, c, new YiyeApiImp(context),ChannelsListAdapter.this);
        }
    }


}