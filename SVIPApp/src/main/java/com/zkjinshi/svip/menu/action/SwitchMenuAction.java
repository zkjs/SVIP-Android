package com.zkjinshi.svip.menu.action;

import android.view.View;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SwitchMenuAction implements MenuAction {

    private View actionMenuView,chatMenuView;

    public void setActionMenuView(View actionMenuView) {
        this.actionMenuView = actionMenuView;
    }

    public void setChatMenuView(View chatMenuView) {
        this.chatMenuView = chatMenuView;
    }

    @Override
    public void executeAction() {
        if(null != chatMenuView && null != actionMenuView){
            chatMenuView.setVisibility(View.VISIBLE);
            actionMenuView.setVisibility(View.GONE);
        }
    }
}
