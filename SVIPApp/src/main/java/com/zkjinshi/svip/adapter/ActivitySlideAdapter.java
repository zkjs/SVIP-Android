package com.zkjinshi.svip.adapter;

import android.app.Activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;


import com.zkjinshi.svip.R;


import com.zkjinshi.svip.view.slidelistview.SlideBaseAdapter;
import com.zkjinshi.svip.vo.ActivityVo;

import java.util.ArrayList;

/**
 * 活动行程适配器
 * 开发者：杜健德
 * 日期：2016/6/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ActivitySlideAdapter extends SlideBaseAdapter {

    private ArrayList<ActivityVo> mDatas;
    private Activity mActivity;
    private CallBack callBack = null;

    public  interface CallBack{
        public void delete(ActivityVo itemVo);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void refresh(ArrayList<ActivityVo> datas){
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public ActivitySlideAdapter(Activity activity, ArrayList<ActivityVo> datas) {
        super(activity);

        this.mActivity = activity;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = createConvertView(position);
            holder.shopnameTv          = (TextView) convertView.findViewById(R.id.shopname_tv);
            holder.actnameTv   = (TextView) convertView.findViewById(R.id.actname_tv);
            holder.timeTv  = (TextView) convertView.findViewById(R.id.time_tv);
            holder.statusTv   = (TextView) convertView.findViewById(R.id.status_tv);
            holder.deleteBtn = (Button)convertView.findViewById(R.id.delete_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ActivityVo itemdata = mDatas.get(position);
        holder.shopnameTv.setText(itemdata.getShopname());
        holder.actnameTv.setText(itemdata.getActname());
        holder.timeTv.setText(itemdata.getStartdate());
        if("已取消".equals(itemdata.getActstatus())){
            holder.statusTv.setVisibility(View.VISIBLE);
        }else{
            holder.statusTv.setVisibility(View.GONE);
        }

        if(holder.deleteBtn != null){
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.delete(itemdata);
                }
            });
        }


        return convertView;
    }

    @Override
    public int getFrontViewId(int position) {
        return R.layout.item_activity;
    }

    @Override
    public int getLeftBackViewId(int position) {
        return 0;
    }

    @Override
    public int getRightBackViewId(int position) {

        ActivityVo itemdata = mDatas.get(position);
        if("已取消".equals(itemdata.getActstatus())){
            return 0;
        }else{
            return R.layout.row_right_view;
        }

    }

    public static class ViewHolder{

        public TextView         shopnameTv;
        public TextView         actnameTv;
        public TextView         timeTv;
        public TextView         statusTv;
        public Button deleteBtn;
    }

}
