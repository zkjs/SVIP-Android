package com.zkjinshi.base.net.listener;

import org.json.JSONObject;

/**
 * 消息接收接收监听
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ReadListener {
	
	/**
	 * 读回调函数
	 * @param success false：出错，true：正常
	 * @param msg 读入的信息
	 */
	public abstract void onRead(boolean success, JSONObject msg);
}
