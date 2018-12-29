package com.coolweather.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.coolweather.android.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SecondFragment extends Fragment {


    private View view;

    private ImageView bingPicImg2;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View view = inflater.inflate(R.layout.second_fragment, container, false);
        bingPicImg2 = (ImageView) view.findViewById(R.id.bing_pic_img2);
        loadBingPic();
        return view;



    }
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

    }
}
