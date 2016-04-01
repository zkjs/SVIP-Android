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
     * 拼接图片路径
     * @param apiUrl
     * @return
     */
    public static String getHostImgUrl(String apiUrl){
       return ConfigUtil.getInst().getImgDomain()+apiUrl;
    }

    /**
     * 推送/更新室内位置
     * @return
     */
    public static String lbsLocBeacon(){
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon";
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

}
