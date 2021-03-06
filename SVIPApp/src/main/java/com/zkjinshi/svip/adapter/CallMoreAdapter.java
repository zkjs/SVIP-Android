package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.vo.ServiceTagTopVo;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallMoreAdapter extends BaseAdapter {

    public ArrayList<ServiceTagTopVo> datalist = new ArrayList<ServiceTagTopVo>();
    private Activity activity;

    public void loadMore(ArrayList<ServiceTagTopVo> morelist){
        datalist.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<ServiceTagTopVo> refreshlist){
        datalist = refreshlist;
        notifyDataSetChanged();
    }

    public CallMoreAdapter(ArrayList<ServiceTagTopVo> datalist, Activity activity) {
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
        final ServiceTagTopVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_call_more, null);
            holder.areaTv = (TextView)convertView.findViewById(R.id.area_tv);

            convertView.setTag(holder);
        }

        holder.areaTv.setText(itemOrder.getLocdesc());
        return convertView;
    }

    static class ViewHolder{
        TextView areaTv;
    }
}
