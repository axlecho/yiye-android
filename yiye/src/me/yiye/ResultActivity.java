package me.yiye;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.MenuItem;

public class ResultActivity extends BaseActivity {
	
	private ListView resultListView;
	private List<HashMap<String,Object> > resultList = new ArrayList<HashMap<String,Object> >();
	private SimpleAdapter resultAdpater;
	
	private static String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_result);
		initActionbar("关于\"" + title + "\"的频道");
		
		resultListView = (ListView) this.findViewById(R.id.listview_result);
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("img", R.drawable.ic_launcher);
		map.put("tittle", "科技");
		map.put("content", "iphone android google 一页");
		resultList.add(map);
		
		String[] from = new String[] {"img","tittle","content"};
		int[] to = new int[] {R.id.imageview_channel_item,R.id.textview_channel_item_title,
				R.id.textview_channel_item_content};
		resultAdpater = new SimpleAdapter(this, resultList, R.layout.item_channel_style, from, to);
		resultListView.setAdapter(resultAdpater);
		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,long id) {

			}
		});
	}
	
	public static void launch(Context context) {
		Intent i = new Intent();
		i.setClass(context,ResultActivity.class);
		context.startActivity(i);
	}
	
	public static void launch(Context context,String title) {
		ResultActivity.title = title;
		launch(context);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
