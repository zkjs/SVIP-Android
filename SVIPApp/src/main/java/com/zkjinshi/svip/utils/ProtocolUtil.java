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
     * 获取客服列表 URL
     * @return
     */
    public static String getCustomerServiceUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/mysemplist";
    }

    public static String getCustomerServiceUrl(String shopId){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/salebelongsi/"+shopId;
    }

    /**
     * 新增订单评价 URL
     * @return
     */
    public static String getAddEvaluateUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/evaluation";
    }

    /**
     * 根据手机号查询服务员
     * @return
     */
    public static String getServerByPhone(String phone){
        return ConfigUtil.getInst().getJavaDomain()+"user/getuser/"+phone+"/";
    }

    /**
     * 获取用户已有标签
     * @param token
     * @param userId
     * @return
     */
    public static String getUserTagsUrl(String token,String userId){
        return ConfigUtil.getInst().getPhpDomain()+"tags/user?userid="+userId+"&token="+token;
    }

    /**
     * 获取随机标签池 每次请求8条
     * @return
     */
    public static String getRandTagsUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"tags/show";
    }

    /**
     * 提交用户选择标签
     * @return
     */
    public static String getPostTagsUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"tags/upload";
    }

    /**
     * 获取用户发票列表
     * @return
     */
    public static String geTicketListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/fplist";
    }

    /**
     * 添加用户发票列表
     * @return
     */
    public static String addTicketUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/fpadd";
    }

    /**
     * 修改/删除个人发票
     * @return
     */
    public static String updateTicketUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/fpupdate";
    }

    /**
     * 确认/修改订单
     * @return
     */
    public static String updateOrderUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"order/update2";
    }

    /**
     * 获取用户常用入住人列表
     * @return
     */
    public static String getPeopleListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"order/createlist";
    }

    /**
     * 用户关联人信息新增
     * @return
     */
    public static String addPeopleListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"order/useradd";
    }

    /**
     * 获取我的专属客服
     * @return
     */
    public static String getUserMysemp(){
        return ConfigUtil.getInst().getPhpDomain()+"user/mysemp";
    }

    /**
     * 请求Charge 支付凭据 发起支付
     * @return
     */
    public static String getPingPayUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"ping/paycharge";
    }

    /**
     * 搜索商家信息
     * @return
     */
    public static String getUserSearch(){
        return ConfigUtil.getInst().getPhpDomain()+"user/search";
    }

    /**
     * 自建服务器好友维护(添加/删除/显示)
     * @return
     */
    public static String getUserFriendUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"user/friend";
    }

    /**
     * 获取销售详情信息
     * @return
     */
    public static String getSalesUrl() {
        return  ConfigUtil.getInst().getPhpDomain()+"user/getsales";
    }

    /**
     * 获取城市列表
     * @return
     */
    public static String getCityListUrl() {
        return ConfigUtil.getInst().getPhpDomain()+"arrive/citylist";
    }

    /**
     * 获取环信群成员
     * @return
     */
    public static String getGroupMemberUrl(String groupId){
        return ConfigUtil.getInst().getForDomain()+"res/v1/im/members/"+groupId;
    }

    /**
     * 获取个人信息更新url
     * @return
     */
    public static String getUserUploadUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/upload";
    }

    /**
     * 根据酒店区域获取用户特权
     * @return
     */
    public static String getUserPrivilegeUrl(String userid,String shopid){
        return ConfigUtil.getInst().getJavaDomain()+"user/privilege/get/"+userid+"/"+shopid;
    }

    /**
     * 获取商家评论列表url
     * @param shopID
     * @param page
     * @param pageSize
     * @return
     */
    public static String getShopCommentListUrl(String shopID, int page, int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/comments/"+shopID+"?page="+page+"&page_size"+pageSize;
    }

    /**
     * 订单新增
     * @return
     */
    public static String orderAddUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/add";
    }

    /**
     * 获取订单详情
     * @return
     */
    public static String orderGetUrl(String orderno){
        return ConfigUtil.getInst().getJavaDomain()+"order/get/"+orderno;
    }

    /**
     * 订单支付
     * @return
     */
    public static String orderPayUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/pay";
    }

    /**
     * 订单确认
     * @return
     */
    public static String orderConfirmUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/confirm";
    }

    /**
     * 取消订单
     * @return
     */
    public static String orderCancelUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/cancel";
    }

    /**
     * 获取订单列表
     * @return
     */
    public static String orderListUrl(String userid,int page,int size){
        return ConfigUtil.getInst().getJavaDomain()+"order/list/"+userid+"/"+page+"/"+size;
    }

    /**
     * 获取未确认订单列表
     * @return
     */
    public static String orderGetUnconfirmedUrl(String userid,int page,int size){
        return ConfigUtil.getInst().getJavaDomain()+"order/get/unconfirmed/"+userid+"/"+page+"/"+size;
    }


    /**
     * 商家详情
     */
    public static String getShopDetailUrl(String shopId){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/detail/"+shopId;
    }

    /**
     * 用户添加反馈
     */
    public static String feedbackAdd(){
        return ConfigUtil.getInst().getPhpDomain()+"feedback/add";
    }

    /**
     * 客人发起绑定服务员
     */
    public static String addfuser(){
        return ConfigUtil.getInst().getPhpDomain()+"user/addfuser";
    }

    /**
     * 拼接图片路径
     * @param apiUrl
     * @return
     */
    public static String getHostImgUrl(String apiUrl){
       return ConfigUtil.getInst().getImgDomain()+apiUrl;
    }

    /**
     * 获得用户头像
     * @param userid
     * @return
     */
    public static String getAvatarUrl(String userid){
        return ConfigUtil.getInst().getImgDomain()+"uploads/users/"+userid+".jpg";
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
        return ConfigUtil.getInst().getPavDomain()+"sso/vcode/v1/si?source=login&dist=568e8db288b8a95d7ecfeb9a5d6936b9c521253f3cad30cd9b83ed2d87db9605";
    }

    /**
     * 注册获取手机验证码
     * @return
     */
    public static String ssoVcodeRegister(){
        return ConfigUtil.getInst().getPavDomain()+"sso/vcode/v1/si?source=register&dist=568e8db288b8a95d7ecfeb9a5d6936b9c521253f3cad30cd9b83ed2d87db9605";
    }

    /**
     * 获取手机验证码
     * @return
     */
    public static String ssoToken(){
        return ConfigUtil.getInst().getPavDomain()+"sso/token/v1/phone/si?dist=568e8db288b8a95d7ecfeb9a5d6936b9c521253f3cad30cd9b83ed2d87db9605";
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
        return ConfigUtil.getInst().getPavDomain()+"res/v1/register/si?dist=568e8db288b8a95d7ecfeb9a5d6936b9c521253f3cad30cd9b83ed2d87db9605";
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

    /**
     * 获取所有商家列表
     * @param page
     * @param pageSize
     * @return
     */
    public static String getShopList(int page,int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop?page="+page+"&page_size="+pageSize;
    }


    /**
     * 获取首页大图
     * @return
     */
    public static String getHomeBigPic(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/systempub/homepicture";
    }

    /**
     * 获取升级版本信息
     * @param apptype
     * @param devicetype
     * @param verno
     * @return
     */
    public static String newestversion(String apptype,String devicetype,String verno){
        return ConfigUtil.getInst().getForDomain()+"res/v1/systempub/upgrade/newestversion/"+apptype+"/"+devicetype+"/"+verno;
    }


    /**
     * 获取本地服务推荐商家
     * @param city
     * @return
     */
    public static String getDefaultMsg(String city){
        return ConfigUtil.getInst().getForDomain()+"res/v1/systempub/localservice/recommend/"+city;
    }

    /**
     * 登录后获取首页详情-订单-特权
     * @param city
     * @return
     */
    public static String getLoginMsg(String city){
        return ConfigUtil.getInst().getForDomain()+"res/v1/system/sihomedetail/"+city;
    }

    /**
     * 根据城市获取商家
     * @param city
     * @param page
     * @param pageSize
     * @return
     */
    public static String getShopListByCity(String city,int page,int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/"+city+"?page="+page+"&page_size="+pageSize;
    }

    /**
     * 获取商家的商品
     * @param shopid
     * @param page
     * @param pageSize
     * @return
     */
    public static String getGoodListByCity(String shopid,int page,int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/goods/"+shopid+"?page="+page+"&page_size="+pageSize;
    }


    public static String getUserLogUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/upload/userlog";
    }

    /**
     * 根据邀请码获取销售信息
     * @return
     */
    public static String getSaleByCodeUrl(String salecode){
        return ConfigUtil.getInst().getForDomain()+"res/v1/salecode/saleuser?salecode="+salecode;
    }

    /**
     * 激活邀请码
     * @return
     */
    public static String getActivateSaleCodeUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/salecode/active/salecode";
    }

    /**
     * 获取用户信息
     * @param userIds
     * @return
     */
    public static String getUsersInfoUrl(String userIds){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/user/all?userids="+userIds;
    }

}
