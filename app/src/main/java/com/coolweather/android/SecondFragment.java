package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.db.City;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SecondFragment extends Fragment {


    public SwipeRefreshLayout swipeRefresh;
    private View view;
    private ImageView bingPicImg2;
    private int[] icons = {R.layout.city_item_sunny, R.layout.city_item_cloudy, R.layout.city_item_rain, R.layout.city_item_snow, R.layout.city_item_overcast};
    private LinearLayoutManager layoutManager;
    private City_Adapter adapter;
    private RecyclerView mRecy;
    private List<CityInfo> cityInfoList=new ArrayList<>();
    private List<CityInfo> cityInfoList2=new ArrayList<>();
    private static String cityna="",tem="",con="",oldcity="";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View view = inflater.inflate(R.layout.second_fragment, container, false);


        mRecy = (RecyclerView)view.findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(getActivity());
        mRecy.setLayoutManager(layoutManager);
        adapter=new City_Adapter(cityInfoList);

        mRecy.setAdapter(adapter);
        initList2();
        //bingPicImg2 = (ImageView) view.findViewById(R.id.bing_pic_img2);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh1);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(oldcity.equals(cityna))
                        {
                            swipeRefresh.setRefreshing(false);
                        }
                        else {
                            CityInfo c2 = new CityInfo();
                            c2.setCity(cityna);
                            c2.setTemp(tem);
                            c2.setCond(con);
                            if (con.contains("晴"))
                                c2.setImageId(R.mipmap.sunny);
                            else if (con.contains("雨"))
                                c2.setImageId(R.mipmap.heavy_rain);
                            else if (con.contains("雪"))
                                c2.setImageId(R.mipmap.hail);
                            else if (con.contains("云"))
                                c2.setImageId(R.mipmap.cloudytosunny);
                            else if (con.contains("阴"))
                                c2.setImageId(R.mipmap.cloudy);
                            c2.save();
                            adapter.addItem(cityInfoList.size(), c2);
                            oldcity = cityna;
                            swipeRefresh.setRefreshing(false);
                        }
                        //Toast.makeText(getActivity(),"sdgsdhshsdh",Toast.LENGTH_SHORT).show();
                    }
                },500);
            }

        });

        return view;

    }
    public void initList2(){
      List<CityInfo> cityInfos=DataSupport.findAll(CityInfo.class);
      for(CityInfo cityInfo:cityInfos){
          cityInfoList.add(cityInfo);
      }
    }
    public void initList(String city,String temp,String cond){
        cityna=city;
        tem=temp;
        con=cond;
    }
    public void refresh(){
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh1);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CityInfo c2=new CityInfo();
                        c2.setCity(cityna);
                        c2.setTemp(tem);
                        c2.setCond(con);
                        c2.setImageId(R.mipmap.sunny);
                        adapter.addItem(cityInfoList.size(),c2);
                        //cityInfoList.add(c2);
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getActivity(),"sdgsdhshsdh",Toast.LENGTH_SHORT).show();
                    }
                },1000);
            }

        });
    }
/*
    private void initData() {
        mData = new ArrayList<>();
//        随机数 用来标记item界面的类型
        Random random = new Random();

        for (int i = 0; i < icons.length; i++) {
            City more = new City();

            more.pic = icons[i];
            more.type = random.nextInt(5);
            mData.add(more);
        }
    }

    private void initViewOper() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecy.setLayoutManager(linearLayoutManager);
        City_Adapter adapter = new City_Adapter(mData);
        mRecy.setAdapter(adapter);
    }
    //获取每日一图
    private void loadBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {



            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

                editor.putString("bing_pic", bingPic);

                editor.apply();

                getActivity().runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        Glide.with(getActivity()).load(bingPic).into(bingPicImg2);

                    }

                });

            }




            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

            }

        });

    }*/
}
