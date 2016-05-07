package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.vo.CommentVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentAdapter extends BaseAdapter {

    public ArrayList<CommentVo> datalist = new ArrayList<CommentVo>();
    private Activity activity;

    public CommentAdapter(ArrayList<CommentVo> datalist, Activity activity) {
        this.datalist = datalist;
        this.activity = activity;
    }

    public void refresh(ArrayList<CommentVo> datalist){
        this.datalist = datalist;
        notifyDataSetChanged();
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
        final CommentVo itemOrder = datalist.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(activity, R.layout.item_comment, null);
            holder.nameameTv = (TextView)convertView.findViewById(R.id.name_tv);
            holder.avatarSdv = (SimpleDraweeView)convertView.findViewById(R.id.avatar_sdv);
            holder.dateTv = (TextView)convertView.findViewById(R.id.datetime_tv);
            holder.commentTv = (TextView)convertView.findViewById(R.id.comment_tv);
            convertView.setTag(holder);
        }

        holder.nameameTv.setText(itemOrder.getName());
        holder.avatarSdv.setImageURI(Uri.parse(itemOrder.getAvatarUrl()));
        Date date = new Date(itemOrder.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = sdf.format(date);
        holder.dateTv.setText(dateStr);
        holder.commentTv.setText(itemOrder.getComment());
        return convertView;
    }

    static class ViewHolder{
        TextView nameameTv,dateTv,commentTv;
        SimpleDraweeView avatarSdv;
    }
}
