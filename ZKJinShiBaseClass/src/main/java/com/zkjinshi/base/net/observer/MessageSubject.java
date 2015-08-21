package com.zkjinshi.base.net.observer;

import android.util.Log;

import com.zkjinshi.base.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * 消息订阅主题
 * 开发者:JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageSubject {

    public static final String TAG = "MessageSubject";

    public static MessageSubject instance;
    private HashMap<Integer, Vector<IMessageObserver>> obsMap;
    private Vector<IMessageObserver> obsVector;
    private Integer msgType;
    private Iterator<Entry<Integer, Vector<IMessageObserver>>> iterator;
    private Entry<Integer, Vector<IMessageObserver>> entry;

    private MessageSubject() {
    }

    public synchronized static MessageSubject getInstance() {
        if (null == instance) {
            instance = new MessageSubject();
            instance.init();
        }
        return instance;
    }

    private void init() {
        obsMap = new HashMap<Integer, Vector<IMessageObserver>>();
    }

    /**
     * 添加消息观察者
     *
     * @param observer
     * @param msgType
     */
    public void addObserver(IMessageObserver observer, Integer msgType) {
        if (obsMap.containsKey(msgType)) {
            obsVector = obsMap.get(msgType);
        } else {
            obsVector = new Vector<IMessageObserver>();
        }
        if (!obsVector.contains(observer)) {
            obsVector.add(observer);
            obsMap.put(msgType, obsVector);
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-注册观察者key:" + msgType);
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-注册观察者Value:" + observer.getClass().getSimpleName());
    }

    /**
     * 移除消息观察者
     *
     * @param observer
     * @param msgType
     */
    public void removeObserver(IMessageObserver observer, Integer msgType) {
        if (obsMap.containsKey(msgType)) {
            obsVector = obsMap.get(msgType);
            if (null != obsVector && obsVector.contains(observer)) {
                obsVector.remove(observer);
                obsMap.put(msgType, obsVector);
            }
        }
    }

    /**
     * 移除消息观察者
     *
     * @param observer
     */
    public void removeObserver(IMessageObserver observer) {
        iterator = obsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            obsVector = entry.getValue();
            msgType = entry.getKey();
            if (null != obsVector && obsVector.contains(observer)) {
                obsVector.remove(observer);
                obsMap.put(msgType, obsVector);
            }
        }
    }

    /**
     * 通知消息给所有观察者
     *
     * @param message
     */
    public void notifyObservers(String message) {
        try {
            JSONObject messageObj = new JSONObject(message);
            msgType = messageObj.getInt("type");
            if (obsMap.containsKey(msgType)) {
                obsVector = obsMap.get(msgType);
                if (!obsVector.isEmpty()) {
                    for (IMessageObserver observer : obsVector) {
                        observer.receive(message);
                        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-通知观察者key:" + msgType);
                        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-通知观察者Value:" + observer.getClass().getSimpleName());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
