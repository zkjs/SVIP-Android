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

    //是否开启短信验证
    public static final boolean SMS_CHECK_ENABLE = false;
    //是否需要密码引导页
    public static final boolean NEED_PASSWORD_GUIDE = true;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_FROM_NAME = "fromName";
    public static final String EXTRA_TO_NAME = "toName";
    public static final String EXTRA_SHOP_ID = "shopId";
    public static final String EXTRA_SHOP_NAME = "shopName";
    public static final String MSG_TXT_EXT_TYPE= "extType";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

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

    public final static String HTTP_URL = ConfigUtil.getInst().getPhpDomain();
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
    //app客户查询手机是否注册过
    public final static String POST_GET_PHONE_URL  = HTTP_URL + "user/getphone?";
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

    //修改姓名
    public static final int FLAG_MODIFY_REAL_NAME = 8;

    //修改昵称
    public static final int FLAG_MODIFY_USER_NAME = 9;

    //修改公司或单位
    public static final int FLAG_MODIFY_REMARK = 10;

    //修改邮箱
    public static final int FLAG_MODIFY_EMAIL = 11;

    public static final String UPDATE_UNREAD_RECEIVER_ACTION = "com.zkjinshi.svip.UPDATE_UNREAD_RECEIVER_ACTION";
    public static final String SHOW_CONTACT_RECEIVER_ACTION = "com.zkjinshi.svip.SHOW_CONTACT_RECEIVER_ACTION";
    public static final String SHOW_CONTACT_F_RECEIVER_ACTION = "com.zkjinshi.svip.SHOW_CONTACT_F_RECEIVER_ACTION";
}
