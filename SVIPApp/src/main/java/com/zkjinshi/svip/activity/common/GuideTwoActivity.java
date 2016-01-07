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

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideTwoActivity extends BaseActivity {

    private GestureDetector gestureDetector;
    private CircleImageView mCivBgGuide;
    private ImageView       mIvBicycle;
    private ImageView       mIvCar;
    private ImageView       mIvDefaultText;

    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_two);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();

        initView();
        initAnim();
        initData();
        initListener();
    }

    private void initView() {
        mIvDefaultText = (ImageView) findViewById(R.id.iv_default_text_2);
        mCivBgGuide = (CircleImageView) findViewById(R.id.civ_bg_guide);
        mCivBgGuide.setImageResource(R.mipmap.defoult_bg_2);
        mIvBicycle = (ImageView) findViewById(R.id.iv_bicycle);
        mIvCar = (ImageView) findViewById(R.id.iv_car);
        mIvBicycle.setImageResource(R.mipmap.defoult_bicycle);
    }

    private void initAnim() {
        int width  = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mIvBicycle.measure(width,height);
        int translationWidth = (mScreenWidth - mIvBicycle.getMeasuredWidth())/2;
        mIvDefaultText.measure(width,height);
        final int textTranslationWidth = (mScreenWidth - mIvDefaultText.getMeasuredWidth())/2;

        ObjectAnimator translationX = ObjectAnimator.ofFloat(mIvBicycle, "translationX", 0, translationWidth);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvBicycle, "alpha", 0.25f, 1);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mIvBicycle, "scaleX", 1f, 0.10f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mIvBicycle, "scaleY", 1f, 0.10f);

        AnimatorSet set = new AnimatorSet();
        set.play(translationX).with(alpha);
        set.play(scaleX).with(scaleY).after(translationX);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mIvCar.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator expandScaleX = ObjectAnimator.ofFloat(mIvCar, "scaleX", 1.0f, 5.0f);
                ObjectAnimator expandScaleY = ObjectAnimator.ofFloat(mIvCar, "scaleY", 1.0f, 5.0f);
                set.play(expandScaleX).with(expandScaleY);
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
                set.setDuration(600).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(600).start();
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
        Intent guidePre = new Intent(GuideTwoActivity.this, GuideOneActivity.class);
        this.startActivity(guidePre);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        GuideTwoActivity.this.finish();
    }

    /**
     * 下一个向导页
     */
    private void goNextGuide() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shrink_out_from_right);
        mIvDefaultText.startAnimation(anim);

        Intent guideNext = new Intent(GuideTwoActivity.this, GuideThreeActivity.class);
        this.startActivity(guideNext);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        GuideTwoActivity.this.finish();
    }

}
