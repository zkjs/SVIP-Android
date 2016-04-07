package com.zkjinshi.svip.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopListView extends ListView {

    public ShopListView(Context context) {
        super(context);
    }

    public ShopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

    @Override
    protected boolean canAnimate() {
        return super.canAnimate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected View findViewTraversal(int id) {
        return super.findViewTraversal(id);
    }

    @Override
    protected View findViewWithTagTraversal(Object tag) {
        return super.findViewWithTagTraversal(tag);
    }
}
