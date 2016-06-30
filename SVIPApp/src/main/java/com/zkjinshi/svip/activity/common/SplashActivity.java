package com.zkjinshi.svip.activity.common;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.manager.SSOManager;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;


import org.json.JSONObject;

/**
 * 开机启动页面
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends BaseActivity {

    private static final int SPLASH_DELAY_MILLIS = 4000;
    public static YunBaMsgVo beaconMsg = null;
    public static InvitationVo invitationMsg = null;

    private RelativeLayout bodyLayout;

    private ImageView logoIv;
    private TextView textTv;

    private static final int GO_LOGIN = 1000;
    private static final int GO_HOME = 1001;
    private static final int GO_GUIDE = 1002;
    private Animation skyDropOutAnim,logoFadeInAnim,logoFadeOutAnim, textFadeInAnim, textFadeOutAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getSerializableExtra("data") != null){
            beaconMsg = (YunBaMsgVo)getIntent().getSerializableExtra("data");
        }

        if(getIntent().getSerializableExtra("invitation_msg") != null){
            invitationMsg = (InvitationVo)getIntent().getSerializableExtra("invitation_msg");
        }

        initView();
        initData();
    }

    private void initView(){
        bodyLayout = (RelativeLayout) findViewById(R.id.splash_body_layout);
        logoIv = (ImageView)findViewById(R.id.splash_logo_iv);
        textTv = (TextView)findViewById(R.id.splash_ad_tv);
    }

    /**
     * 初始化界面元素
     */
    private void initData() {

        if(CacheUtil.getInstance().isFirstTime()){
            YunBaSubscribeManager.getInstance().setAlias(this,"");
            CacheUtil.getInstance().setFirstTime(false);
        }

        //背景星空下移动画
        skyDropOutAnim = AnimationUtils.loadAnimation(this, R.anim.translate_drop_out);
        bodyLayout.startAnimation(skyDropOutAnim);

        //logo淡入效果
        logoFadeInAnim = AnimationUtils.loadAnimation(this,R.anim.fade_in_logo);
        logoIv.startAnimation(logoFadeInAnim);
        logoFadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoFadeOutAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out_logo);
                logoIv.startAnimation(logoFadeOutAnim);
                logoFadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoIv.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        //logo淡入效果
        textFadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_text);
        textTv.startAnimation(textFadeInAnim);
        textFadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textFadeOutAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out_text);
                textTv.startAnimation(textFadeOutAnim);
                textFadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textTv.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //判断当前网络状态
        if(NetWorkUtil.isNetworkConnected(this)){
            // 判断用户是否登录，如果登录则进入主页面
            if (CacheUtil.getInstance().isLogin()) {
                // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
                handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            } else if(CacheUtil.getInstance().isGuide()) {
                handler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
            }else{
                handler.sendEmptyMessageDelayed(GO_LOGIN, SPLASH_DELAY_MILLIS);
            }
        }else{
            showNetDialog();
        }
    }

    private void goLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    private void goHome() {
        SSOManager.getInstance().refreshToken(this, new SSOManager.SSOCallBack() {
            @Override
            public void onNetworkResponseSucceed() {

                LoginController.getInstance().getUserInfo(SplashActivity.this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
                    @Override
                    public void successCallback(JSONObject response) {
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                    }
                });
            }
        });
    }

    private void goGuide() {
        Intent guideIntent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(guideIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_LOGIN:
                   goLogin();
                    break;
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };



    private void showNetDialog(){

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("网络状态不好，稍后再试?");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        customBuilder.create().show();

    }

}
