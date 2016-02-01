package com.zkjinshi.svip.emchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.TxtExtType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMConversationHelper {

    public static final String TAG = EMConversationHelper.class.getSimpleName();

    private EMConversationHelper(){}

    private static EMConversationHelper instance;

    public synchronized static EMConversationHelper getInstance(){
        if(null == instance){
            instance = new EMConversationHelper();
        }
        return instance;
    }

    /**
     * 发送命令消息(添加销售)
     * @param userId
     * @param userName
     * @param emCallBack
     */
    public void sendSaleCmdMessage(String userId,String userName,String toId, EMCallBack emCallBack){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(EMMessage.ChatType.Chat);
        cmdMsg.setAttribute("userId", userId);
        cmdMsg.setAttribute("userName", userName);
        String action="addSales";
        CmdMessageBody cmdBody=new CmdMessageBody(action);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setReceipt(toId);
        EMChatManager.getInstance().sendMessage(cmdMsg, emCallBack);
    }

    /**
     * 发送命令消息(邀请加入)
     * @param userId
     * @param userName
     * @param mobileNo
     * @param date
     * @param emCallBack
     */
    public void sendInviteCmdMessage(String userId,String userName,String mobileNo,long date,String toId, EMCallBack emCallBack){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(EMMessage.ChatType.Chat);
        cmdMsg.setAttribute("userId", userId);
        cmdMsg.setAttribute("userName", userName);
        cmdMsg.setAttribute("mobileNo", mobileNo);
        cmdMsg.setAttribute("date", "" + date);
        String action="inviteAdd";
        CmdMessageBody cmdBody=new CmdMessageBody(action);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setReceipt(toId);
        EMChatManager.getInstance().sendMessage(cmdMsg, emCallBack);
    }

    /**
     * 发送文本消息
     *
     */
    public void sendTxtMessage(String content,String salesId,String toName,String fromName,String shopId,String shopName,boolean bindClient,EMCallBack emCallBack){
        EMConversation conversation = EMChatManager.getInstance().getConversation(salesId);
        EMMessage message = EMMessage.createTxtSendMessage(content, salesId);
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE, TxtExtType.DEFAULT.getVlaue());
        message.setAttribute("bindClient",bindClient);
        message.setAttribute("toName","");
        if(!TextUtils.isEmpty(toName)){
            message.setAttribute("toName",toName);
        }
        message.setAttribute("fromName","");
        if(!TextUtils.isEmpty(fromName)){
            message.setAttribute("fromName",fromName);
        }
        if(!TextUtils.isEmpty(shopId)){
            message.setAttribute("shopId",shopId);
        }

        if(!TextUtils.isEmpty(shopName)){
            message.setAttribute("shopName",shopName);
        }
        message.setChatType(EMMessage.ChatType.Chat);
        message.status = EMMessage.Status.INPROGRESS;
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message,emCallBack);
    }

    /**
     * 发送文本消息
     * @param content
     * @param username
     */
    public void sendTxtMessage(String content,String username,EMCallBack emCallBack){
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(content);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(username);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, emCallBack);
    }

    public void requestGroupListTask(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().getGroupsFromServer();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    Log.i(TAG,"errorCode:"+e.getErrorCode());
                    Log.i(TAG,"errorMessage:"+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    /**
     * 发送语音消息
     * @param username
     * @param filePath
     * @param voiceTime
     * @param emCallBack
     */
    public void sendVoiceMessage(String username,String filePath,int voiceTime,EMCallBack emCallBack){
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        message.setChatType(EMMessage.ChatType.Chat);
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), voiceTime);
        message.addBody(body);
        message.setReceipt(username);
        conversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message, emCallBack);
    }

    /**
     * 发送语音消息
     * @param userId
     * @param fromName
     * @param toName
     * @param shopId
     * @param shopName
     * @param filePath
     * @param voiceTime
     * @param callBack
     */
    public void sendVoiceMessage(String userId,String fromName,String toName,String shopId,String shopName,String filePath, int voiceTime,EMCallBack callBack){
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, voiceTime, userId);
        message.setAttribute("toName","");
        if(!TextUtils.isEmpty(toName)){
            message.setAttribute("toName",toName);
        }
        message.setAttribute("fromName","");
        if(!TextUtils.isEmpty(fromName)){
            message.setAttribute("fromName",fromName);
        }
        if(!TextUtils.isEmpty(shopId)){
            message.setAttribute("shopId",shopId);
        }

        if(!TextUtils.isEmpty(shopName)){
            message.setAttribute("shopName",shopName);
        }
        message.setChatType(EMMessage.ChatType.Chat);
        EMChatManager.getInstance().sendMessage(message,callBack);
    }

    /**
     * 发送图片消息
     * @param username
     * @param filePath
     * @param emCallBack
     */
    public void sendImageMessage(String username,String filePath,EMCallBack emCallBack){
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        message.setReceipt(username);
        conversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message, emCallBack);
    }

    /**
     * 获取会话列表
     *
     * @return
    +    */
    public List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

}
