package com.zkjinshi.base.net.core;

import android.util.Log;

import com.zkjinshi.base.net.queue.QueueContext;
import com.zkjinshi.base.net.util.ImCacheUtil;
import com.zkjinshi.base.util.Constants;

import org.json.JSONObject;

/**
 * 消息发送线程
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageSender implements Runnable {

    public static final String TAG = MessageSender.class.getSimpleName();

    private boolean isRunning;// 是否正在运行
    private boolean needSleepForTask;// 是否需要休眠
    private WebSocketClient webSocketClient;
    private String message;
    private JSONObject messageObj;

    public void close() {
        isRunning = false;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public void quit() {
        isRunning = false;
    }

    @Override
    public void run() {

        try {

            isRunning = true;

            while (isRunning) {

                needSleepForTask = true;

                if (webSocketClient.isConnected()) {

                    if (ImCacheUtil.getInstance().isIMLogin()) {
                        // 1、处理优先请求队列
                        if (!QueueContext.getInstance().getFirstRequestQueue()
                                .isEmpty()) {

                            message = QueueContext.getInstance()
                                    .getFirstRequestQueue().poll();
                            webSocketClient.sendMessage(message);
                            needSleepForTask = false;
                            messageObj = new JSONObject(message);
                            Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-发送优先队列消息:" + messageObj.getInt("type"));
                        }

                        // 2、处理普通请求队列
                        else if (!QueueContext.getInstance()
                                .getNormalRequestQueue().isEmpty()) {
                            message = QueueContext.getInstance()
                                    .getNormalRequestQueue().poll();
                            webSocketClient.sendMessage(message);
                            needSleepForTask = false;
                            messageObj = new JSONObject(message);
                            Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-发送普通队列消息:" + messageObj.getInt("type"));
                        }
                    }

                } else {
                    if (null != webSocketClient) {
                        webSocketClient.connect();
                    }
                }

                // 3、处理完所有请求队列
                if (needSleepForTask) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.ZKJINSHI_BASE_TAG, TAG +
                    "-Exception:" + e.getMessage());
        }
    }


}
