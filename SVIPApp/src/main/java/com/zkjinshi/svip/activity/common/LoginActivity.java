package com.zkjinshi.svip.activity.common;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.config.ConfigActivity;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.SensorManagerHelper;
import com.zkjinshi.svip.utils.StringUtil;


import org.json.JSONObject;



/**
 * 开发者：dujiande
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends BaseActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;

    private EditText mInputPhone;
    private TextView registerTv;
    private RelativeLayout contentRlt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        mInputPhone     = (EditText)    findViewById(R.id.inputEt);
        registerTv      = (TextView)    findViewById(R.id.register_tv);
        registerTv.setVisibility(View.GONE);
        registerTv.setText("立即申请");
        contentRlt = (RelativeLayout)findViewById(R.id.content_rlt);
    }

    private void initData() {

    }

    private void moveUp(){
        ViewHelper.setTranslationY(contentRlt,0);
        int offsetY = DisplayUtil.dip2px(this,100);
        long time = 300;
        ViewPropertyAnimator.animate(contentRlt).translationYBy(-offsetY).setDuration(time);
    }

    private void moveDown(){
        int offsetY = DisplayUtil.dip2px(this,100);
        long time = 300;
        ViewHelper.setTranslationY(contentRlt,-offsetY);
        ViewPropertyAnimator.animate(contentRlt).translationYBy(offsetY).setDuration(time);
    }

    private void initListener() {
        //添加layout大小发生改变监听器
        findViewById(R.id.scrollView).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > 0)){
                    //Toast.makeText(mContext, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
                    moveUp();
                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > 0)){
                    //Toast.makeText(mContext, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
                    moveDown();
                }
            }
        });

        //打开配置项
        SensorManagerHelper sensorHelper = new SensorManagerHelper(this);
        sensorHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {

            @Override
            public void onShake() {
                Intent intent = new Intent(LoginActivity.this, ConfigActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
        });

        //注册
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenum = mInputPhone.getText().toString();

//               if(StringUtil.isEmpty(phonenum)){
//                   Toast.makeText(mContext,"手机号码不能为空。",Toast.LENGTH_SHORT).show();
//                   return;
//               }
//                CacheUtil.getInstance().setUserPhone(phonenum);
//                LoginController.getInstance().sendVerifyCodeForRegister(mContext, phonenum, new LoginController.CallBackListener() {
//                    @Override
//                    public void successCallback(JSONObject response) {
//                        Intent intent = new Intent(mContext,CheckActivity.class);
//                        intent.putExtra("isLogin",false);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        finish();
//                    }
//                });


            }
        });

        //登录
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenum = mInputPhone.getText().toString();
                if(StringUtil.isEmpty(phonenum)){
                    Toast.makeText(mContext,"手机号码不能为空。",Toast.LENGTH_SHORT).show();
                    return;
                }
                CacheUtil.getInstance().setUserPhone(phonenum);
                LoginController.getInstance().sendVerifyCodeForLogin(mContext,registerTv, phonenum, new LoginController.CallBackListener() {
                    @Override
                    public void successCallback(JSONObject response) {
                        Intent intent = new Intent(mContext,CheckActivity.class);
                        intent.putExtra("isLogin",true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                });
            }
        });
    }

}