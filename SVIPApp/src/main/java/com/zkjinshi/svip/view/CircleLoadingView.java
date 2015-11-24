package com.zkjinshi.svip.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zkjinshi.svip.R;



/**
 * 圆形状态控件
 * 开发者：dujiande
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CircleLoadingView extends View{

    public static final String TAG = CircleLoadingView.class.getSimpleName();

    private int yellowColor =  Color.parseColor("#fac473");
    private int whiteColor = Color.parseColor("#ffffff");
    protected Paint mSelectedCirclePaint,mWhitePaint,mBackPaint;
    private int width;
    private int height;
    public int alpha = 255;
    public int radius;
    public int changRadius;
    public Context context;
    public int offset = 1;

    public boolean runAble = true;
    public boolean isPause = false;

    public TypedArray typedArray;

    private Thread runThread = null;

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOTIFY_UPDATE_VIEW:
                    invalidate();
                    break;
            }
        }
    };

    public CircleLoadingView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        initView();


    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        initView();
    }

    private void initView(){
        Resources resources = context.getResources();
        changRadius = radius = typedArray.getDimensionPixelSize(R.styleable.CircleLoadingView_center_radius,resources.getDimensionPixelSize(R.dimen.center_radius));

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(yellowColor);
        mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
        mSelectedCirclePaint.setStyle(Paint.Style.FILL);
        mSelectedCirclePaint.setAlpha(150);

        mBackPaint = new Paint();
        mBackPaint.setFakeBoldText(true);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(yellowColor);
        mBackPaint.setTextAlign(Paint.Align.CENTER);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setAlpha(50);

        mWhitePaint = new Paint();
        mWhitePaint.setFakeBoldText(true);
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(whiteColor);
        mWhitePaint.setTextAlign(Paint.Align.CENTER);
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setAlpha(255);

        if(runThread == null){
            runThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runAble){
                        try {
                            Thread.sleep(10);
                            if(!isPause){
                                changRadius = changRadius + offset;
                                if(changRadius > width/2){
                                    changRadius = radius;
                                }
                                handler.sendEmptyMessage(NOTIFY_UPDATE_VIEW);
                            }

                        } catch (InterruptedException e) {
                            Log.e(TAG,e.getMessage());
                            e.printStackTrace();

                        }
                    }

                }
            });
            runThread.start();
        }
    }

    public void pause(){
        isPause = true;
    }

    public void resume(){
        isPause = false;
    }

    public void destroy(){
        runAble = false;
    }

    protected  void onMeasure(int width,int height){
        super.onMeasure(width,height);
        this.height = getMeasuredHeight();
        this.width = getMeasuredWidth();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int BigRadius = width/2;
        canvas.drawCircle(width / 2, height / 2, BigRadius, mBackPaint);
        canvas.drawCircle(width / 2, height / 2, changRadius, mSelectedCirclePaint);
        canvas.drawCircle(width / 2, height / 2, radius, mWhitePaint);
    }
}
