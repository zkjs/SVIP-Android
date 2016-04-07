package com.zkjinshi.svip.activity.common;
;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.StringUtil;


import org.json.JSONObject;


;

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
    }

    private void initData() {

    }

    private void initListener() {

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
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("webview_url","http://zkjinshi.com/about_us/use_agree.html");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom,
                        R.anim.slide_out_top);

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