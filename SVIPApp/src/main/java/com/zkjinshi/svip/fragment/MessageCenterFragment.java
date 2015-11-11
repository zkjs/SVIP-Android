package com.zkjinshi.svip.fragment;

import android.view.View;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseFragment;

/**
 * 采用toolbar显示的服务中心效果界面
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageCenterFragment extends BaseFragment{

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_message_center, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
