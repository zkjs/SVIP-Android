package com.zkjinshi.svip.activity.city.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.CityBean;
import com.zkjinshi.svip.base.SvipBaseAdapter;

import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityAdapter extends SvipBaseAdapter<CityBean>{

    private final static int ITEM_TYPE_COUNT = 2;
    private final static int CITY_LOCATED = 0x00;//定位城市显示
    private final static int CITY_NORMAL  = 0x01;//普通城市显示

    public CityAdapter(List<CityBean> datas, Activity activity) {
        super(datas, activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityBean cityBean = mDatas.get(position);
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_city_list, null);
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
        if(mDatas.get(position).isCityLocated()){
            return CITY_LOCATED;
        }else {
            return CITY_NORMAL;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_COUNT;
    }
}
