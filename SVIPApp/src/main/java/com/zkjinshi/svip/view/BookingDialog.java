package com.zkjinshi.svip.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.order.ShopActivity;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class BookingDialog extends Dialog implements View.OnClickListener{

     private Activity activity;
     public int pageIndex = 0;

     public BookingDialog(Context context) {
         super(context);
         this.activity = (Activity)context;

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
         LayoutInflater inflater = LayoutInflater.from(activity);
         View view = inflater.inflate(R.layout.dialog_booking, null);
         setContentView(view);
         view.findViewById(R.id.ok_btn).setOnClickListener(this);
         view.findViewById(R.id.choose_btn).setOnClickListener(this);
//
//         Window dialogWindow = getWindow();
//         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//         DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//         lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
//         dialogWindow.setAttributes(lp);
     }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ok_btn:
//                intent = new Intent(activity, ShopActivity.class);
//                activity.startActivity(intent);
//                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.choose_btn:
                intent = new Intent(activity, ShopActivity.class);
                intent.putExtra("pageIndex",pageIndex);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }
        cancel();
    }
}
