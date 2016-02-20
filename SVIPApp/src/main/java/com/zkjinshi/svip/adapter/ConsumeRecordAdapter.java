package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;



import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.OrderForDisplay;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ConsumeRecordAdapter extends BaseAdapter {

    public ArrayList<OrderForDisplay> datalist = new ArrayList<OrderForDisplay>();
    private Activity activity;

    public void loadMore(ArrayList<OrderForDisplay> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<OrderForDisplay> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public ConsumeRecordAdapter(ArrayList<OrderForDisplay> datalist, Activity activity) {
        this.datalist = datalist;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        OrderForDisplay itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_consume_record, null);
            holder.shopIcon     = (SimpleDraweeView) convertView.findViewById(R.id.order_item_cv_hotel_photo);
            holder.hotelNameTv = (TextView)convertView.findViewById(R.id.order_item_tv_hotel_name);
            holder.orderInfoTv = (TextView)convertView.findViewById(R.id.order_item_tv_order_info);
            holder.comingDateTv = (TextView)convertView.findViewById(R.id.order_item_tv_coming_date);
            holder.orderStatusTv = (TextView)convertView.findViewById(R.id.order_item_tv_order_status);
            convertView.setTag(holder);
        }

        //获得shopID网络路径
        if(!TextUtils.isEmpty(itemOrder.getShoplogo())){
            holder.shopIcon.setImageURI(Uri.parse(ProtocolUtil.getHostImgUrl(itemOrder.getShoplogo())));
        }

        SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd");
        try {
            Date arriveDate = new Date(itemOrder.getCreated());
            holder.comingDateTv.setText(detailFormat.format(arriveDate));
            Date date =  new Date(itemOrder.getArrivaldate());
            String simpleDate= simpleFormat.format(date) + "到店";
            String roomTypeStr = "";
            if(!TextUtils.isEmpty(itemOrder.getRoomtype())){
                roomTypeStr = itemOrder.getRoomtype()+ " | ";
            }
            String priceStr = " | 待定价";
            if(itemOrder.getRoomprice() != null){
                priceStr =  " | "+"¥ " + itemOrder.getRoomprice();
            }
            String goodstr = roomTypeStr + simpleDate + priceStr;
            holder.orderInfoTv.setText(goodstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String shopname = itemOrder.getShopname();
        holder.hotelNameTv.setText(shopname);

        holder.orderStatusTv.setText(itemOrder.getOrderstatus());
        holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
        return convertView;
    }

    static class ViewHolder{
        SimpleDraweeView shopIcon;
        TextView hotelNameTv,orderInfoTv,comingDateTv,orderStatusTv;

    }
}
