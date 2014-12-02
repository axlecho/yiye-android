package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.yiye.contents.ChannelEx;
import me.yiye.utils.MLog;
import me.yiye.utils.YiyeApi;
import me.yiye.utils.YiyeApiImp;

public class SearchActivity extends BaseActivity {
	private static final String TAG = "SearchActivity";
	private EditText searchEditText;
	private ListView channelsListView;
	private ChannelsListAdapter channelsListAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_seach);
		initActionbar("发现");
		initSearch();
        initAllChannelList();
	}
	

	private void initSearch() {
		
		final Button search = (Button) this.findViewById(R.id.btn_search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSearch();
			}
		});
		
		searchEditText = (EditText) this.findViewById(R.id.edittext_search_keyword);
		searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent e) {
				if (KeyEvent.KEYCODE_ENTER == keyCode && e.getAction() == KeyEvent.ACTION_DOWN) {
					doSearch();
				}
				return false;
			}
		});
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				Editable editable = searchEditText.getText();
				if(editable.length() >  0) {
					search.setVisibility(View.VISIBLE);
				} else {
					search.setVisibility(View.GONE);
				}
			}

		});

	}

    private void initAllChannelList() {
        channelsListView = (ListView) this.findViewById(R.id.listview_search_channelsets);
        channelsListAdapter = new ChannelsListAdapter(this);
        channelsListView.setAdapter(channelsListAdapter);
        channelsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ChannelActivity.launch(SearchActivity.this,channelsListAdapter.getItem(pos));
            }
        });

        freshdata(new YiyeApiImp(this));
    }

	private void doSearch() {
		String keyword = searchEditText.getText().toString();
		MLog.d(TAG, "onKey### search edit content:" + keyword);
		ResultActivity.launch(SearchActivity.this, keyword);
	}

	public static void launch(Context context) {
		Intent i = new Intent();
		i.setClass(context,SearchActivity.class);
		context.startActivity(i);
	}

    private void freshdata(final YiyeApi api) {
        // 获取频道数据
        new AsyncTask<Void, Void,  List<ChannelEx>>() {
            @Override
            protected List<ChannelEx> doInBackground(Void... words) {
                List<ChannelEx> ret = api.getChannelByPage(1); // 服务器的页数从1开始的，真是奇葩的家伙
                if(ret == null) {
                    cancel(false); // 网络异常 跳到onCancelled处理异常
                }
                return ret;
            }

            @Override
            protected void onPostExecute(List<ChannelEx> list) {
                MLog.d(TAG, "onPostExecute### get channels");
                channelsListAdapter.setData(list);
                channelsListAdapter.notifyDataSetChanged();
                super.onPostExecute(list);
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(SearchActivity.this, api.getError(), Toast.LENGTH_LONG).show(); // 异常提示
                super.onCancelled();
            }
        }.execute();
    }
}
