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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideFiveActivity extends BaseActivity{

    private GestureDetector gestureDetector;
    private CircleImageView mCivBgGuide;
    private RelativeLayout  mRlDefaultBg;
    private CircleImageView mCivGuangHuan;
    private ImageView       mIvHuangGuan;
    private ImageView       mIvDefaultText;
    private ImageButton     mIbtnStart;

    private int mScreenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_five);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIvDefaultText = (ImageView) findViewById(R.id.iv_default_text_5);
        mCivBgGuide = (CircleImageView) findViewById(R.id.civ_bg_guide);
        mCivBgGuide.setImageResource(R.mipmap.defoult_bg_5);
        mRlDefaultBg  = (RelativeLayout) findViewById(R.id.rl_default_bg);
        mCivGuangHuan = (CircleImageView) findViewById(R.id.civ_guang_huan);
        mIvHuangGuan  = (ImageView) findViewById(R.id.iv_default_huang_guan);
        mIbtnStart    = (ImageButton) findViewById(R.id.ibtn_start);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCivGuangHuan, "scaleX", 0, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCivGuangHuan, "scaleY", 0, 1f);

        int width  = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mIvDefaultText.measure(width,height);
        final int textTranslationWidth = (mScreenWidth - mIvDefaultText.getMeasuredWidth())/2;

        mRlDefaultBg.measure(width, height);
        mIvHuangGuan.measure(width, height);

        final int translationHeight = (mRlDefaultBg.getMeasuredHeight() - mIvHuangGuan.getMeasuredHeight())/2;

        set.play(scaleX).with(scaleY);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIvHuangGuan.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();

                ObjectAnimator translationY = ObjectAnimator.ofFloat(mIvHuangGuan, "translationY", 0, translationHeight);
                set.play(translationY);
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
        set.setDuration(300);
        set.start();
    }

    private void initData() {
        gestureDetector = new GestureDetector(this, onGestureListener);
    }

    private void initListener() {
        //进入主页面
        mIbtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
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

                    }
                    return true;
                }
            };
    /**
     * 上一个向导页
     */
    private void goPreGuide() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shrink_out_from_right);
        mIvDefaultText.startAnimation(anim);

        Intent guidePre = new Intent(GuideFiveActivity.this, GuideFourActivity.class);
        this.startActivity(guidePre);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        GuideFiveActivity.this.finish();
    }

    /**
     * 进入主页面
     */
    private void goHome() {
        CacheUtil.getInstance().setGuide(true);
        Intent mainIntent = new Intent(GuideFiveActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

}
