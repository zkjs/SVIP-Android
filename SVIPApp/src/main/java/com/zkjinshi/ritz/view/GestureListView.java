package com.zkjinshi.ritz.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by dujiande on 2016/4/8.
 */
public class GestureListView extends ListView {

    Context context;
    GestureDetector gestureDetector = null;
    /**
     * 在xml布局里面使用GestureList，默认的会调用这个构造方法
     * @param context
     * @param attrs
     */
    public GestureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context=context;

    }
    public GestureListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.context=context;
    }
    public GestureListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context=context;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(gestureDetector == null){
            return super.onTouchEvent(ev);
        }else if(gestureDetector.onTouchEvent(ev)){
             return true;
        }
        return super.onTouchEvent(ev);

    }

    public void setFlingCallback(FlingCallback flingCallback) {
        Gesture gesture = new Gesture(context);
        gesture.flingCallback = flingCallback;
        gestureDetector = new GestureDetector(context,gesture);
    }
}
