package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.apache.log4j.chainsaw.Main;

/**
 * 开机启动页面
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends BaseActivity {

    private DisplayImageOptions options;

    private static final int SPLASH_DELAY_MILLIS = 4000;

    private static final String SPLASH_PREF = "splash_pref";

    private RelativeLayout bodyLayout;

    private LinearLayout msgLayout;

    private ImageView logoIv;
    private TextView textTv;

    private static final int GO_LOGIN = 1000;
    private static final int GO_HOME = 1001;
    private static final int GO_HOME_NO_lOGIN = 1002;
    private static final int GO_GUIDE = 1003;
    private Animation skyDropOutAnim,logoFadeInAnim,logoFadeOutAnim, textFadeInAnim, textFadeOutAnim;

    public static final int OPEN_SET_ACTION_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //如果不需要密码引导页
        if(!Constants.NEED_PASSWORD_GUIDE){
            //设置已经看过了密码引导页，这样密码页就不会出现。
            CacheUtil.getInstance().setGuide(true);
        }
        initBestFitPixel();
        initView();
        initData();
    }

    //初始化最适合的图片分辨率
    private void initBestFitPixel() {
        int myPixel = DisplayUtil.getWidthPixel(this);
        int apiSupportPixels[] = {480,720,1080,1920};
        int bestFitPixel = 720;
        int offset = Math.abs(bestFitPixel - myPixel);
        for(int i=0;i<apiSupportPixels.length;i++){
            int temp = Math.abs(apiSupportPixels[i] - myPixel);
            if(temp < offset ){
                offset = temp;
                bestFitPixel = apiSupportPixels[i];
            }
        }
        CacheUtil.getInstance().setBestFitPixel(bestFitPixel);
    }

    private void initView(){
        bodyLayout = (RelativeLayout) findViewById(R.id.splash_body_layout);
        msgLayout = (LinearLayout)findViewById(R.id.splash_msg_layout);
        logoIv = (ImageView)findViewById(R.id.splash_logo_iv);
        textTv = (TextView)findViewById(R.id.splash_ad_tv);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_logo_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_logo_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_logo_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();

        if(CacheUtil.getInstance().isLogin() && !TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            String userId = CacheUtil.getInstance().getUserId();
            String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(userPhotoUrl, logoIv, options);
        }


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
        //判断当前网络状态
        if(NetWorkUtil.isNetworkConnected(this)){
            // 判断用户是否登录，如果登录则进入主页面
            if (CacheUtil.getInstance().isLogin()) {
                // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
                handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            } else {
                if(CacheUtil.getInstance().isGuide()){
                    handler.sendEmptyMessageDelayed(GO_HOME_NO_lOGIN, SPLASH_DELAY_MILLIS);
                }else{
                    handler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
                }
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

        LoginController.getInstance().init(this);
        LoginController.getInstance().getUserDetailInfo(CacheUtil.getInstance().getUserId(),CacheUtil.getInstance().getToken(),false,false,null);
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
                case GO_HOME_NO_lOGIN:
                    goHomeNoLogin();
                    break;
                case GO_GUIDE:
                    goGuide();
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    private void goHomeNoLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

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
