package com.zkjinshi.base.net.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.queue.QueueContext;
import com.zkjinshi.base.net.queue.QueueType;
import com.zkjinshi.base.util.Constants;

import java.net.URI;

/**
 * WebSocketManager管理器
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketManager implements IMessageProcess, WebSocketClient.Listener {

    public static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private IMessageListener messageListener;
    private WebSocketClient webSocketClient;
    private Thread sendThread, receiveThread;
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
    private URI uri;

    private WebSocketManager() {
    }

    public synchronized static WebSocketManager getInstance() {
        if (null == instance) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    private void init() {
        messageReceiver = new MessageReceiver();
        messageSender = new MessageSender();
        uri = URI.create("ws://" + ConfigUtil.getInst().getIMHost() + ":" + ConfigUtil.getInst().getIMPort() + "/zkjs");
        webSocketClient = new WebSocketClient(uri, this);
        messageSender.setWebSocketClient(webSocketClient);
        if (null != sendThread) {
            sendThread.interrupt();
            sendThread = null;
        }
        sendThread = new Thread(messageSender);
        if (null != receiveThread) {
            receiveThread.interrupt();
            receiveThread = null;
        }
        receiveThread = new Thread(messageReceiver);
    }

    private void start() {

        if (null != sendThread) {
            sendThread.start();
        }
        if (null != receiveThread) {
            receiveThread.start();
        }
        webSocketClient.connect();
    }

    /**
     * 初始化im客户端
     */
    public void initClient() {
        init();
        start();
    }

    public WebSocketManager initService(Context context) {
        Intent intent = new Intent();
        intent.setAction(context.getPackageName()+".im.Message_SERVICE");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
        return this;
    }

    /**
     * 关闭所有线程和结束socket
     */
    public void close() {
        if (null != messageSender) {
            messageSender.close();
        }
        if (null != messageReceiver) {
            messageReceiver.close();
        }
        if (null != webSocketClient) {
            webSocketClient.disconnect();
        }
        Log.e(Constants.ZKJINSHI_BASE_TAG, TAG + ".close()");
    }

    /**
     * 根据类型发送队列消息
     *
     * @param message
     */
    public void sendMessage(String message, QueueType queueType) {
        if (queueType == QueueType.FIRST) {
            QueueContext.getInstance().getFirstRequestQueue().add(message);
        } else {
            sendMessage(message);
        }
    }

    /**
     * 发送普通队列消息
     *
     * @param message
     */
    public void sendMessage(String message) {
        QueueContext.getInstance().getNormalRequestQueue().add(message);
    }

    @Override
    public void process(String message) {
        // 1、处理所有消息,保存本地数据库
        if (null != messageListener) {
            messageListener.onNetReceiveSucceed(message);
        }
        // 2、通知所有观察者更新
        MessageSubject.getInstance().notifyObservers(message);
    }

    public void setMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void onConnect() {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onConnect()");
        if (null != webSocketClient) {
            if (null != messageListener) {
                messageListener.onWebsocketConnected(webSocketClient);
            }
        }
    }

    @Override
    public void onMessage(String message) {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onMessage(message):" + message);
        QueueContext.getInstance().getFirstResponseQueue().add(message);
    }

    @Override
    public void onMessage(byte[] data) {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onMessage(data)");
    }

    @Override
    public void onDisconnect(int code, String reason) {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onDisconnect()");
        if (null != webSocketClient) {
            webSocketClient.reconnect();
        }
    }

    @Override
    public void onError(Exception e) {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onError()-" + e.getMessage());
    }

}
