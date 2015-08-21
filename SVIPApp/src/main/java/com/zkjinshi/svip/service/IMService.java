package com.zkjinshi.svip.service;

import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.listener.ReadListener;
import com.zkjinshi.base.net.listener.SendListener;
import com.zkjinshi.base.net.status.PacketUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.factory.ChatRoomFactory;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.VibratorUtil;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.MessageVo;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;

import de.greenrobot.event.EventBus;

/**
 * 开发者：vincent
 * 日期：2015/7/31
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IMService extends Service {

    private final static String TAG = IMService.class.getSimpleName();

    private String          mUserID;
    private String          mName;

    private IMServiceBinder binder = new IMServiceBinder();

    public class IMServiceBinder extends Binder {
        public IMService getService() {
            return IMService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUserID = CacheUtil.getInstance().getUserId();
        mName   = CacheUtil.getInstance().getUserName();

        registCSChatReaders();//注册接收消息信息
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 注册读取协议信息，收到用户消息后存入数据库
     */
    private void registCSChatReaders() {
        WebSocketClient.getInstance().registReader(PacketUtil.MSG_CustomerServiceTextChat, new ReadListener() {
            @Override
            public void onRead(boolean success, JSONObject msg) {
                if (!success) {
                    LogUtil.getInstance().info(LogLevel.INFO, "接收文本消息失败");
                    return;
                }
                LogUtil.getInstance().info(LogLevel.INFO, "接收到文本消息:" + msg.toString());
                /** 接收文本 */
                Gson gson = new Gson();
                MsgCustomerServiceTextChat msgTextChat = gson.fromJson(
                        msg.toString(), MsgCustomerServiceTextChat.class);
                if(msgTextChat == null){
                    LogUtil.getInstance().info(LogLevel.INFO, "接收到文本消息为空");
                    return ;
                }
                /** 消息表中添加消息记录 */
                long resultCount = MessageDBUtil.getInstance().addMessage(msgTextChat);
                if(resultCount > 0){
                    MessageVo messageVo = MessageFactory.getInstance().buildMessageVoByMsgText(msgTextChat);
                    EventBus.getDefault().post(messageVo);//分发chat
                    // chatroom shopID是否已经存在
                    String shopID = messageVo.getShopId();
                    if(!TextUtils.isEmpty(shopID)) {
                        if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                            ChatRoomDBUtil.getInstance().addChatRoom(messageVo);
                        } else {
                            ChatRoomDBUtil.getInstance().updateChatRoom(messageVo);
                        }
                    }
                }
            }
        });

        /** recevice the audio file */
        WebSocketClient.getInstance().registReader(PacketUtil.MSG_CustomerServiceMediaChat, new ReadListener() {
            @Override
            public void onRead(boolean success, org.json.JSONObject msg) {
                if (!success) {
                    LogUtil.getInstance().info(LogLevel.INFO, "协议接受音频消息失败");
                    return;
                }

                LogUtil.getInstance().info(LogLevel.INFO, "接收到音频消息:"+msg.toString());
                /** 手机振动 */
                VibratorUtil.Vibrate(IMService.this, 1000L);
                //接收音频聊天信息
                Gson gson = new Gson();
                MsgCustomerServiceMediaChat msgMediaChat = gson.fromJson(
                        msg.toString(), MsgCustomerServiceMediaChat.class);

                if(msgMediaChat == null){
                    LogUtil.getInstance().info(LogLevel.INFO, "接收到音频消息为空");
                    return ;
                }

                //保存音频对象，并获得路径
                String mediaName = msgMediaChat.getFilename();
                if(TextUtils.isEmpty(mediaName)){
                    mediaName = System.currentTimeMillis() + ".aac";
                }
                String audioPath   = FileUtil.getInstance().getAudioPath() + mediaName ;
                String base64Audio = msgMediaChat.getBody();

                if(!TextUtils.isEmpty(base64Audio)){
                    FileUtil.getInstance().saveBase64IntoPath(base64Audio, audioPath);
                    Log.v(TAG, msgMediaChat.toString());
                    /** 消息表中添加音频消息记录 */
                    long resultCount = MessageDBUtil.getInstance().addMessage(msgMediaChat);
                    if(resultCount > 0){
                        MessageVo messageVO = MessageFactory.getInstance().buildMessageVoByMsgMedia(
                                                                            msgMediaChat, audioPath);
                        EventBus.getDefault().post(messageVO);
                        // insert or update into table ChatRoom
                        String shopID = messageVO.getShopId();
                        if(!TextUtils.isEmpty(shopID)) {
                            if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                                ChatRoomDBUtil.getInstance().addChatRoom(messageVO);
                            } else {
                                ChatRoomDBUtil.getInstance().updateChatRoom(messageVO);
                            }
                        }
                    }
                }
            }
        });

        //接收图片文件
        WebSocketClient.getInstance().registReader(PacketUtil.MSG_CustomerServiceImgChat, new ReadListener() {
            @Override
            public void onRead(boolean success, org.json.JSONObject msg) {
                if (!success){
                    LogUtil.getInstance().info(LogLevel.INFO, "协议接受图片消息失败");
                    return;
                }

                LogUtil.getInstance().info(LogLevel.INFO, "接收到图片消息:"+msg.toString());
                /** 手机振动 */
                VibratorUtil.Vibrate(IMService.this, 1000L);
                //接收图片聊天信息
                Gson gson = new Gson();
                MsgCustomerServiceImgChat msgImgChat = gson.fromJson(
                        msg.toString(), MsgCustomerServiceImgChat.class);

                if(msgImgChat == null){
                    LogUtil.getInstance().info(LogLevel.INFO, "接收到图片消息为空");
                    return ;
                }

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
                        EventBus.getDefault().post(imageMessageVo);
                        // insert or update into table ChatRoom
                        String shopID = imageMessageVo.getShopId();
                        if(!TextUtils.isEmpty(shopID)) {
                            if(ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID)){
                                ChatRoomDBUtil.getInstance().addChatRoom(imageMessageVo);
                            } else {
                                ChatRoomDBUtil.getInstance().updateChatRoom(imageMessageVo);
                            }
                        }
                    }
                }
            }
        });
    }
}
