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

    //是否需要密码引导页
    public static final boolean NEED_PASSWORD_GUIDE = true;

    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_FROM_NAME = "fromName";
    public static final String EXTRA_TO_NAME = "toName";
    public static final String EXTRA_SHOP_ID = "shopId";
    public static final String EXTRA_SHOP_NAME = "shopName";
    public static final String MSG_TXT_EXT_TYPE= "extType";

    //应用唯一标识，在微信开放平台提交应用审核通过后获得
    public static final String WX_APP_ID = "wxe09e14fcb69825cc";
    //应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
    public static final String WX_APP_SECRE = "8b6355edfcedb88defa7fae31056a3f0";

    //选择图片
    public static final int FLAG_CHOOSE_IMG = 5;

    //拍照
    public static final int FLAG_CHOOSE_PHOTO = 6;

    //修改完成
    public static final int FLAG_MODIFY_FINISH = 7;

    //修改姓名
    public static final int FLAG_MODIFY_REAL_NAME = 8;
    //修改邮箱
    public static final int FLAG_MODIFY_EMAIL = 11;

    public static final String UPDATE_UNREAD_RECEIVER_ACTION = "com.zkjinshi.svip.UPDATE_UNREAD_RECEIVER_ACTION";
    public static final String SHOW_CONTACT_RECEIVER_ACTION = "com.zkjinshi.svip.SHOW_CONTACT_RECEIVER_ACTION";
    public static final String SHOW_CONTACT_F_RECEIVER_ACTION = "com.zkjinshi.svip.SHOW_CONTACT_F_RECEIVER_ACTION";
    public static final String SHOW_IBEACON_PUSH_MSG_RECEIVER_ACTION = "com.zkjinshi.svip.SHOW_IBEACON_PUSH_MSG_ACTION";

    public static final int OVERTIMEOUT = 5000;
}
