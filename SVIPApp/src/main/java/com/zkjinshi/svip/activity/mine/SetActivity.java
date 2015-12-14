package com.zkjinshi.svip.activity.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;

import java.util.Set;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SetActivity extends Activity {

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
                //环信接口退出
                EasemobIMHelper.getInstance().logout();
                //http接口推出
                String userID = CacheUtil.getInstance().getUserId();
                logoutHttp(userID);
                //熊推接口推出
                WebSocketManager.getInstance().logoutIM(VIPContext.getInstance().getContext());
                //修改登录状态
                CacheUtil.getInstance().setLogin(false);
                CacheUtil.getInstance().savePicPath("");
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                Intent loginActiviy = new Intent(SetActivity.this, LoginActivity.class);
                startActivity(loginActiviy);
                finish();

            }
        });
        customerBuilder.create().show();
    }

    /**
     * 断开用户登录连接
     */
    private void logoutHttp(String userID) {
        String logoutUrl = ProtocolUtil.getLogoutUrl(userID);
        Log.i(TAG, logoutUrl);
        NetRequest netRequest = new NetRequest(logoutUrl);
        NetRequestTask netRequestTask = new NetRequestTask(SetActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(SetActivity.this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出失败");
            }

            @Override
            public void onNetworkRequestCancelled() {
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出成功");
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}