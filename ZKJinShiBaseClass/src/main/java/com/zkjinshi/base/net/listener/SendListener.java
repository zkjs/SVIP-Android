package com.zkjinshi.base.net.listener;


import org.json.JSONObject;

/**
 * 消息发送监听
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class SendListener {
	
	private long timestamp;
	private int type;

	/**
	 * @param flag false:出错,true:正常
	 */
	public abstract void onSend(boolean flag, JSONObject msg);

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}