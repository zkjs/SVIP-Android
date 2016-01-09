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
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.adapter.PrivilegeAdapter;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.response.PrivilegeResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;

import me.nereo.multi_image_selector.bean.Image;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class CleverDialog extends Dialog implements View.OnClickListener{


     private Activity mActivity;
     public String shopid="120",shopName;

    private TextView titleTv,shopnameTv;
    private ListView listView;
    PrivilegeAdapter privilegeAdapter;


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


     }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += DisplayUtil.dip2px(mActivity,listItem.getMeasuredHeight())+listView.getDividerHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight; listView.setLayoutParams(params);
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
         listView = (ListView)view.findViewById(R.id.listview);

     }

     public void setContentText(String title,String shopName,ArrayList<PrivilegeResponse> list){
        if(!TextUtils.isEmpty(title)){
            titleTv.setText(title);
        }
        if(!TextUtils.isEmpty(shopName)){
            shopnameTv.setText(shopName);
        }
         privilegeAdapter = new PrivilegeAdapter(list,mActivity);
         listView.setAdapter(privilegeAdapter);
         setListViewHeightBasedOnChildren(listView);
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
