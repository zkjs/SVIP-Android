package com.zkjinshi.svip.activity.im.single.actions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.easemob.EMCallBack;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ChatAdapter;
import com.zkjinshi.svip.emchat.observer.EMessageSubject;
import com.zkjinshi.svip.emchat.observer.IEMessageObserver;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.MsgListView;
import com.zkjinshi.svip.vo.TxtExtType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息ListView管理器
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListViewManager extends Handler implements MsgListView.IXListViewListener,
        ChatAdapter.ResendListener,IEMessageObserver {

    private static final int MESSAGE_LIST_VIEW_UPDATE_UI = 0X00;

    private Context context;
    private MsgListView messageListView;
    private ChatAdapter chatAdapter;
    private EMConversation conversation;
    private List<EMMessage> currentMessageList = new ArrayList<EMMessage>();
    private List<EMMessage> requestMessageList = new ArrayList<EMMessage>();
    public static final int PAGE_SIZE = 10;
    private String userId;// 回话id or 用户id(对方)
    private String fromName;// 聊天对象
    private String toName;// 聊天对象
    private String shopId;// 商店id
    private String shopName;// 商店名称

    public MessageListViewManager(Context context, String userId,String fromName,String toName,String shopId,String shopName) {
        this.context    = context;
        this.userId = userId;
        this.fromName = fromName;
        this.toName = toName;
        this.shopId = shopId;
        this.shopName = shopName;
    }

    public void init() {
        initView((Activity) context);
        initData();
        initListeners();
    }

    private void initView(Activity activity) {
        messageListView = (MsgListView) activity
                .findViewById(R.id.msg_listView);
    }

    private void initData() {
        if(!TextUtils.isEmpty(userId)){
            conversation = EMChatManager.getInstance().getConversation(userId);
        }
        clearChatRoomBadgeNum();
        setOverScrollMode(messageListView);
        chatAdapter = new ChatAdapter(context, null);
        chatAdapter.setResendListener(this);
        messageListView.setPullLoadEnable(false);
        messageListView.setAdapter(chatAdapter);
        if (!chatAdapter.isEmpty()) {
            messageListView.setSelection(chatAdapter.getCount() - 1);
        }
        if(null != conversation){
            currentMessageList = conversation.getAllMessages();
        }
        chatAdapter.setMessageList(currentMessageList);
        scrollBottom();
        addAllObserver();
    }

    private void initListeners() {
        messageListView.setXListViewListener(this);
    }

    public synchronized void destoryMessageListViewManager() {
        clearChatRoomBadgeNum();
        removeAllObserver();
    }

    /**
     * 将消息设置为已读
     */
    private void clearChatRoomBadgeNum(){
        if(null != conversation && conversation.getAllMsgCount() > 0){
            conversation.markAllMessagesAsRead();
        }
    }

    /**
     * 发送默认文本消息
     * @param content
     */
    public void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE, TxtExtType.DEFAULT.getVlaue());
        sendMessage(message);
    }

    /**
     * 发送默认卡片消息
     * @param content
     */
    public void sendCardMessage(String content){
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE, TxtExtType.CARD.getVlaue());
        sendMessage(message);
    }

    /**
     * 发送语音消息
     * @param filePath
     * @param voiceTime
     */
    public void sendVoiceMessage(String filePath, int voiceTime) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, voiceTime, userId);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     * @param filePath
     */
    public void sendImageMessage(String filePath) {
        EMMessage message = EMMessage.createImageSendMessage(filePath, false, userId);
        sendMessage(message);
    }

    protected void sendMessage(final EMMessage message){
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
        currentMessageList.add(message);
        chatAdapter.setMessageList(currentMessageList);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentMessageList.contains(message)) {
                            message.status = EMMessage.Status.SUCCESS;
                            chatAdapter.setMessageList(currentMessageList);
                            scrollBottom();
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh() {
        if(null != currentMessageList && !currentMessageList.isEmpty()){
            EMMessage emMessage = currentMessageList.get(0);
            String startMsgId = emMessage.getMsgId();
            requestMessageList = conversation.loadMoreMsgFromDB(startMsgId, PAGE_SIZE);
            if(null != requestMessageList && !requestMessageList.isEmpty()){
                if(!currentMessageList.containsAll(requestMessageList)){
                    currentMessageList.addAll(0, requestMessageList);
                }
            }
            chatAdapter.setMessageList(currentMessageList);
            int count = requestMessageList != null ? requestMessageList
                    .size() : 0;
            messageListView.setSelection(count > 0 ? count - 1 : 0);
            messageListView.stopRefresh();
        }
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 重发消息
     * @param messageVo
     */
    @Override
    public void onResend(EMMessage messageVo) {
        //消息重发送处理
        //1.删除消息条目
        //2.更新消息条目并将消息状态置为发送中

    }

    public MsgListView getMessageListView() {
        return messageListView;
    }

    /**
     * 设置滚动位置
     */
    public void scrollBottom() {
        if (currentMessageList != null && !currentMessageList.isEmpty()) {
            if (currentMessageList != null && currentMessageList.size() > 0) {
                messageListView.setSelection(currentMessageList.size() - 1);
            }
        }
    }

    /**
     * 禁止ListView自动滚动
     * @param listView
     */
    @SuppressLint("NewApi")
    public static void setOverScrollMode(ListView listView) {
        try {
            int sdk = DeviceUtils.getSdk();
            if (sdk > 10) {
                listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            } else {
                Method method = AbsListView.class.getMethod(
                        "setOverScrollMode", int.class);
                method.invoke(listView, View.OVER_SCROLL_NEVER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(ItemTitleView titleTv){
        if(null != titleTv){
            if(!TextUtils.isEmpty(userId)){
                titleTv.setTextTitle(userId);
            }
            if(!TextUtils.isEmpty(fromName) && !fromName.equals(CacheUtil.getInstance().getUserName())){
                titleTv.setTextTitle(fromName);
            }else {
                if(!TextUtils.isEmpty(toName)){
                    titleTv.setTextTitle(toName);
                }
            }
        }
    }

    /**
     * 添加观察者
     */
    private void addAllObserver(){
        EMessageSubject.getInstance().addObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().addObserver(this, EMNotifierEvent.Event.EventDeliveryAck);
        EMessageSubject.getInstance().addObserver(this, EMNotifierEvent.Event.EventReadAck);
        EMessageSubject.getInstance().addObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventDeliveryAck);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventReadAck);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
    }

    @Override
    public void receive(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                // 获取到message
                EMMessage message = (EMMessage) event.getData();
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(userId)) {
                    clearChatRoomBadgeNum();
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_LIST_VIEW_UPDATE_UI;
                    sendMessage(msg);
                    // 声音和震动提示有新消息
                } else {
                    // 如果消息不是和当前聊天ID的消息
                }
                break;
            case EventDeliveryAck:
                EMChatManager.getInstance().getChatOptions().setRequireDeliveryAck(true);
                final EMMessage deliveryMessage = (EMMessage) event.getData();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentMessageList.contains(deliveryMessage)) {
                            deliveryMessage.status = EMMessage.Status.SUCCESS;
                            chatAdapter.setMessageList(currentMessageList);
                            scrollBottom();
                        }
                    }
                });
				break;
            case EventReadAck:
                // 获取到message
                break;
            case EventOfflineMessage:
                // a list of offline messages
                // List<EMMessage> offlineMessages = (List<EMMessage>)
                // event.getData();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MESSAGE_LIST_VIEW_UPDATE_UI:
                chatAdapter.setMessageList(currentMessageList);
                scrollBottom();
                break;
        }
    }

}