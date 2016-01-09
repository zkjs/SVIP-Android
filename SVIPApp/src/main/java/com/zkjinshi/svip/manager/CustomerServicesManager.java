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
     * 获取客服列表
     * @param shopId
     * @param netRequestListener
     */
    public void requestServiceListTask(Context context, String shopId, ExtNetRequestListener netRequestListener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getCustomerServiceUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid",shopId);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.setNetRequestListener(netRequestListener);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 获取专属客服
     * @param customerServiceList
     * @param salesId
     * @return
     */
    public CustomerServiceBean getExclusiveCustomerService(ArrayList<CustomerServiceBean> customerServiceList, String salesId){
        CustomerServiceBean customerService = null;
        String mSalesId = null;
        for(CustomerServiceBean customerServiceBean : customerServiceList){
            mSalesId = customerServiceBean.getSalesid();
            if(mSalesId.equals(salesId)){
                customerService = customerServiceBean;
            }
        }
        return customerService;
    }

    /**
     * 获取随机客服
     * @param customerServiceList
     * @return
     */
    public CustomerServiceBean getRandomCustomerService(ArrayList<CustomerServiceBean> customerServiceList){
        Random random = new Random();
        int size = customerServiceList.size();
        CustomerServiceBean customerService = customerServiceList.get(random.nextInt(size));
        return customerService;
    }

    /**
     * 获得随机管理员商家
     */
    public CustomerServiceBean getRandomAdminService(ArrayList<CustomerServiceBean> customerServiceList){
        ArrayList<CustomerServiceBean> adminServiceList = new ArrayList<CustomerServiceBean>();
        int roleType = 0;
        for(CustomerServiceBean customerServiceBean : customerServiceList){
            roleType = customerServiceBean.getRoleid();
            if(roleType == 1){
                adminServiceList.add(customerServiceBean);
            }
        }
        Random random = new Random();
        int size = adminServiceList.size();
        CustomerServiceBean customerService = adminServiceList.get(random.nextInt(size));
        return customerService;
    }
}
