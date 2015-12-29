package com.zkjinshi.svip.view;

import android.widget.ScrollView;

/**
 * 自定义ScrollView滚动事件
 * 开发者：JimmyZhang
 * 日期：2015/10/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface ScrollViewListener {

    void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);

}
