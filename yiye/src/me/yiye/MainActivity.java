package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yiye.utils.YiyeApi;


public class MainActivity extends FragmentActivity {

    private List<Map<String, Object>> mMatchData;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private Map<String,Object> loginBtnMap; // 保存登录的Map以方便添加移除login按钮
    private final static String TAG = "MainActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMatchData = new ArrayList<Map<String, Object>>();
        initNavigationDrawer();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View headerView = View.inflate(this, R.layout.view_main_drawer_header, null);
        mDrawerList.addHeaderView(headerView);

        String[] from = new String[]{"img", "text"};
        int[] to = new int[]{R.id.imageview_main_drawer_ico, R.id.textview_main_drawer_text};
        mDrawerList.setAdapter(new SimpleAdapter(this, mMatchData, R.layout.item_main_drawer_list, from, to));
        mDrawerList.setOnItemClickListener(new OnDrawerListItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.drawer_close);
                invalidateOptionsMenu();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.drawer_open);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Enabling Home button
        getActionBar().setHomeButtonEnabled(true);

        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initMainFragment();
        initNavigationDrawer();
        if(YiyeApplication.user != null) { // 没有登录时 去除登录按钮
            mMatchData.remove(loginBtnMap);
        }
        setUserInfo();
    }

    private void initNavigationDrawer() {
        mMatchData.clear();
        String[] planetTitles = getResources().getStringArray(R.array.planets_array);
        int[] planetIco = {R.drawable.ic_drawer_home_normal,
                R.drawable.ic_drawer_collect_normal,
                R.drawable.ic_drawer_explore_normal,
                R.drawable.ic_drawer_login_normal,
                R.drawable.ic_drawer_setting_normal};
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> h = new HashMap<String, Object>();
            h.put("img", planetIco[i]);
            h.put("text", planetTitles[i]);
            mMatchData.add(h);

            // shit 一般的代码
            if(h.get("text").equals("登录")) {
                loginBtnMap = h;
            }
        }
    }

    private void initMainFragment() {
        Fragment packetFragment = new PacketFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, packetFragment).commit();
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, MainActivity.class);
        context.startActivity(i);
    }

    private class OnDrawerListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            int pos = i - 1; // 减去headerview
            mDrawerLayout.closeDrawers();
            String btnSelectString = (String) mMatchData.get(pos).get("text");
            if(btnSelectString.equals("首页")) {

            } else if( btnSelectString.equals("收藏")) {

            } else if(btnSelectString.equals("发现")) {
                SearchActivity.launch(MainActivity.this);
            } else if(btnSelectString.equals("登录")) {
                LoginManagerActivity.launch(MainActivity.this);
            } else if(btnSelectString.equals("设置")) {
                SettingActivity.launch(MainActivity.this);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_loading)
            .showImageForEmptyUri(R.drawable.img_empty)
            .showImageOnFail(R.drawable.img_failed)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(false)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();


    private void setUserInfo() {
        // 设置头像
        ImageView userimageView = (ImageView) this.findViewById(R.id.imageview_personal_userimg);
        TextView usernameTextView = (TextView) this.findViewById(R.id.textview_personal_username);
        if (YiyeApplication.user != null) { // 若已经登陆，设置头像及姓名
            ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + YiyeApplication.user.avatar + YiyeApi.PICSCALEPARAM, userimageView, imageoptions);
            usernameTextView.setText(YiyeApplication.user.username);
        } else {
            userimageView.setImageResource(R.drawable.ic_launcher);
            usernameTextView.setText(this.getResources().getString(R.string.username_no_authentication));
        }
    }
}
