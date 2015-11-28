package com.zkjinshi.svip.menu.action;

import android.text.TextUtils;

import com.zkjinshi.svip.activity.im.group.actions.QuickMenuManager;


/**
 * 快捷聊天指令
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatGroupMenuAction implements MenuAction {

    private String content;

    public ChatGroupMenuAction(String content){
        this.content = content;
    }

    @Override
    public void executeAction() {
        if(!TextUtils.isEmpty(content) ){
            QuickMenuManager.getInstance().getMessageListViewManager().sendTextMessage(content);
        }
    }
}
