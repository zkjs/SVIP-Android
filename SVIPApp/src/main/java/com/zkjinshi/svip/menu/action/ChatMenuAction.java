package com.zkjinshi.svip.menu.action;

import android.widget.Toast;

import com.zkjinshi.svip.utils.VIPContext;

/**
 * 快捷聊天指令
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatMenuAction implements MenuAction{

    @Override
    public void executeAction() {
        Toast.makeText(VIPContext.getInstance().getContext(),"触发聊天指令",Toast.LENGTH_LONG).show();
    }
}
