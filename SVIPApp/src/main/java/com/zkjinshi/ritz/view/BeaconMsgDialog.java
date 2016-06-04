package com.zkjinshi.ritz.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zkjinshi.ritz.R;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class BeaconMsgDialog extends Dialog {


    private Activity mActivity;
    private TextView titleTv,contentTv;

    public BeaconMsgDialog(Context context) {
         super(context);
         this.mActivity = (Activity)context;
     }

    @Override
     protected void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setCanceledOnTouchOutside(true);
         getWindow().setGravity(Gravity.CENTER);
         init();
     }

     public void init() {
         LayoutInflater inflater = LayoutInflater.from(mActivity);
         View view;
         view = inflater.inflate(R.layout.dialog_beacon_msg, null);
         setContentView(view);

         titleTv = (TextView)view.findViewById(R.id.ad_title_tv);
         contentTv = (TextView)view.findViewById(R.id.ad_des_tv);

     }

     public void setContentText(String title,String content){
        if(!TextUtils.isEmpty(title)){
            titleTv.setText(title);
        }
        if(!TextUtils.isEmpty(content)){
            contentTv.setText(content);
        }

    }
    public void cancel(){
        super.cancel();
    }

}
