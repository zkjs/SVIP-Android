package com.zkjinshi.base.net.core;

import com.zkjinshi.base.net.queue.QueueContext;

/**
 * 消息接收线程
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageReceiver implements Runnable {

    public static final String TAG = MessageReceiver.class.getSimpleName();

    private boolean isRunning;
    private boolean needSleepForTask;
    private String message;

    @Override
    public void run() {

        isRunning = true;

        while (isRunning) {

            needSleepForTask = true;

            // 1、处理优先响应队列
            if (!QueueContext.getInstance().getFirstResponseQueue()
                    .isEmpty()) {
                message = QueueContext.getInstance()
                        .getFirstResponseQueue().poll();
                WebSocketManager.getInstance().process(message);
                needSleepForTask = false;
            }

            // 2、处理普通响应队列
            else if (!QueueContext.getInstance().getNormalResponseQueue()
                    .isEmpty()) {
                message = QueueContext.getInstance()
                        .getNormalResponseQueue().poll();
                WebSocketManager.getInstance().process(message);
                needSleepForTask = false;
            }

            if (needSleepForTask) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void close() {
        isRunning = false;
    }

}
