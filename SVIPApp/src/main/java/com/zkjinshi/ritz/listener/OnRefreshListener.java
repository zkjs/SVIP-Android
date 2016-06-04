package com.zkjinshi.ritz.listener;

import android.view.View;
import android.widget.AdapterView;

/**
 * 下拉刷新监听
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface OnRefreshListener {

    /**
     * 正在刷新时的回调
     */
    void onRefreshing();

    /**
     * 加载更多, 加载完成后需要把脚布局隐藏
     */
    void onLoadingMore();

    /**
     * 处理item点击事件
     * */
    void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id);
}