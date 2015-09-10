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
     * 获取区域信息列表
     * @return
     */
    public static String getLocationListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/location";
    }

    /**
     * 添加个人gps信息
     * @return
     */
    public static String getAddGpdInfoUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"user/gpsadd";
    }

    /**
     * 获取用户已有标签
     * @param token
     * @param userId
     * @return
     */
    public static String getUserTagsUrl(String token,String userId){
        return ConfigUtil.getInst().getHttpDomain()+"tags/user?userid="+userId+"&token="+token;
    }

    /**
     * 获取随机标签池 每次请求8条
     * @return
     */
    public static String getRandTagsUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"tags/show";
    }

    /**
     * 提交用户选择标签
     * @return
     */
    public static String getPostTagsUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"tags/upload";
    }

    /**
     * 获取用户订单
     * @return
     */
    public static String getOrderUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"user/order";
    }

    /**
     * 获取商家信息
     * @param shopid
     * @return
     */
    public static String getShopInfoUrl(String shopid){
        return ConfigUtil.getInst().getHttpDomain()+"user/selectshop?shopid="+shopid+"&web=0";
    }

    /**
     * 获取用户发票列表
     * @return
     */
    public static String geTicketListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/fplist";
    }

    /**
     * 添加用户发票列表
     * @return
     */
    public static String addTicketUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/fpadd";
    }

    /**
     * 修改/删除个人发票
     * @return
     */
    public static String updateTicketUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/fpupdate";
    }

    /**
     * 获取单个订单
     * @return
     */
    public static String getOneOrderUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"order/show";
       // return "http://172.21.7.54/order/show";
    }

    /**
     * 确认/修改订单
     * @return
     */
    public static String updateOrderUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"order/update";
    }

    /**
     * 获取用户常用入住人列表
     * @return
     */
    public static String getPeopleListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"order/createlist";
    }

    /**
     * 用户关联人信息新增
     * @return
     */
    public static String addPeopleListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"order/useradd";
    }

    /**
     * 个人获取最新订单
     * @return
     */
    public static String getLastOrder(){
        return ConfigUtil.getInst().getHttpDomain()+"order/last";
    }

}
