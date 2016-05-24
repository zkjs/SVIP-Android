package com.zkjinshi.svip.adapter;

import android.app.Activity;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.MyShopVo;

import java.util.ArrayList;



/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MyShopAdapter extends BaseAdapter {

    public ArrayList<MyShopVo> datalist = new ArrayList<MyShopVo>();
    private Activity activity;

    public void refresh(ArrayList<MyShopVo> datalist){
        this.datalist = datalist;
        notifyDataSetChanged();
    }



    public MyShopAdapter(ArrayList<MyShopVo> datalist, Activity activity) {
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
        final MyShopVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_my_shop, null);
            holder.hotelNameTv = (TextView)convertView.findViewById(R.id.shopname_tv);
            holder.sdvImg = (SimpleDraweeView)convertView.findViewById(R.id.civ_shop_logo);
            convertView.setTag(holder);
        }

        if(itemOrder != null){
            holder.hotelNameTv.setText(itemOrder.getShopname());
            String logoUrl = ProtocolUtil.getHostImgUrl(itemOrder.getShoplogo());
            holder.sdvImg.setImageURI(Uri.parse(logoUrl));
        }
        return convertView;
    }

    static class ViewHolder{
        TextView hotelNameTv;
        SimpleDraweeView sdvImg;
    }
}
