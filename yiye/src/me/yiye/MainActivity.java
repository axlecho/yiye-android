package me.yiye;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yiye.utils.MLog;
import me.yiye.utils.Tools;
import me.yiye.utils.YiyeApi;


public class MainActivity extends FragmentActivity {

    private final static String TAG = "MainActivity";
    private static DisplayImageOptions imageoptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_loading)
            .showImageForEmptyUri(R.drawable.img_empty)
            .showImageOnFail(R.drawable.img_failed)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(false)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;

    private String activityTitle;
    private String drawderTitile;

    String[] planetTitles = new String[]{"首页", "发现", "关于"};

    int[] icos = new int[]{R.drawable.ic_drawer_home_normal,
            R.drawable.ic_drawer_explore_normal,
            R.drawable.ic_drawer_setting_normal};

    private View header;
    private MenuItem searchMenuItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getInstance().cleanActivityStack(); // 退出之前打开的activity 防止按返回键回到splashScreen或登录界面
        setContentView(R.layout.activity_main);
        if (YiyeApplication.user == null) {
            activityTitle = planetTitles[1];
            upDateMainFragment(new SearchFragment());
        } else {
            upDateMainFragment(new PacketFragment());
        }

        initDrawerLayout();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);


    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        drawerListView = (ListView) findViewById(R.id.list_drawer_main);
        header = View.inflate(this, R.layout.view_main_drawer_header, null);
        drawerListView.addHeaderView(header);


        setUserInfo();
        List<Map<String, Object>> drawerContentList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icos.length; i++) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("ico", icos[i]);
            m.put("title", planetTitles[i]);
            drawerContentList.add(m);
        }

        String[] from = new String[]{"ico", "title"};
        int[] to = new int[]{R.id.imageview_main_drawer_ico, R.id.textview_main_drawer_text};

        drawerListView.setAdapter(new SimpleAdapter(this, drawerContentList, R.layout.item_main_drawer_list, from, to));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(activityTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawderTitile);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Fragment activityFragment = null;
                switch (pos) {
                    case 0:
                        if(YiyeApplication.user == null) {
                            LoginManagerActivity.launch(MainActivity.this);
                            break;
                        }

                        PersonalActivity.launch(MainActivity.this);
                        break;
                    case 1:
                        if(YiyeApplication.user == null) {
                            LoginManagerActivity.launch(MainActivity.this);
                            break;
                        }
                        activityFragment = new PacketFragment();

                        break;
                    case 2:
                        activityFragment = new SearchFragment();
                        break;
                    case 3:
                        activityFragment = new AboutFragment();
                        break;
                    default:
                        break;
                }

                if (activityFragment != null) {
                    activityTitle = planetTitles[pos - 1];
                    upDateMainFragment(activityFragment);
                }
                drawerLayout.closeDrawers();
            }
        });

        drawerListView.setItemChecked(1, true); // 设置第一项被按下
        drawderTitile = getResources().getString(R.string.drawer_open);
        activityTitle = planetTitles[0];
        getActionBar().setTitle(activityTitle);
    }

    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    private void upDateMainFragment(Fragment packetFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, packetFragment).commit();
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, MainActivity.class);
        context.startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        searchMenuItem = menu.findItem(R.id.menu_main_search);
//        SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        if (searchView == null) {
//            MLog.e(TAG,"onCreateOptionsMenu### Fail to get Search View.");
//            return true;
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setUserInfo() {
        // 设置头像
        ImageView userimageView = (ImageView) header.findViewById(R.id.imageview_personal_userimg);
        TextView usernameTextView = (TextView) header.findViewById(R.id.textview_personal_username);
        if (YiyeApplication.user != null) { // 若已经登陆，设置头像及姓名
            ImageLoader.getInstance().displayImage(YiyeApi.PICCDN + YiyeApplication.user.avatar, userimageView, imageoptions);
            usernameTextView.setText(YiyeApplication.user.username);
        } else {
            userimageView.setImageResource(R.drawable.ic_launcher);
            usernameTextView.setText(this.getResources().getString(R.string.username_no_authentication));
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
