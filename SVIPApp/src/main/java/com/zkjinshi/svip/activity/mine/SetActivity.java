package com.zkjinshi.svip.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.bluetooth.IBeaconContext;
import com.zkjinshi.svip.bluetooth.IBeaconController;
import com.zkjinshi.svip.emchat.EasemobIMHelper;

import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.utils.CacheUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SetActivity extends BaseActivity {

    public static final String TAG = SetActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private RelativeLayout aboutLayout,quitLayout;

    private void initView(){
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        aboutLayout = (RelativeLayout)findViewById(R.id.set_layout_about);
        quitLayout = (RelativeLayout)findViewById(R.id.set_layout_quit);
    }

    private void initData(){
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("设置");
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //关于我们
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetActivity.this, WebViewActivity.class);
                intent.putExtra("webview_url","http://zkjinshi.com/about_us/");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //退出
        quitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
        initData();
        initListeners();
    }

    private void showQuitDialog(){
        final CustomDialog.Builder customerBuilder = new CustomDialog.Builder(this);
        customerBuilder.setTitle(getString(R.string.exit));
        customerBuilder.setMessage(getString(R.string.if_exit_the_current_account_or_not));
        customerBuilder.setGravity(Gravity.CENTER);
        customerBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customerBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInst().clearLeaveTop();
                //环信接口退出
                EasemobIMHelper.getInstance().logout();
                //修改登录状态
                CacheUtil.getInstance().setLogin(false);
                CacheUtil.getInstance().setActivate(false);
                CacheUtil.getInstance().setUserId("");
                CacheUtil.getInstance().setUserName("");
                CacheUtil.getInstance().setUserPhone("");
                CacheUtil.getInstance().savePicPath("");
                //移除蓝牙服务
                IBeaconController.getInstance().stopBeaconService();
                IBeaconContext.getInstance().clearIBeaconMap();
                ((SVIPApplication)getApplication()).mRegionList.clear();
                //ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                //停止高德地图定位API
                LocationManager.getInstance().stopLocation();
                Intent intent = new Intent(SetActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        customerBuilder.create().show();
    }


}
