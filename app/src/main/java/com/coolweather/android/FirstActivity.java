package com.coolweather.android;



import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.IslamicCalendar;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;



import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class FirstActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private List<Fragment> list;

    private MyAdapter adapter;

    private String[] titles = {"首页", "多城市"};

    private Button cehuaButton;
    public DrawerLayout drawerLayout;






    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //实例化
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        cehuaButton = (Button) findViewById(R.id.cehua_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        cehuaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerLayout();
            }
        });

        //页面，数据源
        list = new ArrayList<>();
        list.add(new FirstFragment());
        list.add(new SecondFragment());

        //ViewPager的适配器
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //绑定
        tabLayout.setupWithViewPager(viewPager);

    }


    private void showDrawerLayout() {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }}



    class MyAdapter extends FragmentPagerAdapter {



        public MyAdapter(FragmentManager fm) {

            super(fm);

        }

        @Override

        public Fragment getItem(int position) {

            return list.get(position);

        }



        @Override

        public int getCount() {

            return list.size();

        }



        //重写这个方法，将设置每个Tab的标题

        @Override

        public CharSequence getPageTitle(int position) {

            return titles[position];

        }

    }

}