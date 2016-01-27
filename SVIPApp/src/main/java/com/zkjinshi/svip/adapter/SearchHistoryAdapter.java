package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.svip.R;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SearchHistoryAdapter extends BaseAdapter{

    private final static String TAG = SearchHistoryAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    public ArrayList<String> dataList = new ArrayList<String>();
  

    public SearchHistoryAdapter(Context context, ArrayList<String> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
  
    }


    @Override
    public int getCount() {
        return dataList.size();
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
            convertView = mInflater.inflate(R.layout.item_search_list, null);
            holder = new ViewHolder();
            holder.key = (TextView)convertView.findViewById(R.id.tv_history_name);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.key.setText(dataList.get(position));
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
        public TextView  key;
    }


}
