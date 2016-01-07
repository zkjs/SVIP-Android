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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideFourActivity extends BaseActivity {

    private GestureDetector gestureDetector;
    private CircleImageView mCivBgGuide;

    private ImageView mIvDefaultP12;
    private ImageView mIvDefaultP7;
    private ImageView mIvDefaultP8;
    private ImageView mIvDefaultP9;
    private ImageView mIvDefaultP10;
    private ImageView mIvDefaultP11;
    private ImageView mIvDefaultP13;
    private ImageView mIvDefaultP14;
    private ImageView mIvDefaultP15;
    private ImageView mIvDefaultP17;
    private ImageView mIvDefaultP16;
    private ImageView mIvDefaultText;

    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_four);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();

        initView();
        initAnim();
        initData();
        initListener();
    }

    private void initView() {

        mIvDefaultText = (ImageView) findViewById(R.id.iv_default_text_4);
        mCivBgGuide = (CircleImageView) findViewById(R.id.civ_bg_guide);
        mCivBgGuide.setImageResource(R.mipmap.defoult_bg_4);

        //居中
        mIvDefaultP12 = (ImageView) findViewById(R.id.iv_default_p_12);

        //左
        mIvDefaultP7 = (ImageView) findViewById(R.id.iv_default_p_7);
        mIvDefaultP8 = (ImageView) findViewById(R.id.iv_default_p_8);
        mIvDefaultP10 = (ImageView) findViewById(R.id.iv_default_p_10);
        mIvDefaultP11 = (ImageView) findViewById(R.id.iv_default_p_11);
        mIvDefaultP15 = (ImageView) findViewById(R.id.iv_default_p_15);

        //右
        mIvDefaultP9 = (ImageView) findViewById(R.id.iv_default_p_9);
        mIvDefaultP13 = (ImageView) findViewById(R.id.iv_default_p_13);
        mIvDefaultP14 = (ImageView) findViewById(R.id.iv_default_p_14);
        mIvDefaultP16 = (ImageView) findViewById(R.id.iv_default_p_16);
        mIvDefaultP17 = (ImageView) findViewById(R.id.iv_default_p_17);
    }

    private void initAnim() {

        int width  = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mIvDefaultP12.measure(width, height);

        mIvDefaultP7.measure(width, height);
        mIvDefaultP8.measure(width, height);
        mIvDefaultP9.measure(width, height);

        mIvDefaultP10.measure(width, height);
        mIvDefaultP11.measure(width, height);

        mIvDefaultP13.measure(width, height);
        mIvDefaultP14.measure(width, height);

        mIvDefaultP16.measure(width, height);

        mIvDefaultP15.measure(width, height);
        mIvDefaultP17.measure(width, height);

        mIvDefaultText.measure(width,height);
        final int textTranslationWidth = (mScreenWidth - mIvDefaultText.getMeasuredWidth())/2;

        final int p12Translation = (mScreenWidth - mIvDefaultP12.getMeasuredWidth())/2;
        final int p8Translation  = (mScreenWidth - mIvDefaultP8.getMeasuredWidth())/2;
        final int p16Translation = (mScreenWidth - mIvDefaultP16.getMeasuredWidth())/2;

        final int p7Translation = p8Translation - mIvDefaultP7.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);
        final int p9Translation = p8Translation - mIvDefaultP9.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);

        final int p11Translation = p12Translation - mIvDefaultP11.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);
        final int p13Translation = p12Translation - mIvDefaultP13.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);

        final int p10Translation = p11Translation - mIvDefaultP10.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);
        final int p14Translation = p13Translation - mIvDefaultP14.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);

        final int p15Translation = p16Translation - mIvDefaultP15.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);
        final int p17Translation = p16Translation - mIvDefaultP17.getMeasuredWidth() - DisplayUtil.dip2px(this, 10);

        ObjectAnimator p12TranslationX = ObjectAnimator.ofFloat(mIvDefaultP12, "translationX", 0, -p12Translation);
        ObjectAnimator p12Alpha = ObjectAnimator.ofFloat(mIvDefaultP12, "alpha", 0.25f, 1);

        AnimatorSet set = new AnimatorSet();
        set.play(p12TranslationX).with(p12Alpha);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mIvDefaultP7.setVisibility(View.VISIBLE);
                mIvDefaultP8.setVisibility(View.VISIBLE);
                mIvDefaultP9.setVisibility(View.VISIBLE);
                mIvDefaultP10.setVisibility(View.VISIBLE);
                mIvDefaultP11.setVisibility(View.VISIBLE);
                mIvDefaultP13.setVisibility(View.VISIBLE);
                mIvDefaultP14.setVisibility(View.VISIBLE);
                mIvDefaultP16.setVisibility(View.VISIBLE);
                mIvDefaultP15.setVisibility(View.VISIBLE);
                mIvDefaultP17.setVisibility(View.VISIBLE);

                AnimatorSet set = new AnimatorSet();
                ObjectAnimator p8TranslationX = ObjectAnimator.ofFloat(mIvDefaultP8, "translationX", 0, p8Translation);
                ObjectAnimator p8Alpha = ObjectAnimator.ofFloat(mIvDefaultP8, "alpha", 0.25f, 1);

                ObjectAnimator p16TranslationX = ObjectAnimator.ofFloat(mIvDefaultP16, "translationX", 0, -p16Translation);
                ObjectAnimator p16Alpha = ObjectAnimator.ofFloat(mIvDefaultP16, "alpha", 0.25f, 1);

                ObjectAnimator p7TranslationX = ObjectAnimator.ofFloat(mIvDefaultP7, "translationX", 0, p7Translation);
                ObjectAnimator p7Alpha = ObjectAnimator.ofFloat(mIvDefaultP7, "alpha", 0.25f, 1);

                ObjectAnimator p9TranslationX = ObjectAnimator.ofFloat(mIvDefaultP9, "translationX", 0, -p9Translation);
                ObjectAnimator p9Alpha = ObjectAnimator.ofFloat(mIvDefaultP9, "alpha", 0.25f, 1);

                ObjectAnimator p10TranslationX = ObjectAnimator.ofFloat(mIvDefaultP10, "translationX", 0, p10Translation);
                ObjectAnimator p10Alpha = ObjectAnimator.ofFloat(mIvDefaultP10, "alpha", 0.25f, 1);

                ObjectAnimator p11TranslationX = ObjectAnimator.ofFloat(mIvDefaultP11, "translationX", 0, p11Translation);
                ObjectAnimator p11Alpha = ObjectAnimator.ofFloat(mIvDefaultP11, "alpha", 0.25f, 1);

                ObjectAnimator p13TranslationX = ObjectAnimator.ofFloat(mIvDefaultP13, "translationX", 0, -p13Translation);
                ObjectAnimator p13Alpha = ObjectAnimator.ofFloat(mIvDefaultP13, "alpha", 0.25f, 1);

                ObjectAnimator p14TranslationX = ObjectAnimator.ofFloat(mIvDefaultP14, "translationX", 0, -p14Translation);
                ObjectAnimator p14Alpha = ObjectAnimator.ofFloat(mIvDefaultP14, "alpha", 0.25f, 1);

                ObjectAnimator p15TranslationX = ObjectAnimator.ofFloat(mIvDefaultP15, "translationX", 0, p15Translation);
                ObjectAnimator p15Alpha = ObjectAnimator.ofFloat(mIvDefaultP15, "alpha", 0.25f, 1);

                ObjectAnimator p17TranslationX = ObjectAnimator.ofFloat(mIvDefaultP17, "translationX", 0, -p17Translation);
                ObjectAnimator p17Alpha = ObjectAnimator.ofFloat(mIvDefaultP17, "alpha", 0.25f, 1);

                set.play(p8TranslationX).with(p8Alpha).with(p7TranslationX).with(p7Alpha).
                    with(p9TranslationX).with(p9Alpha).with(p10TranslationX).with(p10Alpha).
                    with(p11TranslationX).with(p11Alpha).with(p13TranslationX).with(p13Alpha).
                    with(p14TranslationX).with(p14Alpha).with(p16TranslationX).with(p16Alpha).
                    with(p15TranslationX).with(p15Alpha).with(p17TranslationX).with(p17Alpha);
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
                set.setDuration(500).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(300).start();
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
                    goPreGuide();
                } else if (x < 0) {
                    //向右移动
                    goNextGuide();
                }
                return true;
            }
        };

    /**
     * 上一个向导页
     */
    private void goPreGuide() {
        Intent guidePre = new Intent(GuideFourActivity.this, GuideThreeActivity.class);
        this.startActivity(guidePre);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        GuideFourActivity.this.finish();
    }

    /**
     * 下一个向导页
     */
    private void goNextGuide() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shrink_out_from_right);
        mIvDefaultText.startAnimation(anim);

        Intent guideNext = new Intent(GuideFourActivity.this, GuideFiveActivity.class);
        this.startActivity(guideNext);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        GuideFourActivity.this.finish();
    }

}
