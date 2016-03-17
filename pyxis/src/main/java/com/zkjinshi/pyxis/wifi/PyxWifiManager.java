package com.zkjinshi.pyxis.wifi;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 蓝牙管理器
 * 开发者：杜健德
 * 日期：2016/2/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PyxWifiManager {

    public static final String TAG = PyxWifiManager.class.getSimpleName();

    private PyxWifiManager(){}
    private static PyxWifiManager instance;

    public static synchronized PyxWifiManager getInstance(){
        if(null == instance){
            instance = new PyxWifiManager();
        }
        return instance;
    }



    public WifiInfo getWifiInfo(Context context){
        // 获得WifiManager
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
        {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            getBSSID() 获取BSSID属性
//            getDetailedStateOf() 获取客户端的连通性
//            getHiddenSSID() 获取SSID 是否被隐藏
//            getIpAddress() 获取IP 地址
//            getLinkSpeed() 获取连接的速度
//            getMacAddress() 获取Mac 地址
//            getRssi() 获取802.11n 网络的信号
//            getSSID() 获取SSID
//            getSupplicanState() 获取具体客户端状态的信息
            return wifiInfo;
        }
        return null;
    }

}
