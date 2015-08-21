package com.zkjinshi.base.net.observer;

/**
 * 消息观察者接口
 * 开发者:JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface IMessageObserver {

    /**
     * 接收消息包
     *
     * @param message
     */
    public void receive(String message);
}
