package com.zkjinshi.base.net.listener;
/**
 * 网络链接状态监听
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface SocketListener {
	
	public void onConnect();

	public void onDisconnect(int code, String reason);

	public void onError(Exception error);
}
