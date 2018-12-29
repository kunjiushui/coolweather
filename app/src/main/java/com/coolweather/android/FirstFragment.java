package com.coolweather.android;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.coolweather.android.db.City;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import okhttp3.Call;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Response;

public class FirstFragment extends android.support.v4.app.Fragment {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    private String mWeatherId;
    private View view;
    public LocationClient mLocationClient;
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    private String cit;

    private static int num=1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        View view=inflater.inflate(R.layout.first_fragment, container, false);



        bingPicImg = (ImageView) view.findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        titleCity = (TextView) view.findViewById(R.id.title_city);
        titleUpdateTime = (TextView) view.findViewById(R.id.title_update_time);
        degreeText = (TextView) view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        aqiText = (TextView) view.findViewById(R.id.aqi_text);
        pm25Text = (TextView) view.findViewById(R.id.pm25_text);
        comfortText = (TextView) view.findViewById(R.id.comfort_text);
        carWashText = (TextView) view.findViewById(R.id.car_wash_text);
        sportText = (TextView) view.findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String weatherString = prefs.getString("weather", null);

        /*if (weatherString != null) {

            // 有缓存时直接解析天气数据

            Weather weather = Utility.handleWeatherResponse(weatherString);

            mWeatherId = weather.basic.weatherId;

            showWeatherInfo(weather);

        } else {*/

            // 无缓存时去服务器查询天气

            mWeatherId = getActivity().getIntent().getStringExtra("weather_id");
            //Toast.makeText(getActivity(), mWeatherId, Toast.LENGTH_SHORT).show();
            weatherLayout.setVisibility(View.INVISIBLE);
            if (mWeatherId != null) {
                //mWeatherId="CN101300101";


                requestWeather(mWeatherId);
            }





        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                requestWeather(mWeatherId);

            }

        });



        String bingPic = prefs.getString("bing_pic", null);

        if (bingPic != null) {

            Glide.with(this).load(bingPic).into(bingPicImg);

        } else {

            loadBingPic();

        }
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }
        return view;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(getActivity(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity(). finish();
                }
                break;
            default:
        }
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        num=1;
        mLocationClient.stop();
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(num==1){
                num++;
                requestWeatherbyName(location.getCity());

            }
            //Toast.makeText(getActivity(), location.getCity(), Toast.LENGTH_SHORT).show();
            /*//Log.d("getActivity()",location.getCityCode());
            Toast.makeText(getActivity(), location.getCity(), Toast.LENGTH_SHORT).show();
            requestWeather(location.getCityCode());*/
/*

            int provin=0,wea=0;

            provinceList = DataSupport.findAll(Province.class);

            for (Province province : provinceList) {
               if(location.getProvince().equals(province.getProvinceName()))
               {
                   provin=province.getProvinceCode();

               }
            }
            Toast.makeText(getActivity(), provin, Toast.LENGTH_SHORT).show();
            cityList = DataSupport.where("provinceid = ?", String.valueOf(provin)).find(City.class);
            for (City city : cityList) {
                if(city.getCityName().equals(location.getCity())){
                    wea=city.getCityCode();
                }
            }
            Toast.makeText(getActivity(), wea, Toast.LENGTH_SHORT).show();*/
        }

    }
    /**

     * 根据天气id请求城市天气信息。

     */

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=dc6f30eba6ae4cb0a8d96a52bccbfa79";


        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);

                getActivity().runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

                            editor.putString("weather", responseText);

                            editor.apply();

                            mWeatherId = weather.basic.weatherId;

                            showWeatherInfo(weather);

                        } else {

                            Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }

                        swipeRefresh.setRefreshing(false);

                    }

                });

            }




            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        swipeRefresh.setRefreshing(false);

                    }

                });

            }

        });

        loadBingPic();

    }

    public void requestWeatherbyName(final String weatherId) {
        String  weatherUrl= "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=6130363df20b4d1ea928bb07257ace9a";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponseBYCIYT(responseText);

                getActivity().runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

                            editor.putString("weather", responseText);

                            editor.apply();

                            mWeatherId = weather.basic.weatherId;

                            showWeatherInfo(weather);

                        } else {

                            Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }

                        swipeRefresh.setRefreshing(false);

                    }

                });

            }




            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        swipeRefresh.setRefreshing(false);

                    }

                });

            }

        });

        loadBingPic();

    }


    /**

     * 加载必应每日一图

     */

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

                        Glide.with(getActivity()).load(bingPic).into(bingPicImg);

                    }

                });

            }




            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

            }

        });

    }



    /**

     * 处理并展示Weather实体类中的数据。

     */

    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;

        String updateTime = weather.basic.update.updateTime.split(" ")[1];

        String degree = weather.now.temperature + "℃";

        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);

        titleUpdateTime.setText(updateTime);

        degreeText.setText(degree);

        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);

            TextView infoText = (TextView) view.findViewById(R.id.info_text);

            TextView maxText = (TextView) view.findViewById(R.id.max_text);

            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);

            infoText.setText(forecast.more.info);

            maxText.setText(forecast.temperature.max);

            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);

        }

        if (weather.aqi != null) {

            aqiText.setText(weather.aqi.city.aqi);

            pm25Text.setText(weather.aqi.city.pm25);

        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;

        String carWash = "洗车指数：" + weather.suggestion.carWash.info;

        String sport = "运行建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);

        carWashText.setText(carWash);

        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);

       /* Intent intent = new Intent(getActivity(), AutoUpdateService.class);

        getActivity().startService(intent);
*/
    }





}
