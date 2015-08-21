package com.zkjinshi.base.net.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 消息队列上下文
 * 开发者:JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class QueueContext {

    private static QueueContext instance;
    private ConcurrentLinkedQueue<String> firstRequestQueue;//优先请求队列
    private ConcurrentLinkedQueue<String> firstResponseQueue;//优先响应队列
    private ConcurrentLinkedQueue<String> normalRequestQueue;//普通请求队列
    private ConcurrentLinkedQueue<String> normalResponseQueue;//普通请求队列

    private QueueContext() {
    }

    public static synchronized QueueContext getInstance() {
        if (instance == null) {
            instance = new QueueContext();
            instance.init();
        }
        return instance;
    }

    private void init() {
        firstRequestQueue = new ConcurrentLinkedQueue<String>();
        firstResponseQueue = new ConcurrentLinkedQueue<String>();
        normalRequestQueue = new ConcurrentLinkedQueue<String>();
        normalResponseQueue = new ConcurrentLinkedQueue<String>();
    }

    /**
     * 获取优先请求队列
     *
     * @return
     */
    public ConcurrentLinkedQueue<String> getFirstRequestQueue() {
        return firstRequestQueue;
    }

    /**
     * 获取优先响应队列
     *
     * @return
     */
    public ConcurrentLinkedQueue<String> getFirstResponseQueue() {
        return firstResponseQueue;
    }

    /**
     * 获取普通请求列队
     *
     * @return
     */
    public ConcurrentLinkedQueue<String> getNormalRequestQueue() {
        return normalRequestQueue;
    }

    /**
     * 获取普通响应队列
     *
     * @return
     */
    public ConcurrentLinkedQueue<String> getNormalResponseQueue() {
        return normalResponseQueue;
    }
}
