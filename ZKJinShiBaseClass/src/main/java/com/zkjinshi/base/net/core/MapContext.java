package com.zkjinshi.base.net.core;

import com.zkjinshi.base.net.listener.ReadListener;
import com.zkjinshi.base.net.listener.SendListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 接收和发送Map上下文
 * 开发者:JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MapContext {
	private static MapContext instance;

	private MapContext() {
	}

	public synchronized static MapContext getInstance() {
		if (null == instance) {
			instance = new MapContext();
		}
		return instance;
	}

	private Map<Integer, ReadListener> responseListenerMap = new HashMap<Integer, ReadListener>();
	private Map<String, SendListener> requestListenerMap = new HashMap<String, SendListener>();

	public Map<Integer, ReadListener> getResponseListenerMap() {
		if(null == responseListenerMap){
			responseListenerMap = new HashMap<Integer, ReadListener>();
		}
		return responseListenerMap;
	}

	public Map<String, SendListener> getRequestListenerMap() {
		if(null == requestListenerMap){
			requestListenerMap = new HashMap<String, SendListener>();
		}
		return requestListenerMap;
	}
}
