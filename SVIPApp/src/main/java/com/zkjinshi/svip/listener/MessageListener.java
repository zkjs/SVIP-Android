package com.zkjinshi.svip.listener;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.IMessageListener;
import com.zkjinshi.base.net.core.MessageReceiver;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.request.login.LoginRequestManager;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.vo.MessageVo;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListener implements IMessageListener {

    public static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onNetReceiveSucceed(String message) {
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            /** 文本消息处理 */
            if (ProtocolMSG.MSG_CustomerServiceTextChat == type) {
                if(gson == null){
                    gson = new Gson();
                }
                LogUtil.getInstance().info(LogLevel.INFO, "接收到文本消息:");
                MsgCustomerServiceTextChat msgText = gson.fromJson(message,
                                         MsgCustomerServiceTextChat.class);
                /** 处理消息入库 （数据库相关操作） */
                long resultCount = MessageDBUtil.getInstance().addMessage(msgText);
                if(resultCount > 0){
                    MessageVo messageVo = MessageFactory.getInstance().buildMessageVoByMsgText(msgText);
                    String shopID = messageVo.getShopId();
                    if(!TextUtils.isEmpty(shopID)) {
                        if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                            ChatRoomDBUtil.getInstance().addChatRoom(messageVo);
                        } else {
                            ChatRoomDBUtil.getInstance().updateChatRoom(messageVo);
                        }
                    }
                    /** 静默处理 */
                    EventBus.getDefault().post(messageVo);
                }
            }

            /** 音频消息处理 */
            if(ProtocolMSG.MSG_CustomerServiceMediaChat == type){
                if(gson == null)
                    gson = new Gson();
                LogUtil.getInstance().info(LogLevel.INFO, "接收到音频消息:");
                MsgCustomerServiceMediaChat msgMediaChat = gson.fromJson(
                              message, MsgCustomerServiceMediaChat.class);

                /** 保存音频对象，并获得路径 */
                String mediaName = msgMediaChat.getFilename();
                if(TextUtils.isEmpty(mediaName)){
                    mediaName = System.currentTimeMillis() + ".aac";
                }
                String audioPath   = FileUtil.getInstance().getAudioPath() + mediaName;
                String base64Audio = msgMediaChat.getBody();

                if(!TextUtils.isEmpty(base64Audio)){
                    FileUtil.getInstance().saveBase64IntoPath(base64Audio, audioPath);
                    Log.v(TAG, msgMediaChat.toString());
                    /** 消息表中添加音频消息记录 */
                    long resultCount = MessageDBUtil.getInstance().addMessage(msgMediaChat);
                    if(resultCount > 0){
                        MessageVo messageVO = MessageFactory.getInstance().buildMessageVoByMsgMedia(
                                                                            msgMediaChat, audioPath);
                        /** insert or update into table ChatRoom */
                        String shopID = messageVO.getShopId();
                        if(!TextUtils.isEmpty(shopID)) {
                            if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                                ChatRoomDBUtil.getInstance().addChatRoom(messageVO);
                            } else {
                                ChatRoomDBUtil.getInstance().updateChatRoom(messageVO);
                            }
                        }
                        /** post the message to chatActivity */
                        EventBus.getDefault().post(messageVO);
                    }
                }
            }

            /** 图片消息处理 */
            if(ProtocolMSG.MSG_CustomerServiceImgChat == type) {
                if(gson == null)
                    gson = new Gson();
                LogUtil.getInstance().info(LogLevel.INFO, "接收到图片消息:");
                MsgCustomerServiceImgChat msgImgChat = gson.fromJson(message,
                                            MsgCustomerServiceImgChat.class);
                //保存音频对象，并获得路径
                String imageName = msgImgChat.getFilename();
                String mediaPath = null;
                if(TextUtils.isEmpty(imageName)){
                    mediaPath = FileUtil.getInstance().getImagePath() + imageName;
                } else {
                    mediaPath = System.currentTimeMillis() + "." + msgImgChat.getFormat();
                }
                String base64Image = msgImgChat.getBody();
                if(!TextUtils.isEmpty(base64Image)){
                    Log.v(TAG, msgImgChat.toString());
                    FileUtil.getInstance().saveBase64IntoPath(base64Image, mediaPath);
                    /** 消息表中添加图片消息记录 */
                    long resultCount = MessageDBUtil.getInstance().addMessage(msgImgChat);
                    if(resultCount > 0){
                        MessageVo imageMessageVo = MessageFactory.getInstance().buildMessageVoByMsgImg(
                                                                                 msgImgChat, mediaPath);
                        /** insert or update into table ChatRoom */
                        String shopID = imageMessageVo.getShopId();
                        if(!TextUtils.isEmpty(shopID)) {
                            if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                                ChatRoomDBUtil.getInstance().addChatRoom(imageMessageVo);
                            } else {
                                ChatRoomDBUtil.getInstance().updateChatRoom(imageMessageVo);
                            }
                        }
                        EventBus.getDefault().post(imageMessageVo);
                    }
                }
            }

            /** 注册转移服务器IP */
            if(ProtocolMSG.MSG_TransferServer == type){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }

    @Override
    public void onWebsocketConnected(WebSocketClient webSocketClient) {
        LoginRequestManager.getInstance().init().sendLoginRequest(webSocketClient);
    }
}
