package com.zkjinshi.svip.adapter;

import android.app.Activity;
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
import com.zkjinshi.svip.vo.AreaVo;
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
public class VideoAdapter extends BaseAdapter {

    public ArrayList<AreaVo> datalist = new ArrayList<AreaVo>();
    private Activity activity;
  
    public VideoAdapter(ArrayList<AreaVo> datalist, Activity activity) {
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
        final AreaVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_video, null);
            holder.areaNameTv = (TextView)convertView.findViewById(R.id.area_tv);
            convertView.setTag(holder);
        }

        holder.areaNameTv.setText(itemOrder.getLocdesc());
        return convertView;
    }

    static class ViewHolder{
        TextView areaNameTv;
      
    }
}
