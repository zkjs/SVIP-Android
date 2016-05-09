package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.WaiterVo;

import java.util.ArrayList;

/**
 * 服务员列表适配器
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WaiterListAdapter extends BaseAdapter {

    private ArrayList<WaiterVo> waiterList;
    private Context context;
    private LayoutInflater inflater;

    public WaiterListAdapter(Context context,ArrayList<WaiterVo> waiterList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setWaiterList(waiterList);
    }

    public void setWaiterList(ArrayList<WaiterVo> waiterList) {
        if(null == waiterList){
            this.waiterList = new ArrayList<WaiterVo>();
        }else {
            this.waiterList = waiterList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return waiterList.size();
    }

    @Override
    public Object getItem(int position) {
        return waiterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_tips_waiter,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.waiterNameTv = (TextView)convertView.findViewById(R.id.tv_waiter_name);
            viewHolder.waiterPhotoDv = (SimpleDraweeView)convertView.findViewById(R.id.iv_waiter_photo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WaiterVo waiterVo = waiterList.get(position);
        if(null != waiterVo){
            String waiterName = waiterVo.getUsername();
            if(!TextUtils.isEmpty(waiterName)){
                viewHolder.waiterNameTv.setText(waiterName);
            }
            String waiterPhoto = waiterVo.getUserimage();
            if(!TextUtils.isEmpty(waiterPhoto)){
                Uri photoUri = Uri.parse(ProtocolUtil.getImageUrlByWidth(context,waiterPhoto,150));
                if(null != photoUri){
                    viewHolder.waiterPhotoDv.setImageURI(photoUri);
                }
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView waiterNameTv;
        SimpleDraweeView waiterPhotoDv;
    }

}
