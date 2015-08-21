package com.zkjinshi.base.net.core;

/**
 * 消息监听器（包括socket连接成功和接收数据成功）
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface IMessageListener {

    /**
     * 消息接收
     *
     * @param message
     */
    public void onNetReceiveSucceed(String message);

    /**
     * 连接成功回调
     */
    public void onWebsocketConnected(WebSocketClient webSocketClient);

}
