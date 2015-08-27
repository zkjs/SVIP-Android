package com.zkjinshi.svip.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.IMessageListener;
import com.zkjinshi.base.net.core.MessageReceiver;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.order.PayOrderActivity;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.bean.jsonbean.MsgUserDefine;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.notification.NotificationHelper;
import com.zkjinshi.svip.request.login.LoginRequestManager;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.MessageVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListener extends Handler implements IMessageListener {

    public static final String TAG = MessageReceiver.class.getSimpleName();

    public static final int BOOK_HOTEL_FAIL_FLAG = 0;
    public static final int BOOK_HOTEL_SUCC_FLAG = 1;
    public static final int RELOGIN_MSG_FLAG = 2;

    private SimpleDateFormat sdf;
    private BookOrder bookOrder;
    private Message notifyMessage;

    @Override
    public void onNetReceiveSucceed(String message) {
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            if (type == ProtocolMSG.MSG_ServerRepeatLogin) {//重复登录
                WebSocketManager.getInstance().logoutIM(VIPContext.getInstance().getContext());
                notifyMessage = new Message();
                notifyMessage.what = RELOGIN_MSG_FLAG;
                sendMessage(notifyMessage);
                return;
            }

            if (type == ProtocolMSG.MSG_UserDefine_RSP) {//用户自定义协议
                //1、订单成功信息响应
                if (gson == null) {
                    gson = new Gson();
                }
                MsgUserDefine msgUserDefine = gson.fromJson(message, MsgUserDefine.class);
                if (null != msgUserDefine) {
                    int childType = msgUserDefine.getChildtype();
                    if (0 == childType) {//订单预定失败
                        notifyMessage = new Message();
                        notifyMessage.what = BOOK_HOTEL_FAIL_FLAG;
                        sendMessage(notifyMessage);
                    } else if (1 == childType) {//订单预定成功
                        String bookMsg = msgUserDefine.getContent();
                        if (!TextUtils.isEmpty(bookMsg)) {
                            bookOrder = gson.fromJson(bookMsg, BookOrder.class);
                        }
                        notifyMessage = new Message();
                        notifyMessage.what = BOOK_HOTEL_SUCC_FLAG;
                        notifyMessage.obj = bookOrder;
                        sendMessage(notifyMessage);
                    }
                }
                return;
            }

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
                    NotificationHelper.getInstance().showNotification(VIPContext.getInstance().getContext(), messageVo);
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
                        NotificationHelper.getInstance().showNotification(VIPContext.getInstance().getContext(), messageVO);
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
                        NotificationHelper.getInstance().showNotification(VIPContext.getInstance().getContext(), imageMessageVo);
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

    /**
     * 显示重复登录提示框
     *
     * @param context
     */
    private synchronized void showReLoginDialog(final Context context) {
        Dialog dialog = null;
        sdf = new SimpleDateFormat("HH:mm");
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("下载通知");
        customBuilder.setMessage("您的账号于" + sdf.format(new Date()) + "在另一台设备登录");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        customBuilder.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                WebSocketManager.getInstance().initClient();
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    private synchronized void showBookHotelSuccDialog(final Context context, final BookOrder bookOrder) {
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("订单通知");
        customBuilder.setMessage("您的订单已经生成，请尽快确认");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        customBuilder.setPositiveButton("查看", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, PayOrderActivity.class);
                intent.putExtra("bookOrder", bookOrder);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    private synchronized void showBookHotelFailDialog(final Context context) {
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("订单通知");
        customBuilder.setMessage("您的订单预定失败，望悉知");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BOOK_HOTEL_FAIL_FLAG:
                showBookHotelFailDialog(VIPContext.getInstance().getContext());
                break;

            case BOOK_HOTEL_SUCC_FLAG:
                bookOrder = (BookOrder) msg.obj;
                showBookHotelSuccDialog(VIPContext.getInstance().getContext(), bookOrder);
                break;

            case RELOGIN_MSG_FLAG:
                showReLoginDialog(VIPContext.getInstance().getContext());
                break;

            default:
        }
        super.handleMessage(msg);
    }
}
