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

        //获得shopID网络路径
        if(!TextUtils.isEmpty(itemOrder.getShopid())){
            String logoUrl = Constants.GET_SHOP_LOGO + itemOrder.getShopid() + ".png";
            ImageLoader.getInstance().displayImage(logoUrl, holder.shopIcon, options);
        }
//        SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            //转换成指定之间格式
//            Date date = detailFormat.parse(itemOrder.getArrival_date());
//            String simpleDate= simpleFormat.format(date);
//            holder.orderData.setText(simpleDate + "");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        holder.orderData.setText(itemOrder.getArrival_date());
        String goodstr = itemOrder.getRoom_type()+"×"+itemOrder.getRooms();
        holder.good.setText(goodstr);
        String shopname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(itemOrder.getShopid());
        holder.shopName.setText(shopname);
        holder.price.setText("￥" + itemOrder.getRoom_rate()+"元");

        if(itemOrder.getScore().equals("0")){
            holder.gradeLayout.setVisibility(View.GONE);
            holder.unGrade.setVisibility(View.VISIBLE);
        }else{
            holder.gradeLayout.setVisibility(View.VISIBLE);
            holder.unGrade.setVisibility(View.GONE);
            holder.starNum.setText(itemOrder.getScore());
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
