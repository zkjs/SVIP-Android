package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.PayUtil;
import com.zkjinshi.svip.vo.ServiceTaskDataVo;


import java.util.ArrayList;



/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallOrderAdapter extends BaseAdapter {

    public ArrayList<ServiceTaskDataVo> datalist = new ArrayList<ServiceTaskDataVo>();
    private Activity activity;

    public void loadMore(ArrayList<ServiceTaskDataVo> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<ServiceTaskDataVo> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public CallOrderAdapter(ArrayList<ServiceTaskDataVo> datalist, Activity activity) {
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
        final ServiceTaskDataVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_call_order, null);
            holder.serviceNameTv = (TextView)convertView.findViewById(R.id.service_tv);
            holder.datetimeTv = (TextView)convertView.findViewById(R.id.datetime_tv);
            holder.statusTv = (TextView)convertView.findViewById(R.id.status_tv);

            convertView.setTag(holder);
        }

        holder.serviceNameTv.setText(itemOrder.getSrvname());
        holder.datetimeTv.setText(itemOrder.getCreatetime());
        holder.statusTv.setText(itemOrder.getStatus());
        return convertView;
    }

    static class ViewHolder{
        TextView serviceNameTv,statusTv,datetimeTv;
    }
}
