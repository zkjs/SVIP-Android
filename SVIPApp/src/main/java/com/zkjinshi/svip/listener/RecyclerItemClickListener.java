package com.zkjinshi.svip.listener;

import android.view.View;

/**
 * RecyclerView监听器
 * 开发者：JimmyZhang
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface RecyclerItemClickListener {

    /**
     * 监听点击item事件
     * @param view
     * @param postion
     */
    public void onItemClick(View view,int postion);
}

