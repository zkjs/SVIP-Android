package com.zkjinshi.svip.utils;

import com.zkjinshi.base.config.ConfigUtil;

/**
 * 配置工具类
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Constants {

    //应用唯一标识，在微信开放平台提交应用审核通过后获得
    public static final String WX_APP_ID = "wxe09e14fcb69825cc";
    //应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
    public static final String WX_APP_SECRE = "8b6355edfcedb88defa7fae31056a3f0";
    public static final String WX_APP_SCOPE = "snsapi_base";
    public static final String WX_APP_STATE = "svip_wx_login";
    //开发者在QQ互联申请的APP ID
    public static final String QQ_APP_ID = "100424468";
    //开发者在QQ互联申请的APP kEY
    public static final String QQ_APP_KEY = "c7394704798a158208a74ab60104f0ba";
    //友盟Appkey
    public static final String UMAppKey = "55c31431e0f55a65c1002597";


    public final static String DISPLAY_FOR_IMAGE = "[图片]";
    public final static String DISPLAY_FOR_MIX   = "[图文消息]";
    public final static String DISPLAY_FOR_AUDIO = "[语音]";
    public final static String DISPLAY_FOR_ERROR = "[未知消息]";

    public final static int UPDATE_ORDER = 0;
    public final static int QUREY_ORDER  = 1;

    public final static int POST_SUCCESS = 1;
    public final static int POST_FAILED  = 0;

    //协议包返回结果
    public final static int PROTOCAL_SUCCESS = 0;
    public final static int PROTOCAL_FAILED  = 1;

    public final static String FORMAT_PNG  = ".png";
    public final static String FORMAT_JPG  = ".jpg";

    public final static String HTTP_URL = ConfigUtil.getInst().getHttpDomain();
    //app客户注册登录
    public final static String POST_LOGIN_URL     = HTTP_URL + "user/reg?";
    //app客户获取用户
    public final static String POST_GET_USER_URL  = HTTP_URL + "user/getuser?";
    //app用户查询用户详细信息
    public final static String GET_USER_DETAIL_URL = HTTP_URL + "user/select?";
    //获取用户历史账单
    public final static String GET_HISTORY_ORDERS = HTTP_URL + "user/recirdst?";
    //1. 获取用户所有订单
    //2. 用户修改订单 set=0 修改订单(取消订单)
    public final static String POST_USER_ORDERS   = HTTP_URL + "user/order?";
    //用户头像 uploads/users/userid.jpg
    public final static String GET_USER_AVATAR    = HTTP_URL + "uploads/users/";
    //商家logo uploads/shops/shopid.png
    public final static String GET_SHOP_LOGO      = HTTP_URL + "uploads/shops/";

    //获取商家列表
    public final static String GET_SHOP_LIST = HTTP_URL + "user/selectshop?web=0&page=1";

    //用户修改用户资料
    public final static String MODIFY_USER_INFO_METHOD = "user/upload";

    //获取商家列表
    public final static String GET_GOOD_LIST ="user/goods";

    //商品预订
    public final static String POST_BOOK_MSG = HTTP_URL + "user/postvipre";

    //选择图片
    public static final int FLAG_CHOOSE_IMG = 5;

    //拍照
    public static final int FLAG_CHOOSE_PHOTO = 6;

    //修改完成
    public static final int FLAG_MODIFY_FINISH = 7;

}
