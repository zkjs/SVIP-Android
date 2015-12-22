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
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
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
 public class BookingDialog extends Dialog implements View.OnClickListener{

     private DisplayImageOptions options;
     private Activity activity;
     public String shopId,shopName;
     public CustomerServiceBean customerService = null;

     public String recordFileName;// 录音文件名称
     public int 	recordSecond;// 录音总长度

     public BookingDialog(Context context) {
         super(context);
         this.activity = (Activity)context;
         this.options = new DisplayImageOptions.Builder()
                 .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                 .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                 .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                 .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                 .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                 .build();

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
         View view = inflater.inflate(R.layout.dialog_booking, null);
         setContentView(view);
         view.findViewById(R.id.ok_btn).setOnClickListener(this);
         view.findViewById(R.id.choose_btn).setOnClickListener(this);

         TextView name = (TextView)view.findViewById(R.id.server_name_tv);
         CircleImageView icon = (CircleImageView)view.findViewById(R.id.server_icon_iv);
         TextView rating = (TextView)view.findViewById(R.id.server_rating_tv);

         findViewById(R.id.server_chat).setOnClickListener(this);
         findViewById(R.id.server_tel).setOnClickListener(this);

         if(customerService != null){
             if(!TextUtils.isEmpty(customerService.getName())){
                 name.setText(customerService.getName());
             }

             if(!TextUtils.isEmpty(customerService.getSalesid())){
                 ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(customerService.getSalesid()), icon, options);
             }

         }
     }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ok_btn:
                if(customerService != null){
                    intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, customerService.getSalesid());
                    intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                    if(null != customerService){
                        String userName = customerService.getName();
                        if (!TextUtils.isEmpty(userName)) {
                            intent.putExtra(Constants.EXTRA_TO_NAME,userName);
                        }
                    }
                    if (!TextUtils.isEmpty(shopId)) {
                        intent.putExtra(Constants.EXTRA_SHOP_ID,shopId);
                    }
                    if (!TextUtils.isEmpty(shopName)) {
                        intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                    }
                    if(!TextUtils.isEmpty(recordFileName)){
                        intent.putExtra("filePath",recordFileName);
                        intent.putExtra("voiceTime",recordSecond);
                    }
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                cancel();

                break;
            case R.id.choose_btn:
                if(activity instanceof MainActivity){
                    MainActivity mainActivity = (MainActivity)activity;
                    mainActivity.setCurrentItem(1);
                }
                cancel();
                break;
            case R.id.server_chat:
                if (customerService != null && !TextUtils.isEmpty(customerService.getPhone())) {
                    IntentUtil.sendMessage("", customerService.getPhone(), activity);
                }
                break;
            case R.id.server_tel:
                if (customerService != null && !TextUtils.isEmpty(customerService.getPhone())) {
                    IntentUtil.callPhone(activity, customerService.getPhone());
                }
                break;
        }

    }
}
