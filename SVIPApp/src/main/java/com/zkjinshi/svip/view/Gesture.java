package com.zkjinshi.svip.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.zkjinshi.base.log.LogUtil;

/**
 * Created by dujiande on 2016/4/8.
 */
public class Gesture implements GestureDetector.OnGestureListener {
    public Context context;
    public FlingCallback flingCallback = null;
    public UpFilingViewCallBack upFilingViewCallBack = null;
    public View selfView = null;
    public Gesture(Context context) {
        // TODO Auto-generated constructor stub
        this.context=context;

    }
    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    /**
     * 滑动事件的处理
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if(e1 != null && e2 != null){
            Log.d("Gesture","velocityX="+velocityX+" velocityY="+velocityY);
            float y = Math.abs(e2.getY() - e1.getY());
            float x = Math.abs(e2.getX() - e1.getX());
            //左滑动
            if (e1.getX() - e2.getX() > 50 && y < 80) {
                if(flingCallback != null){
                    flingCallback.flingLeft();
                }
                return false;
            }
            //右滑动
            else if (e2.getX() - e1.getX()>50 && y < 80) {
                if(flingCallback != null){
                    flingCallback.flingRight();
                }
                return false;
            }

            //上滑
            else if(e1.getY() - e2.getY() > 80 && x < 80 ){
                if(selfView != null){
                    int left = selfView.getLeft() - 30;
                    int right = selfView.getRight() + 30;
                    int bottom = selfView.getBottom();
                    int top = selfView.getTop();
                    if(e1.getX() > left && e1.getX() < right &&
                            e2.getX() > left && e2.getX() < right &&
                            e1.getY() > bottom && e2.getY() < top ){
                        if(upFilingViewCallBack != null){
                            upFilingViewCallBack.flingUp();
                        }

                    }
                }
            }
        }

        return false;
    }
}
