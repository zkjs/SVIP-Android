package com.zkjinshi.ritz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.utils.PayUtil;
import com.zkjinshi.ritz.vo.PayRecordDataVo;
import com.zkjinshi.ritz.vo.PayRecordDataVo;


import java.util.ArrayList;


/**
 * 消费记录适配器
 * 开发者：JimmyZhang
 * 日期：2016/3/29
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ExpenseAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<PayRecordDataVo> expenseList;

    public int firstVisibleItem = 0;
    public int visibleItemCount;
    public int scrollState;
    public boolean isClick = false;
    public int clickIndex = -1;

    private String[] colors = {"#ffbe5c","#ffb74a","#ffaf38","#ffa826","#ffa114","#ff9900","#ff9100","#ff8800","#ff9800"};

    public ExpenseAdapter(Context context, ArrayList<PayRecordDataVo> expenseList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setExpenseList(expenseList);
    }

    public void setExpenseList(ArrayList<PayRecordDataVo> expenseList) {
        if(null == expenseList){
            this.expenseList = new ArrayList<PayRecordDataVo>();
        }else {
            this.expenseList = expenseList;
        }
        notifyDataSetChanged();
    }

    public ArrayList<PayRecordDataVo> getExpenseList() {
        return expenseList;
    }

    public void loadMore(ArrayList<PayRecordDataVo> morelist){
        expenseList.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<PayRecordDataVo> refreshlist){
        expenseList = refreshlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_expense,null);
            viewHolder = new ViewHolder();
            viewHolder.shopNameTv = (TextView)convertView.findViewById(R.id.item_expense_tv_shop_name);
            viewHolder.priceTv = (TextView)convertView.findViewById(R.id.item_expense_tv_price);
            viewHolder.payTimeTv = (TextView)convertView.findViewById(R.id.item_expense_tv_pay_time);
            viewHolder.payNoTv = (TextView)convertView.findViewById(R.id.item_expense_tv_pay_no);
            viewHolder.payeeTv = (TextView)convertView.findViewById(R.id.item_expense_tv_payee);
            viewHolder.introLayout = (RelativeLayout) convertView.findViewById(R.id.item_expense_layout_intro);
            viewHolder.detailLayout = (LinearLayout)convertView.findViewById(R.id.item_expense_layout_detail);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PayRecordDataVo payRecordDataVo = expenseList.get(position);
        String shopName = payRecordDataVo.getShopname();
        if(!TextUtils.isEmpty(shopName)){
            viewHolder.shopNameTv.setText(shopName);
        }
        String payee = payRecordDataVo.getShopname();
        if(!TextUtils.isEmpty(payee)){
            viewHolder.payeeTv.setText(payee);
        }
        String payNo = payRecordDataVo.getPaymentno();
        if(!TextUtils.isEmpty(payNo)){
            viewHolder.payNoTv.setText(payNo);
        }
        String payTime = ""+ payRecordDataVo.getConfirmtime();
        if(!TextUtils.isEmpty(payTime)){
            viewHolder.payTimeTv.setText(payTime);
        }
        String price =  PayUtil.changeMoney(payRecordDataVo.getAmount());
        if(!TextUtils.isEmpty(price)){
            viewHolder.priceTv.setText("¥ " +price);
        }
        if(isClick){
            if(clickIndex != firstVisibleItem &&  position == firstVisibleItem){
                payRecordDataVo.setShow(false);
            }
            else if(clickIndex == position){

            }else{
                payRecordDataVo.setShow(false);
            }
        }else{
//            if(position == firstVisibleItem){
//                payRecordDataVo.setShow(true);
//            }else{
//                payRecordDataVo.setShow(false);
//            }
            //payRecordDataVo.setShow(false);
        }


        if(payRecordDataVo.isShow()){
            viewHolder.detailLayout.setVisibility(View.VISIBLE);
        }else{
            viewHolder.detailLayout.setVisibility(View.GONE);
        }
        int offset = position - firstVisibleItem;
        int colorSize = colors.length;
        if(offset >= 0){
            String colorStr = colors[colorSize -1];
            offset = offset+1;
            if(offset < colorSize){
                colorStr = colors[offset];
            }
            viewHolder.introLayout.setBackgroundColor(Color.parseColor(colorStr));
        }else{
            String colorStr = colors[0];
            viewHolder.introLayout.setBackgroundColor(Color.parseColor(colorStr));
        }

        return  convertView;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount,int scrollState){
        Log.d("test", "firstVisibleItem="+firstVisibleItem+" visibleItemCount="+visibleItemCount+" totalItemCount="+totalItemCount);
        this.visibleItemCount = visibleItemCount;
        this.scrollState = scrollState;
        if(firstVisibleItem != this.firstVisibleItem){
            this.firstVisibleItem = firstVisibleItem;
            notifyDataSetChanged();
        }
        isClick = false;
    }

    class ViewHolder{
        TextView shopNameTv,priceTv,payTimeTv,payNoTv,payeeTv;
        RelativeLayout introLayout;
        LinearLayout detailLayout;
    }

}
