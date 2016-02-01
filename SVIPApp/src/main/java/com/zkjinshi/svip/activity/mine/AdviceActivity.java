package com.zkjinshi.svip.activity.mine;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;

import com.google.gson.Gson;

import com.zkjinshi.base.util.DialogUtil;

import com.zkjinshi.svip.R;

import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.bean.BaseBean;

import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.HashMap;

/**
 * 开发者：杜健德
 * 日期：2016/01/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AdviceActivity extends BaseActivity {

    public static final String TAG = AdviceActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private EditText inputEt;

    private void initView(){
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        inputEt = (EditText)findViewById(R.id.input_et);
    }

    private void initData(){
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("意见反馈");
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //提交按钮
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputEt.getText().toString();
                if(TextUtils.isEmpty(text)){
                    DialogUtil.getInstance().showToast(AdviceActivity.this,"意见不能为空。");
                    return;
                }
                submitAdvice(text);
            }
        });
    }

    //调用API反馈意见
    private void submitAdvice(String text) {
        PackageManager manager = getPackageManager();
        String appVersion = "";
        int currentVersionCode = 0;
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            appVersion = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String url = ProtocolUtil.feedbackAdd();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("app_version", currentVersionCode +"");
        bizMap.put("content", text);
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(AdviceActivity.this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(AdviceActivity.this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(AdviceActivity.this,"反馈意见提交失败。");
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if(baseBean != null && baseBean.isSet()){
                        DialogUtil.getInstance().showToast(AdviceActivity.this,"反馈意见提交成功。");
                        finish();
                    }else{
                        DialogUtil.getInstance().showToast(AdviceActivity.this,"反馈意见提交失败。");
                    }
                } catch (Exception e) {
                    DialogUtil.getInstance().showToast(AdviceActivity.this,"反馈意见提交失败。");
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        initView();
        initData();
        initListeners();
    }

}
