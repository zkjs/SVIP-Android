package com.zkjinshi.svip.emchat.observer;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMNotifier;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.svip.manager.OrderManager;
import com.zkjinshi.svip.notification.NotificationHelper;
import com.zkjinshi.svip.utils.VIPContext;

/**
 *
 * 环信消息收发观监听器
 * 开发者：JimmyZhang
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMessageListener implements EMEventListener {

    private EMessageListener(){}

    private static EMessageListener instance;

    public synchronized static EMessageListener getInstance(){
        if(null == instance){
            instance = new EMessageListener();
        }
        return instance;
    }

    /**
     * 注册消息事件监听
     */
    public void registerEventListener(){
        EMChatManager.getInstance().registerEventListener(this);
        EMChat.getInstance().setAppInited();
    }

    /**
     * 移除消息事件监听
     */
    public void unregisterEventListener(){
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        NotificationHelper.getInstance().showNotification(VIPContext.getInstance().getContext(),event);
        EMessageSubject.getInstance().notifyObservers(event);
    }

}
