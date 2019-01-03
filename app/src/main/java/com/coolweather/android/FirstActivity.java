package com.coolweather.android;


import android.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.IslamicCalendar;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;



import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private List<CityInfo> cityInfoList;

    private String[] titles = {"首页", "多城市"};

    private Button cehuaButton;
    public DrawerLayout drawerLayout;
    private FloatingActionButton mFab;
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
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.hide();
       mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OtherCityDialog(FirstActivity.this, new OtherCityDialog.MyDialogListenner() {
                    @Override
                    public void ClickedSure(String cityName) {

                        requestWeatherbyName(cityName);


                    }
                }).show();
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
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            public void onPageSelected(int position) {
                if (position==0) {
                    mFab.hide();
                } else {
                    mFab.show();
                }
            }
        });
    }

    public void requestWeatherbyName(final String weatherId) {
        String  weatherUrl= "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=6130363df20b4d1ea928bb07257ace9a";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponseBYCIYT(responseText);//
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(FirstActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            String cityName = weather.basic.cityName;

                            String degree = weather.now.temperature + "℃";
                            String weatherInfo = weather.now.more.info;
                            //Toast.makeText(FirstActivity.this,cityName+degree+weatherInfo,Toast.LENGTH_SHORT).show();
                            SecondFragment secondFragment=new SecondFragment();
                            secondFragment.initList(cityName,degree,weatherInfo);
                            /*CityInfo cityInfo=new CityInfo();
                            cityInfo.setCond(weatherInfo);
                            cityInfo.setCity(cityName);
                            cityInfo.setTemp(degree);
                            cityInfo.setImageId(R.mipmap.sunny);
                            RecyclerView mRecy = (RecyclerView)findViewById(R.id.recyclerview);
                            LinearLayoutManager layoutManager=new LinearLayoutManager(FirstActivity.this);
                            mRecy.setLayoutManager(layoutManager);
                            City_Adapter adapter1=new City_Adapter(cityInfoList);
                            adapter1.addItem(cityInfoList.size(),cityInfo);*/

                        } else {
                            Toast.makeText(FirstActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FirstActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                       // swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
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