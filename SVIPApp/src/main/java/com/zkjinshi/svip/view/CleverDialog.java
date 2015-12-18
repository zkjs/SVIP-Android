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
import com.zkjinshi.svip.response.PrivilegeResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import me.nereo.multi_image_selector.bean.Image;

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

    private TextView titleTv,shopnameTv,privilegeTv,desTv;
    private ImageView logoIv;

    private PrivilegeListener privilegeListener = null;

    private PrivilegeResponse privilegeResponse = null;

    public interface PrivilegeListener{
        public void callback(PrivilegeResponse privilegeResponse);
    }

    public void setPrivilegeListener(PrivilegeListener getPrivilegeListener) {
        this.privilegeListener = getPrivilegeListener;
    }

    public CleverDialog(Context context) {
         super(context);
         this.mActivity = (Activity)context;
         this.options = new DisplayImageOptions.Builder()
                 .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                 .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                 .build();


     }

    public PrivilegeResponse getPrivilegeResponse() {
        return privilegeResponse;
    }

    public void setPrivilegeResponse(PrivilegeResponse privilegeResponse) {
        this.privilegeResponse = privilegeResponse;
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
         view = inflater.inflate(R.layout.dialog_clever_active, null);
         setContentView(view);
         findViewById(R.id.clever_active_layout).setOnClickListener(this);

         titleTv = (TextView)view.findViewById(R.id.ad_title_tv);
         shopnameTv = (TextView)view.findViewById(R.id.ad_shopname_tv);
         privilegeTv = (TextView)view.findViewById(R.id.privilege_tv);
         desTv = (TextView)view.findViewById(R.id.ad_des_tv);
         logoIv = (ImageView)view.findViewById(R.id.logo_iv);

     }

     public void setContentText(String title,String shopName,String privilege,String des,String logo){
        if(!TextUtils.isEmpty(title)){
            titleTv.setText(title);
        }
        if(!TextUtils.isEmpty(shopName)){
            shopnameTv.setText(shopName);
        }
        if(!TextUtils.isEmpty(privilege)){
            privilegeTv.setText(privilege);
        }
        if(!TextUtils.isEmpty(des)){
            desTv.setText(des);
        }
        if(!TextUtils.isEmpty(logo)){
            ImageLoader.getInstance().displayImage(logo,logoIv,options);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clever_active_layout:
                cancel();
                break;
        }
    }

    public void cancel(){
        super.cancel();
        if(privilegeListener != null){
            privilegeListener.callback(privilegeResponse);
        }
    }

}
