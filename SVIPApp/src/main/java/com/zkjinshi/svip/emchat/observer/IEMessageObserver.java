package com.zkjinshi.svip.emchat.observer;

import com.easemob.EMNotifierEvent;

/**
 *
 * 环信消息收发观察者
 * 开发者：JimmyZhang
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface IEMessageObserver {

    /**
     * 接收消息包
     * @param event
     */
    public void receive(EMNotifierEvent event);
}
