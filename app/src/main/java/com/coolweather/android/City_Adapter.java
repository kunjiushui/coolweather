package com.coolweather.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.android.db.City;

import java.util.List;

public class City_Adapter extends RecyclerView.Adapter<City_Adapter.ViewHolder> {


    private List<CityInfo> mData;
    Context        mContext;
    LayoutInflater mInflater;


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView city1;
        TextView temp1;
        TextView cond1;
        ImageView imageid1;
        public ViewHolder(View view){
            super(view);
            city1=(TextView)view.findViewById(R.id.item_multi_city);
            temp1=(TextView)view.findViewById(R.id.item_multi_temperature);
            cond1=(TextView)view.findViewById(R.id.item_multi_cond);
            imageid1=(ImageView)view.findViewById(R.id.item_multi_icon);
        }
    }
    public City_Adapter(List<CityInfo> Data){
        mData=Data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            CityInfo city = mData.get(position);
            holder.city1.setText(city.getCity());
            holder.temp1.setText(city.getTemp());
            holder.cond1.setText(city.getCond());
            holder.imageid1.setImageResource(city.getImageId());

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void addItem(int position, CityInfo ddata) {
        mData.add(position,ddata);
        notifyItemInserted(position);//通知演示插入动画
        notifyItemRangeChanged(position,mData.size()-position);//通知数据与界面重新绑定
    }


}

