package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.view.CircleImageView;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ConsumeRecordAdapter extends BaseAdapter {

    public ArrayList<BookOrder> datalist = new ArrayList<BookOrder>();
    private Activity activity;
    private DisplayImageOptions options;

    public void loadMore(ArrayList<BookOrder> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<BookOrder> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public ConsumeRecordAdapter(ArrayList<BookOrder> datalist, Activity activity) {
        this.datalist = datalist;
        this.activity = activity;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_hotel_anli03)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_hotel_anli03)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_hotel_anli03)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        BookOrder itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_consume_record, null);
            holder.shopIcon     = (CircleImageView) convertView.findViewById(R.id.civ_shop_icon);
            holder.upLine  =  convertView.findViewById(R.id.line_up);
            holder.downLine =  convertView.findViewById(R.id.line_down);
            holder.shopName  = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.good     = (TextView) convertView.findViewById(R.id.tv_good);
            holder.price   = (TextView) convertView.findViewById(R.id.tv_price);
            holder.orderData       = (TextView)convertView.findViewById(R.id.tv_time);
            holder.unGrade     = (TextView)convertView.findViewById(R.id.tv_ungrade);
            holder.gradeLayout = (LinearLayout)convertView.findViewById(R.id.layout_grade);
            holder.starNum = (TextView)convertView.findViewById(R.id.tv_grade_value);
            convertView.setTag(holder);
        }

        if(position == 0){
            holder.upLine.setVisibility(View.INVISIBLE);
        }else{
            holder.upLine.setVisibility(View.VISIBLE);
        }
        if(position == datalist.size() -1){
            holder.downLine.setVisibility(View.INVISIBLE);
        }else{
            holder.downLine.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder{
        CircleImageView shopIcon;
        View            upLine;
        View            downLine;
        TextView        shopName;
        TextView        price;
        TextView        orderData;
        TextView        good;
        TextView        unGrade;
        LinearLayout    gradeLayout;
        TextView        starNum;
    }
}
