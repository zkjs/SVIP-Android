package com.zkjinshi.svip.emchat.observer;

import com.easemob.EMNotifierEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * 环信消息收发观订阅主题
 * 开发者：JimmyZhang
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMessageSubject {

    public static final String TAG = "EMessageSubject";

    public static EMessageSubject instance;
    private HashMap<EMNotifierEvent.Event, Vector<IEMessageObserver>> obsMap;
    private Vector<IEMessageObserver> obsVector;
    private EMNotifierEvent.Event eventType;
    private Iterator<Map.Entry<EMNotifierEvent.Event, Vector<IEMessageObserver>>> iterator;
    private Map.Entry<EMNotifierEvent.Event, Vector<IEMessageObserver>> entry;

    private EMessageSubject() { }

    public synchronized static EMessageSubject getInstance() {
        if (null == instance) {
            instance = new EMessageSubject();
            instance.init();
        }
        return instance;
    }

    private void init() {
        obsMap = new HashMap<EMNotifierEvent.Event, Vector<IEMessageObserver>>();
    }

    /**
     * 添加消息观察者
     *
     * @param observer
     * @param eventType
     */
    public void addObserver(IEMessageObserver observer, EMNotifierEvent.Event eventType) {
        if (obsMap.containsKey(eventType)) {
            obsVector = obsMap.get(eventType);
        } else {
            obsVector = new Vector<IEMessageObserver>();
        }
        if (!obsVector.contains(observer)) {
            obsVector.add(observer);
            obsMap.put(eventType, obsVector);
        }
    }

    /**
     * 移除消息观察者
     *
     * @param observer
     * @param eventType
     */
    public void removeObserver(IEMessageObserver observer, EMNotifierEvent.Event eventType) {
        if (obsMap.containsKey(eventType)) {
            obsVector = obsMap.get(eventType);
            if (null != obsVector && obsVector.contains(observer)) {
                obsVector.remove(observer);
                obsMap.put(eventType, obsVector);
            }
        }
    }

    /**
     * 移除消息观察者
     *
     * @param observer
     */
    public void removeObserver(IEMessageObserver observer) {
        iterator = obsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            obsVector = entry.getValue();
            eventType = entry.getKey();
            if (null != obsVector && obsVector.contains(observer)) {
                obsVector.remove(observer);
                obsMap.put(eventType, obsVector);
            }
        }
    }

    /**
     * 通知消息给所有观察者
     *
     * @param event
     */
    public void notifyObservers(EMNotifierEvent event) {
        eventType =  event.getEvent();
        if (obsMap.containsKey(eventType)) {
            obsVector = obsMap.get(eventType);
            if (!obsVector.isEmpty()) {
                for (IEMessageObserver observer : obsVector) {
                    observer.receive(event);
                }
            }
        }
    }
}
