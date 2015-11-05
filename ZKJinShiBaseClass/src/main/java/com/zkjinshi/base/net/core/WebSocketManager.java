package com.zkjinshi.base.net.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.ping.HeartBeatTimer;
import com.zkjinshi.base.net.queue.QueueContext;
import com.zkjinshi.base.net.queue.QueueType;
import com.zkjinshi.base.net.util.ImCacheUtil;
import com.zkjinshi.base.util.Constants;

import java.net.URI;

/**
 * WebSocketManager管理器
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketManager extends Handler implements IMessageProcess, WebSocketClient.Listener {

    public static final String TAG = "WebSocketManager";

    public static final int NOTIFY_OBSERVERS_MSG_WHAT = 0x0010;

    private static WebSocketManager instance;
    private IMessageListener messageListener;
    private WebSocketClient webSocketClient;
    private Thread sendThread, receiveThread;
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
    private URI uri;
    private boolean isLogoutIM;
    private boolean isConnected;

    private WebSocketManager() {
    }

    public synchronized static WebSocketManager getInstance() {
        if (null == instance) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    private void init() {
        isLogoutIM = false;
        messageReceiver = new MessageReceiver();
        messageSender = new MessageSender();
        uri = URI.create("wss://" + ConfigUtil.getInst().getIMHost() + ":" + ConfigUtil.getInst().getIMPort() + "/zkjs");
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
    public WebSocketManager initClient() {
        init();
        start();
        return this;
    }

    public WebSocketManager initService(Context context) {
        Intent intent = new Intent();
        intent.setAction(context.getPackageName()+".im.Message_SERVICE");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
        return this;
    }

    public void stopService(Context context) {
        Intent intent = new Intent();
        intent.setAction(context.getPackageName() + ".im.Message_SERVICE");
        intent.setPackage(context.getPackageName());
        context.stopService(intent);
    }

    public void logoutIM(Context context) {
        isLogoutIM = true;
        stopService(context);
        close();
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
        stopHeartBeat();
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
        Message msg = new Message();
        msg.what = NOTIFY_OBSERVERS_MSG_WHAT;
        msg.obj = message;
        sendMessage(msg);
    }

    public void setMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void onConnect() {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onConnect()");
        isConnected = true;
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
        isConnected = false;
        ImCacheUtil.getInstance().setIMLogin(false);
        if (null != webSocketClient) {
            if (!isLogoutIM) {
                webSocketClient.reconnect();
            }
        }
    }

    @Override
    public void onError(Exception e) {
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onError()-" + e.getMessage());
        isConnected = false;
        ImCacheUtil.getInstance().setIMLogin(false);
        if (null != webSocketClient) {
            if (!isLogoutIM) {
                webSocketClient.reconnect();
            }
        }
    }

    /**
     * 开启心跳服务
     */
    public void startHeartBeat() {
        HeartBeatTimer.getInstance().init().startHeartbeat();
    }

    /**
     * 停止心跳服务
     */
    public void stopHeartBeat() {
        HeartBeatTimer.getInstance().stopHeartbeat();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case NOTIFY_OBSERVERS_MSG_WHAT:
            {//通知所有观察者
                String message = (String)msg.obj;
                MessageSubject.getInstance().notifyObservers(message);
            }
            break;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
