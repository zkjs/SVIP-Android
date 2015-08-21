package com.zkjinshi.base.net.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义DataInputStream
 * 开发者:JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SocketDataInputStream extends DataInputStream {

	public SocketDataInputStream(InputStream in) {
		super(in);
	}

	public byte[] readBytes(int length) throws IOException {
		byte[] buffer = new byte[length];
		readFully(buffer);
		return buffer;
	}

}
