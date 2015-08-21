package com.zkjinshi.base.net.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.zkjinshi.base.net.listener.ReadListener;
import com.zkjinshi.base.net.listener.SendListener;
import com.zkjinshi.base.net.listener.SocketListener;

import org.json.JSONObject;

import java.net.URI;


/**
 * 网络请求管理类
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketClient {
	private static final String TAG = "WebSocketClient";

	private static WebSocketClient mInstance = null;

	private SocketAsyncTask mSocketAsyncTask;
	private HandlerThread mHandlerThread;
	private Handler mHandler;

	public synchronized static WebSocketClient getInstance() {
		if (null == mInstance) {
			mInstance = new WebSocketClient();
		}
		return mInstance;
	}

	private WebSocketClient() {
	}

	public void connect(Context context, URI uri, SocketListener listener) {
		mSocketAsyncTask = new SocketAsyncTask();
		mHandlerThread = new HandlerThread("websocket-thread");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mSocketAsyncTask.setUrl(uri);
		mSocketAsyncTask.setContext(context);
		mSocketAsyncTask.setSocketListener(listener);
		mSocketAsyncTask.setHandler(mHandler);
		mSocketAsyncTask.execute();
	}

	public void connect(Context context, URI uri, SocketListener listener,
			int readThreadLimNum, int sendTrheadLimNum) {
		mSocketAsyncTask = new SocketAsyncTask();
		mHandlerThread = new HandlerThread("websocket-thread");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mSocketAsyncTask.setReadThreadLimitNum(readThreadLimNum);
		mSocketAsyncTask.setSendThreadLimitNum(sendTrheadLimNum);
		mSocketAsyncTask.setUrl(uri);
		mSocketAsyncTask.setContext(context);
		mSocketAsyncTask.setSocketListener(listener);
		mSocketAsyncTask.setHandler(mHandler);
		mSocketAsyncTask.execute();
	}

	public void disconnect() {
		if (null != mSocketAsyncTask) {
			mSocketAsyncTask.disconnect();
		}
	}

	public void send(JSONObject msg, SendListener sendListener) {
		if (null != mSocketAsyncTask && (mSocketAsyncTask.getSocket() != null)
				&& !(mSocketAsyncTask.getSocket().isClosed())) {
			mSocketAsyncTask.send(msg, sendListener);
		} else {
			Log.e(TAG, "连接尚未建立");
		}
	}

	public void send(byte[] msg) {
		if (null != mSocketAsyncTask && (mSocketAsyncTask.getSocket() != null)
				&& !(mSocketAsyncTask.getSocket().isClosed())) {
			mSocketAsyncTask.send(msg);
		} else {
			Log.e(TAG, "连接尚未建立");
		}
	}

	public void registReader(Integer type, ReadListener r) {
		MapContext.getInstance().getResponseListenerMap().put(type, r);
	}

	public void unregistReader(Integer type) {
		MapContext.getInstance().getResponseListenerMap().remove(type);
	}

	public void sendHeartbeat() {
		if (mSocketAsyncTask != null)
			mSocketAsyncTask.sendHeartbeat();
	}
}
