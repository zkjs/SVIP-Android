package com.zkjinshi.ritz.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.ritz.R;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
 public class PayDialog extends Dialog{


     private Activity activity;
     public boolean isSuccess = true;

    public ImageView logoIv;
    public TextView textTv;
    public Button lookBtn;

     public PayDialog(Context context, boolean isSuccess) {
         super(context);
         this.activity = (Activity)context;
         this.isSuccess = isSuccess;
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setCanceledOnTouchOutside(true);
         getWindow().setGravity(Gravity.CENTER);
         init();
     }

     public void init() {
         LayoutInflater inflater = LayoutInflater.from(activity);
         View view = inflater.inflate(R.layout.dialog_pay, null);
         setContentView(view);

         logoIv = (ImageView) view.findViewById(R.id.logo_iv);
         textTv = (TextView)view.findViewById(R.id.textTv);
         lookBtn = (Button)view.findViewById(R.id.btn_look);

         if(isSuccess){
             textTv.setText("支付成功");
             lookBtn.setText("确认");
             logoIv.setImageResource(R.mipmap.ic_zhifuchenggong);
         }else{
             textTv.setText("支付失败");
             lookBtn.setText("重新支付");
             logoIv.setImageResource(R.mipmap.ic_cuowu);
         }

     }

}
