package com.zkjinshi.svip.activity.city.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.CityBean;

import java.util.ArrayList;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityAdapter extends BaseAdapter{

    private final static int ITEM_TYPE_COUNT = 2;
    private final static int CITY_LOCATED = 0x00;//定位城市显示
    private final static int CITY_NORMAL  = 0x01;//普通城市显示

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CityBean> cityList;

    public void setCityList(ArrayList<CityBean> cityList) {
        if(null == cityList){
            this.cityList = new ArrayList<CityBean>();
        }else {
            this.cityList = cityList;
        }
        notifyDataSetChanged();
    }

    public CityAdapter(Context context,ArrayList<CityBean> cityList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setCityList(cityList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityBean cityBean = cityList.get(position);
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_city_list, null);
            holder.tvCityName    = (TextView) convertView.findViewById(R.id.tv_city_name);
            holder.ivDingWei     = (ImageView) convertView.findViewById(R.id.iv_dingwei);
            holder.tvCityLocated = (TextView) convertView.findViewById(R.id.tv_city_located);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(CITY_LOCATED == getItemViewType(position)){
            String cityLocated = cityBean.getCity();
            if(!TextUtils.isEmpty(cityLocated)){
                holder.tvCityLocated.setText(cityLocated);
                holder.ivDingWei.setVisibility(View.VISIBLE);
            }
        }else {
            String cityName = cityBean.getCity();
            if(!TextUtils.isEmpty(cityName)){
                holder.tvCityName.setText(cityName);
            }
        }
        return convertView;
    }

    static class ViewHolder{
        TextView  tvCityName;
        ImageView ivDingWei;
        TextView  tvCityLocated;
    }

    @Override
    public int getItemViewType(int position) {
        if(null != cityList && !cityList.isEmpty() && cityList.get(position).isCityLocated()){
            return CITY_LOCATED;
        }else {
            return CITY_NORMAL;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
