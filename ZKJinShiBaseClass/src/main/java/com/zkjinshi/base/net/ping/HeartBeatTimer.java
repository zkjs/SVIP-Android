package com.zkjinshi.base.net.ping;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.net.queue.QueueContext;
import com.zkjinshi.base.net.util.ImCacheUtil;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.NetWorkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 心跳定时器
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HeartBeatTimer implements IMessageObserver {

    public static final String TAG = "HeartBeatTimer";
    private static HeartBeatTimer instance;
    private Timer heartbeatTimer;
    private long ACTIVE_TIME = 60 * 1000;// 发送心跳时间间隔
    private boolean isActive = true;
    private PingRequestVo pingRequestVo;

    private HeartBeatTimer() {
    }

    public synchronized static HeartBeatTimer getInstance() {
        if (null == instance) {
            instance = new HeartBeatTimer();
        }
        return instance;
    }

    /**
     * 初始化心跳服务
     */
    public HeartBeatTimer init() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_Ping);
        return this;
    }

    /**
     * 启动心跳包服务
     */
    public void startHeartbeat() {
        if (null != heartbeatTimer) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
        heartbeatTimer = new Timer();
        heartbeatTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (ImCacheUtil.getInstance().isIMLogin()) {
                    if (isActive) {
                        sendPingMessage();
                        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-开始发送心跳包");
                        isActive = false;
                    } else {
                        try {
                            WebSocketManager.getInstance().close();
                            WebSocketManager.getInstance().initClient();
                            isActive = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, ACTIVE_TIME, ACTIVE_TIME);
    }

    /**
     * 停止心跳服务
     */
    public void stopHeartbeat() {
        if (null != heartbeatTimer) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
    }

    @Override
    public void receive(String message) {
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");
            if (ProtocolMSG.MSG_Ping == type) {
                Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-收到响应心跳包");
                isActive = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendPingMessage() {
        pingRequestVo = new PingRequestVo();
        pingRequestVo.setType(ProtocolMSG.MSG_Ping);
        pingRequestVo.setTimestamp(System.currentTimeMillis());
        QueueContext.getInstance().getFirstRequestQueue().add(new Gson().toJson(pingRequestVo));
    }

}
