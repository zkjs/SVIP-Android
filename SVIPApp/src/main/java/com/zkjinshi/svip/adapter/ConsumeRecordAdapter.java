package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.CircleImageView;

import java.text.ParseException;
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

    public ArrayList<OrderConsumeResponse> datalist = new ArrayList<OrderConsumeResponse>();
    private Activity activity;
    private DisplayImageOptions options;

    public void loadMore(ArrayList<OrderConsumeResponse> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<OrderConsumeResponse> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public ConsumeRecordAdapter(ArrayList<OrderConsumeResponse> datalist, Activity activity) {
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
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        OrderConsumeResponse itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_consume_record, null);
            holder.shopIcon     = (CircleImageView) convertView.findViewById(R.id.order_item_cv_hotel_photo);
            holder.hotelNameTv = (TextView)convertView.findViewById(R.id.order_item_tv_hotel_name);
            holder.orderInfoTv = (TextView)convertView.findViewById(R.id.order_item_tv_order_info);
            holder.comingDateTv = (TextView)convertView.findViewById(R.id.order_item_tv_coming_date);
            holder.orderStatusTv = (TextView)convertView.findViewById(R.id.order_item_tv_order_status);
            convertView.setTag(holder);
        }

        //获得shopID网络路径
        if(!TextUtils.isEmpty(itemOrder.getShopid())){
            String logoUrl = Constants.GET_SHOP_LOGO + itemOrder.getShopid() + ".png";
            ImageLoader.getInstance().displayImage(logoUrl, holder.shopIcon, options);
        }
        holder.comingDateTv.setText(itemOrder.getArrival_date());
        SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd");
        try {
            Date date = detailFormat.parse(itemOrder.getArrival_date());
            String simpleDate= simpleFormat.format(date) + "入住";
            String goodstr = itemOrder.getRoom_type()+ " | " + simpleDate+ " | "+"¥ " + itemOrder.getRoom_rate();
            holder.orderInfoTv.setText(goodstr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String shopname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(itemOrder.getShopid());
        holder.hotelNameTv.setText(shopname);

        if(!TextUtils.isEmpty(itemOrder.getStatus())) {
            switch (Integer.valueOf(itemOrder.getStatus())){
                case BookOrder.ORDER_UNCONFIRMED:
                    holder.orderStatusTv.setText(R.string.order_unconfirmed);
                    holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    break;
                case BookOrder.ORDER_CANCELLED:
                    holder.orderStatusTv.setText(R.string.order_cancelled);
                    holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    break;
                case BookOrder.ORDER_CONFIRMED:
                    holder.orderStatusTv.setText(R.string.order_confirmed);
                    holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    break;
                case BookOrder.ORDER_FINISHED:
                    if(itemOrder.getScore().equals("0")){
                        holder.orderStatusTv.setText("待评价");
                        holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    }else {
                        holder.orderStatusTv.setText(R.string.order_finished);
                        holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai);
                    }
                    break;
                case BookOrder.ORDER_USING:
                    holder.orderStatusTv.setText(R.string.order_using);
                    holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    break;
                case BookOrder.ORDER_DELETED:
                    holder.orderStatusTv.setText(R.string.trade_deleted);
                    holder.orderStatusTv.setBackgroundResource(R.mipmap.btn_zhuantai_orange);
                    break;
            }

        }
        return convertView;
    }

    static class ViewHolder{
        CircleImageView shopIcon;
        TextView hotelNameTv,orderInfoTv,comingDateTv,orderStatusTv;

    }
}
