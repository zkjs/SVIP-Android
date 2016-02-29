package com.zkjinshi.svip.activity.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideOneActivity extends BaseActivity {

    private GestureDetector gestureDetector;
    private CircleImageView mCivBgGuide;
    private RelativeLayout  mRlUfoCloud;
    private ImageView       mIvUfo;
    private ImageView       mIvCloud1;
    private ImageView       mIvCloud2;
    private ImageView       mIvDefaultText;
    private Animation       mShakeAnim;//抖动动画

    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_one);

        YunBaSubscribeManager.getInstance().unSubscribe(this);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();

        initView();
        initAnim();
        initData();
        initListener();
    }

    private void initView() {
        mCivBgGuide = (CircleImageView) findViewById(R.id.civ_bg_guide);
        mCivBgGuide.setImageResource(R.mipmap.defoult_earth);

        mRlUfoCloud = (RelativeLayout) findViewById(R.id.rl_ufo_cloud);
        mIvUfo    = (ImageView) findViewById(R.id.iv_default_ufo);
        mIvCloud1 = (ImageView) findViewById(R.id.iv_cloud_1);
        mIvCloud2 = (ImageView) findViewById(R.id.iv_cloud_2);
        mIvDefaultText = (ImageView) findViewById(R.id.iv_default_text);
    }

    private void initAnim() {
        mShakeAnim = AnimationUtils.loadAnimation(GuideOneActivity.this, R.anim.shake);

        int width  = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mRlUfoCloud.measure(width,height);
        int UfoTranslationWidth = (mScreenWidth - mRlUfoCloud.getMeasuredWidth())/2;

        ObjectAnimator ufoTranslationX = ObjectAnimator.ofFloat(mIvUfo, "translationX", 0, -UfoTranslationWidth);
        ObjectAnimator ufoScaleX = ObjectAnimator.ofFloat(mIvUfo, "scaleX", 0.0f, 1f);
        ObjectAnimator ufoScaleY = ObjectAnimator.ofFloat(mIvUfo, "scaleY", 0.0f, 1f);
        ObjectAnimator ufoAlpha = ObjectAnimator.ofFloat(mIvUfo, "alpha", 0.25f, 1);

        ObjectAnimator cloud1TranslationX = ObjectAnimator.ofFloat(mIvCloud1, "translationX", 0, -UfoTranslationWidth);
        ObjectAnimator cloud1ScaleX = ObjectAnimator.ofFloat(mIvCloud1, "scaleX", 0.0f, 1f);
        ObjectAnimator cloud1ScaleY = ObjectAnimator.ofFloat(mIvCloud1, "scaleY", 0.0f, 1f);
        ObjectAnimator cloud1Alpha = ObjectAnimator.ofFloat(mIvCloud1, "alpha", 0.25f, 1);

        ObjectAnimator cloud2TranslationX = ObjectAnimator.ofFloat(mIvCloud2, "translationX", 0, -UfoTranslationWidth);
        ObjectAnimator cloud2ScaleX = ObjectAnimator.ofFloat(mIvCloud2, "scaleX", 0.0f, 1f);
        ObjectAnimator cloud2ScaleY = ObjectAnimator.ofFloat(mIvCloud2, "scaleY", 0.0f, 1f);
        ObjectAnimator cloud2Alpha = ObjectAnimator.ofFloat(mIvCloud2, "alpha", 0.25f, 1);

        mIvDefaultText.measure(width,height);
        final int textTranslationWidth = (mScreenWidth - mIvDefaultText.getMeasuredWidth())/2;

//        ObjectAnimator textTranslationX = ObjectAnimator.ofFloat(mIvDefaultText, "translationX", 0, -textTranslationWidth);
//        ObjectAnimator textScaleX = ObjectAnimator.ofFloat(mIvDefaultText, "scaleX", 0.0f, 1f);
//        ObjectAnimator textScaleY = ObjectAnimator.ofFloat(mIvDefaultText, "scaleY", 0.0f, 1f);
//        ObjectAnimator textAlpha = ObjectAnimator.ofFloat(mIvDefaultText, "alpha", 0.25f, 1);

        AnimatorSet set = new AnimatorSet();
        set.play(ufoTranslationX).with(ufoScaleX).with(ufoScaleY).with(ufoAlpha).
                with(cloud1TranslationX).with(cloud1ScaleX).with(cloud1ScaleY).with(cloud1Alpha).
                with(cloud2TranslationX).with(cloud2ScaleX).with(cloud2ScaleY).with(cloud2Alpha);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mIvDefaultText.setVisibility(View.VISIBLE);
                ObjectAnimator textTranslationX = ObjectAnimator.ofFloat(mIvDefaultText, "translationX", 0, -textTranslationWidth);
                ObjectAnimator textAlpha = ObjectAnimator.ofFloat(mIvDefaultText, "alpha", 0.25f, 1);
                AnimatorSet set = new AnimatorSet();
                set.play(textTranslationX).with(textAlpha);
                set.setDuration(200).start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(800).start();
    }

    private void initData() {
        gestureDetector = new GestureDetector(this, onGestureListener);
    }

    private void initListener() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener onGestureListener =
        new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x = e2.getX() - e1.getX();

                if (x > 0) {
                    //向左移动
                } else if (x < 0) {
                    //向右移动
                    goNextGuide();

                }
                return true;
            }
        };

    /**
     * 进入下一个向导页
     */
    private void goNextGuide() {

        Animation anim = AnimationUtils.loadAnimation(GuideOneActivity.this, R.anim.shrink_out_from_right);
        mIvDefaultText.startAnimation(anim);

        Intent guideTwo = new Intent(GuideOneActivity.this, GuideTwoActivity.class);
        this.startActivity(guideTwo);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        GuideOneActivity.this.finish();
    }

}
