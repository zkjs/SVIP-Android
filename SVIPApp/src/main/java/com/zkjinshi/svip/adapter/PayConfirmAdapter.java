package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.utils.PayUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayConfirmAdapter extends BaseAdapter {

    public ArrayList<PayRecordDataVo> datalist = new ArrayList<PayRecordDataVo>();
    private Activity activity;

    public  String status = "0";

    public void loadMore(ArrayList<PayRecordDataVo> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<PayRecordDataVo> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public PayConfirmAdapter(ArrayList<PayRecordDataVo> datalist, Activity activity) {
        this.datalist = datalist;
        this.activity = activity;
    }

    //是否已经追加营销消息历史。
    public boolean isAppendBeaconMsg(){
        int size = datalist.size();
        if(size > 0){
            PayRecordDataVo lastVo = datalist.get(size -1);
            if(lastVo.getYunBaMsgVo() != null){
                return true;
            }
        }
        return false;
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
        final PayRecordDataVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_pay_confirm, null);
            holder.hotelNameTv = (TextView)convertView.findViewById(R.id.shopname_tv);
            holder.datetimeTv = (TextView)convertView.findViewById(R.id.datetime_tv);
            holder.priceTv = (TextView)convertView.findViewById(R.id.price_tv);
            holder.clickRlt = (RelativeLayout)convertView.findViewById(R.id.click_rlt);
            convertView.setTag(holder);
        }

        if(itemOrder.getYunBaMsgVo() == null){
            holder.hotelNameTv.setText(itemOrder.getShopname());
            holder.datetimeTv.setText(itemOrder.getCreatetime());
            holder.priceTv.setText("¥ "+ PayUtil.changeMoney(itemOrder.getAmount()));
            holder.sdvImg.setVisibility(View.GONE);
            holder.priceTv.setVisibility(View.VISIBLE);
        }else{
            YunBaMsgVo yunBaMsgVo = itemOrder.getYunBaMsgVo();
            holder.hotelNameTv.setText(yunBaMsgVo.getTitle());
            Date date = new Date(yunBaMsgVo.getInsert_time());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateStr = sdf.format(date);
            holder.datetimeTv.setText(dateStr);
            String imgUrl = ProtocolUtil.getHostImgUrl(yunBaMsgVo.getImg_url());
            holder.sdvImg.setImageURI(Uri.parse(imgUrl));
            holder.sdvImg.setVisibility(View.VISIBLE);
            holder.priceTv.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView hotelNameTv,priceTv,datetimeTv;
        RelativeLayout clickRlt;
        SimpleDraweeView sdvImg;
    }
}
