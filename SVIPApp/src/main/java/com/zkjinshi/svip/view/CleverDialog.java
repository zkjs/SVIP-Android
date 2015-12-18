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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
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



 public class CleverDialog extends Dialog implements View.OnClickListener{

     private DisplayImageOptions options;
     private Activity mActivity;
     public String shopid="120",shopName;
     public boolean isActive = false;
    Animation bigAnimation;
    ImageView pic;



     public CleverDialog(Context context) {
         super(context);
         this.mActivity = (Activity)context;
         this.options = new DisplayImageOptions.Builder()
                 .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                 .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                 .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                 .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                 .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                 .build();

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
         isActive = CacheUtil.getInstance().isActivate();
         if(isActive){
             view = inflater.inflate(R.layout.dialog_clever_active, null);
             setContentView(view);
             findViewById(R.id.clever_active_layout).setOnClickListener(this);
         }else{
             view = inflater.inflate(R.layout.dialog_clever_unactive, null);
             setContentView(view);
             findViewById(R.id.ad_title_tv).setOnClickListener(this);
         }
         pic = (ImageView)findViewById(R.id.ad_pic_iv);
         bigAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_bigger);
         pic.startAnimation(bigAnimation);
     }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.clever_active_layout:
                intent = new Intent(mActivity, OrderBookingActivity.class);
                intent.putExtra("shopid", shopid);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                cancel();
                break;
            case R.id.ad_title_tv:
                intent = new Intent(mActivity, InviteCodeActivity.class);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                cancel();
                break;
        }
    }

    public void cancel(){
        super.cancel();
        bigAnimation.cancel();
        bigAnimation = null;
    }
}
