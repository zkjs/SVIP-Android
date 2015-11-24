package com.zkjinshi.svip.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.activity.order.ShopActivity;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class ListenerDialog extends Dialog {


     private Activity activity;
     CircleLoadingView circleLoadingView;
     public ListenerDialog(Context context) {
         super(context,R.style.dialog_no_title);
         this.activity = (Activity)context;

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
         View view = inflater.inflate(R.layout.dialog_listener, null);
         setContentView(view);

         circleLoadingView = (CircleLoadingView)findViewById(R.id.circleLoadingView);
     }

    public void stopAnimation(){
        circleLoadingView.destroy();
    }
}
