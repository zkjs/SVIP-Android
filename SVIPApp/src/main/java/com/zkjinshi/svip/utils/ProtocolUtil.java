package com.zkjinshi.svip.utils;

import android.content.Context;
import android.text.TextUtils;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DisplayUtil;

/**
 * 协议接口工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {


    /**
     * 拼接图片路径
     * @param apiUrl
     * @return
     */
    public static String getHostImgUrl(String apiUrl){
       return ConfigUtil.getInst().getCdnDomain()+apiUrl;
    }


    /**
     * 根据尺寸获取图片路径
     * @param context
     * @param apiUrl
     * @param w
     * @param h
     * @return
     */
    public static String getImageUrlByScale(Context context, String apiUrl, int w, int h){
        w = DisplayUtil.dip2px(context,w);
        h = DisplayUtil.dip2px(context,h);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+w+"w_"+h+"h";
    }

    public static String getImageUrlByWidth(Context context, String apiUrl, int w){
        w = DisplayUtil.dip2px(context,w);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+w+"w";
    }

    public static String getImageUrlByHeight(Context context, String apiUrl, int h){
        h = DisplayUtil.dip2px(context,h);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+h+"h";
    }

    public static String getAvatarUrl(Context context, String apiUrl){
        return getImageUrlByScale(context,apiUrl,60,60);
    }

    /**
     * 推送/更新室内位置
     * @return
     */
    public static String lbsLocBeacon(){
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon";
    }

    /**
     * Beacon信息收集
     * @return
     */
    public static String lbsLocBeacons(){//beacon的经纬度和高度信息只在能获取到GPS位置信息时上传, 否则留空(即传0值)
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacons";
    }

    /**
     * 推送/更新室外位置
     * @return
     */
    public static String lbsLocGps(){
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/gps";
    }

    /**
     * 登录获取手机验证码
     * @return
     */
    public static String ssoVcodeLogin(){
        return ConfigUtil.getInst().getPavDomain()+"sso/vcode/v1/si?source=login";
    }

    /**
     * 注册获取手机验证码
     * @return
     */
    public static String ssoVcodeRegister(){
        return ConfigUtil.getInst().getPavDomain()+"sso/vcode/v1/si?source=register";
    }

    /**
     * 获取手机验证码
     * @return
     */
    public static String ssoToken(){
        return ConfigUtil.getInst().getPavDomain()+"sso/token/v1/phone/si";
    }

    /**
     * 获得刷新token Url
     * @return
     */
    public static String getTokenRefreshUrl(){
        return ConfigUtil.getInst().getPavDomain()+"sso/token/v1";
    }

    /**
     * 注册si用户
     * @return
     */
    public static String registerSi(){
        return ConfigUtil.getInst().getPavDomain()+"res/v1/register/si";
    }

    /**
     * 注册流程-更新si信息
     * @return
     */
    public static String registerUpdateSi(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/register/update/si";
    }

    /**
     * 登陆后更新用户资料
     * @return
     */
    public static String loginUpdateSi(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/update/user";
    }

    /**
     * 获取用户资料
     * @return
     */
    public static String querySiAll(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/si/all";
    }



    public static String getUserLogUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/upload/userlog";
    }

    /**
     * 获取用户信息
     * @param userIds
     * @return
     */
    public static String getUsersInfoUrl(String userIds){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/user/all?userids="+userIds;
    }

    /**
     * 查询账户余额(仅限FACEPAY用户) [GET]
     * @return
     */
    public static String getAccount(){
        return  ConfigUtil.getInst().getForDomain()+"res/v1/payment/balance";
    }

    /**
     * 获取收付款列表,含明细
     * @return
     */
    public static String getPayList(String status,int page){
        return ConfigUtil.getInst().getForDomain()+"res/v1/payment/si?status="+status+"&page="+page+"&page_size=10";
    }


    /**
     * 用户确认/拒绝付款
     * @return
     */
    public static String payment(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/payment";
    }

    /**
     * 获取商家详情url
     * @param shopId
     * @return
     */
    public static String getShopDetailUrl(String shopId){
        if(TextUtils.isEmpty(shopId)){
            return ConfigUtil.getInst().getForDomain()+"res/v1/shop/detail";
        }else {
            return ConfigUtil.getInst().getForDomain()+"res/v1/shop/detail/"+shopId;
        }
    }

    /**
     * 我的商家
     * @return
     */
    public static String shopBelong(int page,int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/belong/si?page="+page+"&page_size="+pageSize;
    }

    /**
     * 获取升级版本信息
     * @param apptype 1 (int required) - 应用类型： 1 超级身份 2 超级服务 3.静享 4.超级锁屏 5.手势游戏
     * @param devicetype IOS (String required) - 设备类型：IOS ANDROID AND_PAD
     * @param verno 1.0.0.1 (String required) - 当前客户端版本号
     * @return
     */
    public static String upgradeNewestVersion(int apptype,String devicetype,String verno){
        return ConfigUtil.getInst().getForDomain()+"/res/v1/systempub/upgrade/newestversion/"+apptype+"/"+devicetype+"/"+verno;
    }

    /**
     * 获取呼叫服务列表
     * @return
     */
    public static String servicetag(String locid){
        return ConfigUtil.getInst().getForDomain()+"res/v1/call/servicetag?locid="+locid;
    }

    /**
     * 超级身份创建呼叫服务
     * @return
     */
    public static String serviceTaskCreate(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/call/service/task";
    }

    /**
     * 获取呼叫服务任务列表
     * @return
     */
    public static String serviceTaskList(int page){
        return ConfigUtil.getInst().getForDomain()+"res/v1/call/service/task?page="+page+"&page_size=10";
    }

    /**
     * 获取当前商家Beacon列表
     * @return
     */
    public static String getZoneList(String shopid,int page){
        return ConfigUtil.getInst().getForDomain()+"lbs/v1/loc/beacon?shopid="+shopid+"&page="+page+"&page_size=10";
    }

}
