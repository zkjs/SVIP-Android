package com.zkjinshi.svip.activity.indoor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

/**
 *
 * 室内导航地图
 * 开发者：JimmyZhang
 * 日期：2016/5/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class IndoorMapActivity extends BaseActivity implements View.OnTouchListener{

    static final float MIN_ZOOM_SCALE = 0.1f;
    static final float MAX_ZOOM_SCALE = 4.0f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final float MIN_FINGER_DISTANCE = 10.0f;

    int gestureMode;
    PointF prevPoint;
    PointF nowPoint;
    PointF midPoint;
    float prevFingerDistance;
    Matrix matrix;
    ImageView mImageView;
    Bitmap mBitmap;
    DisplayMetrics mDisplayMetrics;
    private float imageScale = 1.0f;

    private void initView(){
        mImageView = (ImageView) findViewById(R.id.floors_indoor_map_iv);
    }

    private void initData(){
        gestureMode = NONE;
        prevPoint = new PointF();
        nowPoint = new PointF();
        midPoint = new PointF();
        matrix = new Matrix();
        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_first_floor);
        mImageView.setImageBitmap(mBitmap);
        mImageView.setOnTouchListener(this);

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics); // 获取屏幕信息（分辨率等）

        initImageMatrix();
        mImageView.setImageMatrix(matrix);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPostTranslate(777,3537);
            }
        },3000);

    }

    private void initListeners(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);
        initView();
        initData();
        initListeners();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 主点按下
                gestureMode = DRAG;
                prevPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 副点按下
                prevFingerDistance = getFingerDistance(event);
                if (getFingerDistance(event) > MIN_FINGER_DISTANCE) {
                    gestureMode = ZOOM;
                    setMidpoint(midPoint, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (gestureMode == DRAG) {
                    nowPoint.set(event.getX(), event.getY());
                    matrix.postTranslate(nowPoint.x - prevPoint.x, nowPoint.y
                            - prevPoint.y);
                    prevPoint.set(nowPoint);
                } else if (gestureMode == ZOOM) {
                    float currentFingerDistance = getFingerDistance(event);
                    if (currentFingerDistance > MIN_FINGER_DISTANCE) {
                        float zoomScale = currentFingerDistance
                                / prevFingerDistance;
                        matrix.postScale(zoomScale, zoomScale, midPoint.x,
                                midPoint.y);
                        prevFingerDistance = currentFingerDistance;
                    }
                    checkImageViewSize();
                }
                mImageView.setImageMatrix(matrix);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                gestureMode = NONE;
                break;
        }
        return true;
    }

    /**
     * 限制图片缩放比例：不能太小，也不能太大
     */
    private void checkImageViewSize() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (gestureMode == ZOOM) {
            if (p[0] < MIN_ZOOM_SCALE) {
                float tScale = MIN_ZOOM_SCALE / p[0];
                matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
            } else if (p[0] > MAX_ZOOM_SCALE) {
                float tScale = MAX_ZOOM_SCALE / p[0];
                matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
            }
        }
    }

    private void initImageMatrix() {
        imageScale = Math.min(1.0f, Math.min(
                (float) mDisplayMetrics.widthPixels
                        / (float) mBitmap.getWidth(),
                (float) mDisplayMetrics.heightPixels
                        / (float) mBitmap.getHeight()));
        if (imageScale < 1.0f) { // 图片比屏幕大，需要缩小
            matrix.postScale(imageScale, imageScale);
        }
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        matrix.mapRect(rect); // 按 initImageScale 缩小矩形，或者不变
        float dx = (mDisplayMetrics.widthPixels - rect.width()) / 2.0f;
        float dy = (mDisplayMetrics.heightPixels - rect.height()) / 2.0f;
        matrix.postTranslate(dx, dy);
    }

    /**
     * 根据坐标点移动
     * @param x
     * @param y
     */
    private void setPostTranslate(float x,float y){
        matrix.reset();
        imageScale = Math.min(1.0f, Math.min(
                (float) mDisplayMetrics.widthPixels
                        / (float) mBitmap.getWidth(),
                (float) mDisplayMetrics.heightPixels
                        / (float) mBitmap.getHeight()));
        if (imageScale < 1.0f) { // 图片比屏幕大，需要缩小
            matrix.postScale(imageScale, imageScale);
        }
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        matrix.mapRect(rect); // 按 initImageScale 缩小矩形，或者不变
        float dx = (mDisplayMetrics.widthPixels  / 2.0f) -  x*imageScale;
        float dy = (mDisplayMetrics.heightPixels / 2.0f) - y*imageScale;
        matrix.postTranslate(dx, dy);
        mImageView.setImageMatrix(matrix);
        mImageView.invalidate();
    }

    Handler handler = new Handler();

    private float getFingerDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void setMidpoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2.0f, y / 2.0f);
    }

}
