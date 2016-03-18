package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zkjinshi.svip.R;

import com.zkjinshi.svip.vo.PayRecordDataVo;


import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayRecordAdapter extends BaseAdapter {

    public ArrayList<PayRecordDataVo> datalist = new ArrayList<PayRecordDataVo>();
    private Activity activity;

    public void loadMore(ArrayList<PayRecordDataVo> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<PayRecordDataVo> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public PayRecordAdapter(ArrayList<PayRecordDataVo> datalist, Activity activity) {
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
        PayRecordDataVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_pay_confirm, null);
            holder.hotelNameTv = (TextView)convertView.findViewById(R.id.shopname_tv);
            holder.datetimeTv = (TextView)convertView.findViewById(R.id.datetime_tv);
            holder.priceTv = (TextView)convertView.findViewById(R.id.price_tv);

            convertView.setTag(holder);
        }
        holder.hotelNameTv.setText(itemOrder.getShopname());
        holder.datetimeTv.setText(itemOrder.getCreatetime());
        holder.priceTv.setText("¥ "+itemOrder.getAmount());

        return convertView;
    }

    static class ViewHolder{
        TextView hotelNameTv,priceTv,datetimeTv;
    }
}
