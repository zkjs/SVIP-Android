package com.zkjinshi.base.net.core;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 请求和响应队列上下文
 * 开发者:JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class QueueContext {

	private static QueueContext instance;

	private QueueContext() {
	}

	public synchronized static QueueContext getInstance() {
		if (null == instance) {
			instance = new QueueContext();
		}
		return instance;
	}

	private BlockingQueue<JSONObject> requestQueue = new LinkedBlockingQueue<JSONObject>();

	private BlockingQueue<String> responseQueue = new LinkedBlockingQueue<String>();

	public BlockingQueue<String> getResponseQueue() {
		if(null == responseQueue){
			responseQueue = new LinkedBlockingQueue<String>();
		}
		return responseQueue;
	}

	public BlockingQueue<JSONObject> getRequestQueue() {
		if(null == requestQueue){
			requestQueue = new LinkedBlockingQueue<JSONObject>();
		}
		return requestQueue;
	}
}
