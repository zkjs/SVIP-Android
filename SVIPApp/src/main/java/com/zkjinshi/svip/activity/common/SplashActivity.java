package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.CacheUtil;

/**
 * 开机启动页面
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_DELAY_MILLIS = 4000;

    private static final String SPLASH_PREF = "splash_pref";

    private RelativeLayout bodyLayout;

    private LinearLayout msgLayout;

    private ImageView logoIv;
    private TextView textTv;

    private static final int GO_LOGIN = 1000;
    private static final int GO_HOME = 1001;
    private Animation skyDropOutAnim,logoFadeInAnim,logoFadeOutAnim, textFadeInAnim, textFadeOutAnim;

    public static final int OPEN_SET_ACTION_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
    }

    private void initView(){
        bodyLayout = (RelativeLayout) findViewById(R.id.splash_body_layout);
        msgLayout = (LinearLayout)findViewById(R.id.splash_msg_layout);
        logoIv = (ImageView)findViewById(R.id.splash_logo_iv);
        textTv = (TextView)findViewById(R.id.splash_ad_tv);
    }

    /**
     * 初始化界面元素
     */
    private void initData() {

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
        textFadeInAnim = AnimationUtils.loadAnimation(this,R.anim.fade_in_text);
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

        SharedPreferences preferences = getSharedPreferences(
                SPLASH_PREF, MODE_PRIVATE);
        // 判断用户是否登录，如果登录则进入主页面
        if (CacheUtil.getInstance().isLogin()) {
            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
            handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            handler.sendEmptyMessageDelayed(GO_LOGIN, SPLASH_DELAY_MILLIS);
        }
    }

    private void goLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    private void goHome() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
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
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
