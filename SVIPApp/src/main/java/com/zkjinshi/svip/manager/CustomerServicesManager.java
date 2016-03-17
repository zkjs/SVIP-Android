package com.zkjinshi.svip.manager;

import android.content.Context;

import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 客服信息管理器
 * 开发者：JimmyZhang
 * 日期：2015/11/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomerServicesManager {

    private CustomerServicesManager(){}

    private static CustomerServicesManager instance;

    public synchronized static CustomerServicesManager getInstance(){
        if(null == instance){
            instance = new CustomerServicesManager();
        }
        return instance;
    }

    /**
     * 获取专属客服
     * @param shopId
     * @param netRequestListener
     */
    public void requestServiceTask(Context context, String shopId, ExtNetRequestListener netRequestListener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getCustomerServiceUrl(shopId));
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.setNetRequestListener(netRequestListener);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}
