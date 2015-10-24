package com.zkjinshi.svip.request.login;

import android.text.TextUtils;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.svip.bean.jsonbean.MsgClientLogin;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.LoginType;

/**
 * 登录请求帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginRequestTool {

    /**
     * 构建协议登录对象
     * @return
     */
    public static MsgClientLogin buildLoginRequest() {
        String userID   = CacheUtil.getInstance().getUserId();
        String userName = CacheUtil.getInstance().getUserName();

        MsgClientLogin msgClientLogin = new MsgClientLogin();
        msgClientLogin.setType(ProtocolMSG.MSG_ClientLogin);
        msgClientLogin.setTimestamp(System.currentTimeMillis());
        if(!TextUtils.isEmpty(userID)){
            msgClientLogin.setId(userID);
        }
        if(!TextUtils.isEmpty(userName)){
            msgClientLogin.setName(userName);
        }
        msgClientLogin.setLogintype(LoginType.appUser.getVlaue());
        msgClientLogin.setVersion("");
        msgClientLogin.setPlatform("A");
        msgClientLogin.setAppid("");
        return msgClientLogin;
    }
}
