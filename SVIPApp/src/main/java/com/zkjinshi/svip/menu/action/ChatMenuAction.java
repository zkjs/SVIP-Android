package com.zkjinshi.svip.menu.action;

import android.text.TextUtils;
import android.widget.Toast;

import com.zkjinshi.svip.activity.im.actions.MessageListViewManager;
import com.zkjinshi.svip.activity.im.actions.QuickMenuManager;
import com.zkjinshi.svip.utils.VIPContext;

/**
 * 快捷聊天指令
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatMenuAction implements MenuAction{

    private String content;

    public ChatMenuAction(String content){
        this.content = content;
    }

    @Override
    public void executeAction() {
        if(!TextUtils.isEmpty(content) ){
            QuickMenuManager.getInstance().getMessageListViewManager().sendTextMessage(content);
        }
    }
}
