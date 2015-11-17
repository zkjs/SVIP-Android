package com.zkjinshi.svip.activity.im.actions;

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
import com.easemob.EMEventListener;
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
import com.zkjinshi.svip.manager.OrderManager;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.MsgListView;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.TxtExtType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息ListView管理器
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListViewManager extends Handler implements MsgListView.IXListViewListener,
        ChatAdapter.ResendListener,IEMessageObserver,EMEventListener {

    private static final String TAG = MessageListViewManager.class.getSimpleName();
    private static final int MESSAGE_LIST_VIEW_UPDATE_UI = 0X00;

    private Context context;
    private MsgListView messageListView;
    private ChatAdapter chatAdapter;
    private EMConversation conversation;
    private Vector<String> messageVector = new Vector<String>();//存储页面发送的消息ID
    private List<EMMessage> currentMessageList = new ArrayList<EMMessage>();
    private ArrayList<EMMessage> requestMessageList;
    private static final int PRE_LOAD_PAGE_SIZE = 20;// 每次预加载20条记录
    private String userId;

    private LinkedBlockingQueue<MessageVo> messageQueue = new LinkedBlockingQueue<MessageVo>();

    public MessageListViewManager(Context context, String userId) {
        this.context    = context;
        this.userId = userId;
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
        conversation = EMChatManager.getInstance().getConversation(userId);
        clearChatRoomBadgeNum();
        setOverScrollMode(messageListView);
        messageQueue = new LinkedBlockingQueue<MessageVo>();
        chatAdapter = new ChatAdapter(context, null);
        chatAdapter.setResendListener(this);
        messageListView.setPullLoadEnable(false);
        messageListView.setAdapter(chatAdapter);
        if (!chatAdapter.isEmpty()) {
            messageListView.setSelection(chatAdapter.getCount() - 1);
        }
        currentMessageList = conversation.getAllMessages();
        chatAdapter.setMessageList(currentMessageList);
        scrollBottom();
        addAllObserver();
    }

    private void initListeners() {
        messageListView.setXListViewListener(this);
    }

    public synchronized void destoryMessageListViewManager() {
        messageVector.clear();
        clearChatRoomBadgeNum();
        removeAllObserver();
    }

    /**
     * 将消息设置为已读
     */
    private void clearChatRoomBadgeNum(){
        conversation.markAllMessagesAsRead();
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
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE,TxtExtType.CARD.getVlaue());
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
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<>();
        }
        currentMessageList = conversation.getAllMessages();
        if(null != chatAdapter){
            chatAdapter.setMessageList(currentMessageList);
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
                messageListView.requestFocusFromTouch();
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
            if(null != conversation){
                if(conversation.getIsGroup()){
                    EMGroup group = EMGroupManager.getInstance().getGroup(userId);
                    if (group != null){
                        titleTv.setTextTitle(group.getGroupName());
                    }
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
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewCMDMessage});
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventDeliveryAck);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventReadAck);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
        EMChatManager.getInstance().unregisterEventListener(this);
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

    @Override
    public void onEvent(EMNotifierEvent event) {
        OrderManager.getInstance().receiveCmdMessage(event,context);
    }
}