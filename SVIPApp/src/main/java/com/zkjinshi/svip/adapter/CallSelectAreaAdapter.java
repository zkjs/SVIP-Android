package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.vo.ZoneVo;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallSelectAreaAdapter extends BaseAdapter {

    public ArrayList<ZoneVo> datalist = new ArrayList<ZoneVo>();
    private Activity activity;



    public void loadMore(ArrayList<ZoneVo> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<ZoneVo> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public CallSelectAreaAdapter(ArrayList<ZoneVo> datalist, Activity activity) {
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
        final ZoneVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_call_more, null);
            holder.areaTv = (TextView)convertView.findViewById(R.id.area_tv);

            convertView.setTag(holder);
        }

        holder.areaTv.setText(itemOrder.getArea());
        return convertView;
    }

    static class ViewHolder{
        TextView areaTv;
    }
}
