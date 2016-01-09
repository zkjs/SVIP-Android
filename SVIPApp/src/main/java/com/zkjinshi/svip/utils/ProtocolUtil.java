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

    public static int MY_SOCKET_TIMEOUT_MS = 3000; //超时时间
    public static int DEFAULT_MAX_RETRIES = 0; //增加重试次数
    public static float DEFAULT_BACKOFF_MULT = 1f;//它允许你指定一个退避乘数可以用来实现“指数退避”来从RESTful服务器请求数据。

    /**
     * 获取客服列表 URL
     * @return
     */
    public static String getCustomerServiceUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/mysemplist";
    }

    /**
     * 获取订单详细信息
     * @return
     */
    public static String getOrderDetailUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"order/v10show";
    }

    /**
     * 获取订单评价 URL
     * @return
     */
    public static String getGetEvaluateUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"comment/show";
    }


    /**
     * 新增订单评价 URL
     * @return
     */
    public static String getAddEvaluateUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/evaluation";
    }

    /**
     * 获取用户id请求URL
     * @param token
     * @param userId
     * @return
     */
    public static String getUserInfoUrl(String token,String userId){
        return ConfigUtil.getInst().getPhpDomain()+"user/select?userid="+userId+"&token="+token;
    }

    /**
     * 获取商家logo URL
     * @param logo
     * @return
     */
    public static String getShopLogoUrl(String logo){
        return ConfigUtil.getInst().getPhpDomain()+logo;
    }

    /**
     * 获取商品列表 URL
     * @param shopId
     * @return
     */
    public static String getGoodListUrl(String shopId){
        return ConfigUtil.getInst().getJavaDomain()+"goods/get/"+shopId;
    }

    /**
     * 获得商品图片
     * @param imageUrl
     * @return
     */
    public static String getGoodImgUrl(String imageUrl){
        return ConfigUtil.getInst().getPhpDomain()+imageUrl;
    }

    /**
     * 获取Ibeancon列表
     * @return
     */
    public static String getIBeaconListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"beacon/show";
    }

    /**
     * 获取区域信息列表
     * @return
     */
    public static String getLocationListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/location";
    }

    /**
     * 添加个人gps信息
     * @return
     */
    public static String getAddGpdInfoUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"user/gpsadd";
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
     * 获取用户订单
     * @return
     */
    public static String getOrderUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"user/order";
    }

    /**
     * 获取商家信息
     * @param shopid
     * @return
     */
    public static String getShopInfoUrl(String shopid){
        return ConfigUtil.getInst().getPhpDomain()+"user/selectshop?shopid="+shopid+"&web=0";
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
     * 获取单个订单
     * @return
     */
    public static String getOneOrderUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"order/show";
        // return "http://172.21.7.54/order/show";
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
     * 个人获取最新订单
     * @return
     */
    public static String getLastOrder(){
        return ConfigUtil.getInst().getPhpDomain()+"order/last";
    }

    /**
     * 个人获取全部订单列表
     * @return
     */
    public static String getOrderList(){
        return ConfigUtil.getInst().getPhpDomain()+"order/showlist";
    }

    /**
     * 获取我的专属客服
     * @return
     */
    public static String getUserMysemp(){
        return ConfigUtil.getInst().getPhpDomain()+"user/mysemp";
    }

    /**
     * POST 服务员随机获取一个邀请码
     * @return
     */
    public static String getNewRandomInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/random";
    }

    /**
     * POST 服务员查看我的邀请码
     * @return
     */
    public static String getEmpInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/sempcode";
    }

    /**
     * POST 客户根据邀请码查询服务员
     * @return
     */
    public static String getEmpByInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/getcode";
    }

    /**
     * POST 超级身份输入邀请码动作
     * @return
     */
    public static String getUserBindInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/bdcode";
    }

    /**
     * 获得用户头像
     * @param userid
     * @return
     */
    public static String getAvatarUrl(String userid){
        return ConfigUtil.getInst().getPhpDomain()+"uploads/users/"+userid+".jpg";
    }

    /**
     * 获得商家logo
     * @param shopid
     * @return
     */
    public static String getShopIconUrl(String shopid){
        return ConfigUtil.getInst().getPhpDomain()+"uploads/shops/"+shopid+".png";
    }

    /**
     * V0.9.1 订单列表带评论状态
     * @return
     */
    public static String getOrderV10list(){
        return ConfigUtil.getInst().getPhpDomain()+"order/v10list";
    }

    /**
     * 固定链接获取推送广告
     * @return
     */
    public static String getAdPush(){
        return ConfigUtil.getInst().getPhpDomain()+"ad/push";
    }

    /**
     * 获取广告列表
     * @return
     */
    public static String getAdList(){
        return ConfigUtil.getInst().getPhpDomain()+"ad/list";
    }

    /**
     * 获取商家列表 new 带行业分类和扩展属性的
     *
     *page  翻页 页数 默认每页10条
     *type  类型 酒店1, 休闲2, 餐饮3  后期会有接口列表
     *
     * @return
     */
    public static String getShopList(int page,int type){
        return ConfigUtil.getInst().getPhpDomain()+"shop/list?page="+page+"&type="+type;
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
     * 退出appHttp连接
     * @return
     */
    public static String getLogoutUrl(String userID) {
        return ConfigUtil.getInst().getPhpDomain()+"user/logout?userid=" + userID;
    }

    /**
     * 获取用户信息
     * @return
     */
    public static String getUserInfoUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"v10/user";
    }

    /**
     * 环信添加/删除/显示好友
     * @return
     */
    public static String gethximFriendUrl(){
        return  ConfigUtil.getInst().getPhpDomain()+"hxim/friend";
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
     * 获取全部商家列表
     * @return
     */
    public static String getShopListUrl(int page, int pageSize) {
        return ConfigUtil.getInst().getJavaDomain()+"shop/list"+"/"+page+"/"+pageSize+"/";
    }

    /**
     * 根据城市名称获取商家列表
     * @return
     */
    public static String getShopListByCityUrl(String city, int page, int pageSize) {
        return  ConfigUtil.getInst().getJavaDomain()+"shop/list/"+city+"/"+page+"/"+pageSize+"/";
    }

    /**
     * 获取专属客服全部商家列表
     * @return
     */
    public static String getShopListUserUrl(String userID, int page, int pageSize) {
        return ConfigUtil.getInst().getJavaDomain()+"shop/list/user/"+userID+"/"+page+"/"+pageSize+"/";
    }

    /**
     * 根据城市名称获取我的专属客服商家列表
     * @return
     */
    public static String getShopListUserByCityUrl(String userID, String city, int page, int pageSize) {
        return  ConfigUtil.getInst().getJavaDomain()+"shop/list/user/"+userID+"/"+city+"/"+page+"/"+pageSize+"/";
    }

    /**
     * 根据城市名称获取推荐商家列表
     * @return
     */
    public static String getRecommendedShopListUrl(String city) {
        return  ConfigUtil.getInst().getJavaDomain()+"shop/recommended/" + city;
    }

    /**
     * 获取环信群成员
     * @return
     */
    public static String getGroupMemberUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"hxim/member";
    }

    /**
     * 获取个人信息更新url
     * @return
     */
    public static String getUserUploadUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/upload";
    }

    /**
     * 获取首页大图
     * @return
     */
    public static String getBigPicUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"firstpage/icons";
    }

    /**
     * 获取用户推送消息(用户未登陆)
     * @return
     */
    public static String getMessageDefaultUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"messages/default";
    }

    /**
     * 获取推荐商家列表
     * @return
     */
    public static String getShopRecommendedUrl(String city){
        return ConfigUtil.getInst().getJavaDomain()+"shop/recommended/"+city+"/";
    }


    public static String getArriveNoticeUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"arrive/notice";
    }

    /**
     * 获取合适分辨率的图片URL
     * @return
     */
    public static String getFitPicUrl(String path,String file){
        return path+"andriod/"+CacheUtil.getInstance().getBestFitPixel()+"/"+file;

    }

    /**
     * 获取用户订单状态消息
     * @return
     */
    public static String getOrderMsgUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"messages/orders";
    }

    /**
     * 获取用户订单状态消息
     * @return
     */
    public static String getMsgUrl(String userid,String city){
        return ConfigUtil.getInst().getJavaDomain()+"messages/"+userid+"/"+city;
    }

    /**
     * 根据手机号查询服务员
     * @return
     */
    public static String getServerByPhone(String phone){
        return ConfigUtil.getInst().getJavaDomain()+"user/getuser/"+phone+"/";
    }


    /**
     * 根据shopid 查询商家基本信息
     * @return
     */
    public static String getShopBaseInfoUrl(String shopid){
        return ConfigUtil.getInst().getJavaDomain()+"shop/getshop/"+shopid;
    }

    /**
     * 根据酒店区域获取用户特权
     * @return
     */
    public static String getUserPrivilegeUrl(String userid,String shopid){
        return ConfigUtil.getInst().getJavaDomain()+"user/privilege/"+userid+"/"+shopid;
    }

    /**
     * 获取商家评论列表url
     * @param shopID
     * @param page
     * @param pageSize
     * @return
     */
    public static String getShopCommentListUrl(String shopID, int page, int pageSize){
        return ConfigUtil.getInst().getJavaDomain()+"shop/evaluation/get/"+shopID+"/"+page+"/"+pageSize;
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
     * 获取未确认订单列表
     * @return
     */
    public static String appUpgradeUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"app/upgrade";
    }


    /**
     * 商家详情
     */
    public static String getShopDetailUrl(String shopId){
        return ConfigUtil.getInst().getJavaDomain()+"shop/get/"+shopId;
    }


}
