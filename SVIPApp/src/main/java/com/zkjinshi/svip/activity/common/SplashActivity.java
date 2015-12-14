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
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

/**
 * 开机启动页面
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class
SplashActivity extends Activity {

    private DisplayImageOptions options;

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
        //logoFadeInAnim = AnimationUtils.loadAnimation(this,R.anim.fade_in_logo);
       // logoIv.startAnimation(logoFadeInAnim);
        int[] ids = {R.id.guiji01,R.id.guiji02,R.id.guiji03};
        for(int i=0;i<ids.length;i++){
            Animation rotate = AnimationUtils.loadAnimation(this,R.anim.anim_guiji);
            findViewById(ids[i]).startAnimation(rotate);
        }



        //logo text 淡入效果
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
                handler.sendEmptyMessageDelayed(GO_LOGIN, SPLASH_DELAY_MILLIS);
               // handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
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
//        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
//        SplashActivity.this.startActivity(mainIntent);
//        SplashActivity.this.finish();
//        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        LoginController.getInstance().init(this);
        LoginController.getInstance().getUserDetailInfo(CacheUtil.getInstance().getUserId(),CacheUtil.getInstance().getToken(),false,null);
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
