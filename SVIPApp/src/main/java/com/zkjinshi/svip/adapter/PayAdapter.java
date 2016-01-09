package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.PayBean;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayAdapter extends BaseAdapter{

    private final static String TAG = BaseAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<PayBean> payList = new ArrayList<PayBean>();
    private int checkid = 0;

    public PayAdapter(Context context, ArrayList<PayBean> payList, int checkid) {
        this.mInflater = LayoutInflater.from(context);
        this.payList = payList;
        this.checkid = checkid;
    }

    public PayBean gePayByPosition(int position){
        return payList.get(position);
    }

    public void setCheckidByPosition(int position) {
        this.checkid = payList.get(position).getPay_id();
    }

    private boolean ischecked(int id){
        return this.checkid == id ? true : false;
    }

    @Override
    public int getCount() {
        return payList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pay, null);
            holder = new ViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.iv_img);
            holder.name = (TextView)convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.name.setText(payList.get(position).getPay_name());

        int id = payList.get(position).getPay_id();
        boolean c = ischecked(id);
        if(c){
            holder.img.setImageResource(R.mipmap.ic_xuankuang_pre);
        }else{
            holder.img.setImageResource(R.mipmap.ic_xuankuang_nor);
        }
        return convertView;
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }


    /*存放控件*/
    public final class ViewHolder{
        public ImageView img;
        public TextView  name;
    }


}
