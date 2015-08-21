package com.zkjinshi.svip.utils;

import com.zkjinshi.base.config.ConfigUtil;

/**
 * 协议接口工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {

    /**
     * 获取用户id请求URL
     * @param token
     * @param userId
     * @return
     */
    public static String getUserInfoUrl(String token,String userId){
        return ConfigUtil.getInst().getHttpDomain()+"user/select?userid="+userId+"&token="+token;
    }

    /**
     * 获取商家logo URL
     * @param logo
     * @return
     */
    public static String getShopLogoUrl(String logo){
        return ConfigUtil.getInst().getHttpDomain()+logo;
    }

    /**
     * 获取商品列表 URL
     * @param shopId
     * @return
     */
    public static String getGoodListUrl(String shopId){
        return ConfigUtil.getInst().getHttpDomain()+Constants.GET_GOOD_LIST+"?shopid="+shopId+"&page=1";
    }

    /**
     * 获得商品图片
     * @param imageUrl
     * @return
     */
    public static String getGoodImgUrl(String imageUrl){
        return ConfigUtil.getInst().getHttpDomain()+imageUrl;
    }

    /**
     * 获取Ibeancon列表
     * @return
     */
    public static String getIBeaconListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"beacon/show";
    }

    /**
     * 添加个人gps信息
     * @return
     */
    public static String getAddGpdInfoUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"user/gpsadd";
    }
}
