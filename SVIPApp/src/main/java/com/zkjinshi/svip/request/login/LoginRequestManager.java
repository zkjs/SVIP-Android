package com.zkjinshi.svip.request.login;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.net.util.ImCacheUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgClientLogin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录请求管理器
 * 开发者：JimmyZhang
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginRequestManager implements IMessageObserver {

    private static LoginRequestManager instance;

    private LoginRequestManager() {
    }

    public synchronized static LoginRequestManager getInstance() {
        if (null == instance) {
            instance = new LoginRequestManager();
        }
        return instance;
    }

    public LoginRequestManager init() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ClientLogin_RSP);
        return this;
    }

    /**
     * 发送登录请求
     * @param webSocketClient
     */
    public void sendLoginRequest(WebSocketClient webSocketClient) {
        MsgClientLogin msgClientLogin = LoginRequestTool.buildLoginRequest();
        webSocketClient.sendMessage(new Gson().toJson(msgClientLogin));
    }

    @Override
    public void receive(String message) {
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");
            if (ProtocolMSG.MSG_ClientLogin_RSP == type) {
                ImCacheUtil.getInstance().setIMLogin(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
