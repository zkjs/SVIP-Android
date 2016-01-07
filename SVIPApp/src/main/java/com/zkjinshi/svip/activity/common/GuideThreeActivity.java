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
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
public class GuideThreeActivity extends BaseActivity {

    private GestureDetector gestureDetector;
    private CircleImageView mCivBgGuide;

    private RelativeLayout mRlGuide;
    private ImageView      mIvGift;
    private ImageView      mIvGuangYun;
    private ImageView      mIvJiu;
    private ImageView      mIvPass;
    private ImageView      mIvHua;
    private ImageView      mIvDefaultText;

    private int viewHeight;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_three);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();

        initView();
        initData();
        initListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus == true) {
            viewHeight = mRlGuide.getHeight();
            initAnim();
        }
    }

    private void initView() {
        mIvDefaultText = (ImageView) findViewById(R.id.iv_default_text_3);
        mCivBgGuide = (CircleImageView) findViewById(R.id.civ_bg_guide);
        mCivBgGuide.setImageResource(R.mipmap.defoult_bg_3);

        mRlGuide = (RelativeLayout) findViewById(R.id.rl_guide);
        mIvGift  = (ImageView) findViewById(R.id.iv_liwu);
        mIvGuangYun = (ImageView) findViewById(R.id.iv_guang_yun);
        mIvPass  = (ImageView) findViewById(R.id.iv_pass);
        mIvJiu   = (ImageView) findViewById(R.id.iv_jiu);
        mIvHua   = (ImageView) findViewById(R.id.iv_hua);
    }

    private void initAnim() {

        int width  = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mIvGift.measure(width, height);
        int giftHeight = mIvGift.getMeasuredHeight();

        mIvGuangYun.measure(width, height);
        int guangYunHeight = mIvGuangYun.getMeasuredHeight();

        mIvPass.measure(width, height);
        int passCardHeight = mIvPass.getMeasuredHeight();

        mIvJiu.measure(width, height);
        int wineHeight = mIvJiu.getMeasuredHeight();

        mIvHua.measure(width, height);
        int huaHeight  = mIvHua.getMeasuredHeight();

        mIvDefaultText.measure(width,height);
        final int textTranslationWidth = (mScreenWidth - mIvDefaultText.getMeasuredWidth())/2;

        int giftTranslation = (viewHeight - guangYunHeight)/2 - giftHeight + DisplayUtil.dip2px(this, 15);
        ObjectAnimator giftTranslationY = ObjectAnimator.ofFloat(mIvGift, "translationY", 0, -giftTranslation);
        ObjectAnimator giftAlpha = ObjectAnimator.ofFloat(mIvGift, "alpha", 0.25f, 1);
        ObjectAnimator giftScaleX = ObjectAnimator.ofFloat(mIvGift, "scaleX", 0.1f, 1.0f);
        ObjectAnimator giftScaleY = ObjectAnimator.ofFloat(mIvGift, "scaleY", 0.1f, 1.0f);

        final int guangYunTranslation = (viewHeight - guangYunHeight)/2;
        final int passTranslation = (viewHeight - passCardHeight)/2 + DisplayUtil.dip2px(this, 20);
        final int wineTranslation = (viewHeight)/2 - DisplayUtil.dip2px(this, 20);
        final int huaTranslation = (viewHeight-huaHeight)/2 + DisplayUtil.dip2px(this, 20);


        AnimatorSet animSetOne = new AnimatorSet();
        animSetOne.play(giftTranslationY).with(giftAlpha).with(giftScaleX).with(giftScaleY);
        animSetOne.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mIvGuangYun.setVisibility(View.VISIBLE);
                ObjectAnimator guangYunTransaltionY = ObjectAnimator.ofFloat(mIvGuangYun,
                                                "translationY", 0, -guangYunTranslation);
                ObjectAnimator guangYunTransaltionX = ObjectAnimator.ofFloat(mIvGuangYun,
                     "translationX", 0, DisplayUtil.dip2px(GuideThreeActivity.this, 10));

                ObjectAnimator guangYunAlpha = ObjectAnimator.ofFloat(mIvGuangYun, "alpha", 0.25f, 1);
                ObjectAnimator guangScaleX = ObjectAnimator.ofFloat(mIvGuangYun, "scaleX", 0.0f, 1.0f);
                ObjectAnimator guangScaleY = ObjectAnimator.ofFloat(mIvGuangYun, "scaleY", 0.01f, 1.0f);

                AnimatorSet animSetTwo = new AnimatorSet();
                animSetTwo.play(guangYunTransaltionY).with(guangYunAlpha).with(guangYunTransaltionX).
                           with(guangScaleX).with(guangScaleY);
                animSetTwo.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        mIvPass.setVisibility(View.VISIBLE);
                        mIvHua.setVisibility(View.VISIBLE);
                        mIvJiu.setVisibility(View.VISIBLE);

                        ObjectAnimator passTransaltionY = ObjectAnimator.ofFloat(mIvPass, "translationY", 0, -passTranslation);
                        ObjectAnimator passAlpha = ObjectAnimator.ofFloat(mIvPass, "alpha", 0.25f, 1);
                        ObjectAnimator passScaleX = ObjectAnimator.ofFloat(mIvPass, "scaleX", 0.0f, 1.0f);
                        ObjectAnimator passScaleY = ObjectAnimator.ofFloat(mIvPass, "scaleY", 0.0f, 1.0f);

                        ObjectAnimator wineTransaltionY = ObjectAnimator.ofFloat(mIvJiu, "translationY", 0, -wineTranslation);
                        ObjectAnimator wineScaleX = ObjectAnimator.ofFloat(mIvJiu, "scaleX", 0.0f, 1.0f);
                        ObjectAnimator wineScaleY = ObjectAnimator.ofFloat(mIvJiu, "scaleY", 0.0f, 1.0f);
                        ObjectAnimator wineAlpha = ObjectAnimator.ofFloat(mIvJiu, "alpha", 0.25f, 1);

                        ObjectAnimator huaTransaltionY = ObjectAnimator.ofFloat(mIvHua, "translationY", 0, -huaTranslation);
                        ObjectAnimator huaAlpha = ObjectAnimator.ofFloat(mIvHua, "alpha", 0.25f, 1);
                        ObjectAnimator huaScaleX = ObjectAnimator.ofFloat(mIvHua, "scaleX", 0.0f, 1.0f);
                        ObjectAnimator huaScaleY = ObjectAnimator.ofFloat(mIvHua, "scaleY", 0.0f, 1.0f);
                        ObjectAnimator huaTransaltionX = ObjectAnimator.ofFloat(mIvHua,
                                "translationX", 0, DisplayUtil.dip2px(GuideThreeActivity.this, 20));

                        AnimatorSet animSetThree = new AnimatorSet();
                        animSetThree.play(passTransaltionY).with(passAlpha).with(passScaleX).with(passScaleY).
                                with(wineTransaltionY).with(wineAlpha).with(wineScaleX).with(wineScaleY).
                                with(huaTransaltionY).with(huaTransaltionX).with(huaAlpha).
                                with(huaScaleX).with(huaScaleY);

                        animSetThree.addListener(new Animator.AnimatorListener() {
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
                        animSetThree.setDuration(600).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animSetTwo.setDuration(300).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSetOne.setDuration(300).start();

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
        Intent guidePre = new Intent(GuideThreeActivity.this, GuideTwoActivity.class);
        this.startActivity(guidePre);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        GuideThreeActivity.this.finish();
    }

    /**
     * 下一个向导页
     */
    private void goNextGuide() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shrink_out_from_right);
        mIvDefaultText.startAnimation(anim);

        Intent guideNext = new Intent(GuideThreeActivity.this, GuideFourActivity.class);
        this.startActivity(guideNext);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        GuideThreeActivity.this.finish();
    }

}
