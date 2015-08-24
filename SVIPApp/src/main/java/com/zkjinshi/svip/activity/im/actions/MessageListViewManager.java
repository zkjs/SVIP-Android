package com.zkjinshi.svip.activity.im.actions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ChatAdapter;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChatRSP;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChatRSP;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChatRSP;
import com.zkjinshi.svip.bean.jsonbean.MsgRequestWaiterC2S;
import com.zkjinshi.svip.bean.jsonbean.MsgRequestWaiterC2SRSP;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.UUIDBuilder;
import com.zkjinshi.svip.view.MsgListView;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.MimeType;
import com.zkjinshi.svip.vo.SendStatus;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * 消息ListView管理器
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListViewManager extends Handler implements MsgListView.IXListViewListener,
        ChatAdapter.ResendListener, IMessageObserver{

    private static final String TAG = "MessageListViewManager";

    private static final int UPDATE_ADAPTER_UI = 0X00 ;

    private Context context;
    private MsgListView messageListView;
    private ChatAdapter chatAdapter;
    private Vector<String> messageVector       = new Vector<>();//存储页面发送的消息ID
    private List<MessageVo> currentMessageList = new ArrayList<>();
    private ArrayList<MessageVo> requestMessageList;
    private String sessionId;
    private long lastSendTime;
    private static final int PRE_LOAD_PAGE_SIZE = 20;// 每次预加载20条记录

    private boolean         isCallService;//是否已经呼叫服务
    private MessageVo       mMessageVo;

    private String          mShopID;

    public MessageListViewManager(Context context, String sessionId) {
        this.context = context;
        this.sessionId = sessionId;
    }

    public void setShopID(String shopID){
        this.mShopID = shopID;
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
        addObservers();
        clearChatRoomBadgeNum(sessionId);
        setOverScrollMode(messageListView);
        chatAdapter = new ChatAdapter(context, null);
        chatAdapter.setResendListener(this);
        messageListView.setPullLoadEnable(false);
        messageListView.setAdapter(chatAdapter);
        if (!chatAdapter.isEmpty()) {
            messageListView.setSelection(chatAdapter.getCount() - 1);
        }
        //获得本地最后一次发送消息的时间
        lastSendTime = MessageDBUtil.getInstance().queryLastSendTime(sessionId);
        // 查询数据库获得第一页数据
        queryPageMessages(sessionId, PRE_LOAD_PAGE_SIZE, lastSendTime, true);
    }

    private void initListeners() {
        messageListView.setXListViewListener(this);
    }

    public synchronized void destoryMessageListViewManager() {
        removeObservers();
        messageVector.clear();
        clearChatRoomBadgeNum(sessionId);
    }

    /**
     * 将消息设置为已读
     * @param seesionId
     */
    private void clearChatRoomBadgeNum(String seesionId){
        //step1  a.查询未读消息，并更新为已读
        //TODO Jimmy 1、清除本地聊天室未读消息
        if(MessageDBUtil.getInstance().queryNotifyCount(seesionId) > 0){
            MessageDBUtil.getInstance().updateMsgReadedBySessionID(seesionId);
        }

        //TODO Jimmy  2、网络请求聊天室置为已读消息
    }

    /**
     * 添加EventBus消息通知观察者
     */
    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat_RSP);
        EventBus.getDefault().register(this);
    }

    /**
     * 删除EventBus消息通知观察者
     */
    private void removeObservers() {
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat_RSP);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MessageVo messageVo) {
        //step2  将接收到的消息显示在界面中
        currentMessageList.add(messageVo);
        Message msg = Message.obtain();
        msg.what    = UPDATE_ADAPTER_UI;
        this.sendMessage(msg);
    }

    /**
     * 发送文本消息
     * @param content
     */
    public void sendTextMessage(String content) {
        String tempMessageId    = UUIDBuilder.getInstance().getRandomUUID();
        String defaultRuleType = context.getString(R.string.default_rule_type);

        long tempSendTime    = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        /** 1、IM发送文本消息 */
        mMessageVo = buildTextMessageVo(mShopID, sessionId, content,
                                        tempMessageId, tempSendTime,
                                        SendStatus.SENDING, defaultRuleType);

        /** 判断shopID聊天室是否存在 */
        boolean isExist = ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(mShopID);
        if(isExist){
            // 聊天室已存在, 更新聊天室信息
            long updResult = ChatRoomDBUtil.getInstance().updateChatRoom(mMessageVo);
        } else {
            // 聊天室尚未创建, 创建新的聊天室
            long addResult = ChatRoomDBUtil.getInstance().addChatRoom(mMessageVo);
        }

        /** 2、保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 3、构建文本vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        Message msg = Message.obtain();
        msg.what    = UPDATE_ADAPTER_UI;
        this.sendMessage(msg);
        if(!isCallService) {
            sendMessageVo(mMessageVo);
        }else {
            callService(mShopID, defaultRuleType);
        }
    }

    /**
     * 发送文本消息
     * @param content
     */
    public void sendTextMessage(String shopID, String content, String ruleType) {
        //step3
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime    = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        /** 1、IM发送文本消息 */
        mMessageVo = buildTextMessageVo(shopID, sessionId, content,
                tempMessageId, tempSendTime,
                SendStatus.SENDING, ruleType);

        /** 判断shopID聊天室是否存在 */
        boolean isExist = ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID);
        if(isExist){
            // 聊天室已存在, 更新聊天室信息
            long updResult = ChatRoomDBUtil.getInstance().updateChatRoom(mMessageVo);
        } else {
            // 聊天室尚未创建, 创建新的聊天室
            long addResult = ChatRoomDBUtil.getInstance().addChatRoom(mMessageVo);
        }

        /** 2、保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 3、构建文本vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        Message msg = Message.obtain();
        msg.what    = UPDATE_ADAPTER_UI;
        this.sendMessage(msg);
        if(!isCallService) {
            sendMessageVo(mMessageVo);
        }else {
            callService(shopID, ruleType);
        }
    }

    /**
     * 发送语音消息
     * @param fileName
     * @param filePath
     * @param voiceTime
     */
    public void sendAudioMessage(String shopID, String fileName,
                                 final String filePath, int voiceTime, String ruleType) {
        //临时消息ID
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime = System.currentTimeMillis();
        messageVector.add(tempMessageId);

        //get the base64 string from the filepath
        String attachId = FileUtil.getInstance().filePath2Base64(filePath);
        mMessageVo = buildAudioMessageVo(shopID, sessionId, tempMessageId,
                tempSendTime, SendStatus.SENDING,
                attachId, fileName, filePath,
                voiceTime, ruleType);

        /** 判断shopID聊天室是否存在 */
        boolean isExist = ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID);
        if(isExist){
            // 聊天室已存在, 更新聊天室信息
            long updResult = ChatRoomDBUtil.getInstance().updateChatRoom(mMessageVo);
        } else {
            // 聊天室尚未创建, 创建新的聊天室
            long addResult = ChatRoomDBUtil.getInstance().addChatRoom(mMessageVo);
        }

        // 1、保存文本消息到sqlite(注意此时的消息正在发送中)
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        // 3、构建语音vo实体，将消息内容显示到页面
        currentMessageList.add(mMessageVo);
        Message msg = Message.obtain();
        msg.what    = UPDATE_ADAPTER_UI;
        this.sendMessage(msg);
        if(isCallService) {
            sendMessageVo(mMessageVo);
        }else {
            callService(shopID, ruleType);
        }
    }

    /**
     * 发送图片消息
     * @param fileName
     * @param filePath
     */
    public void sendImageMessage(String shopID, String fileName,final String filePath ,String ruleType) {
        //临时消息ID
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        String attachId = ImageUtil.photo2Base64(filePath);
        /** 生成MessageVo对象 */
        mMessageVo = buildImageMessageVo(shopID, sessionId, tempMessageId,
                tempSendTime, SendStatus.SENDING,
                attachId, fileName, filePath, ruleType);

        /** 判断shopID聊天室是否存在 */
        boolean isExist = ChatRoomDBUtil.getInstance().isChatRoomExistsByShopID(shopID);
        if(isExist){
            // 聊天室已存在, 更新聊天室信息
            long updResult = ChatRoomDBUtil.getInstance().updateChatRoom(mMessageVo);
        } else {
            // 聊天室尚未创建, 创建新的聊天室
            long addResult = ChatRoomDBUtil.getInstance().addChatRoom(mMessageVo);
        }

        /** 1 保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 2 构建图片vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);

        Message msg = Message.obtain();
        msg.what    = UPDATE_ADAPTER_UI;
        this.sendMessage(msg);

        if(isCallService) {
            sendMessageVo(mMessageVo);
        }else {
            callService(shopID, ruleType);
        }
    }

    /**
     * 发送呼叫服务请求
     * @param shopID
     * @param ruleType
     */
    private void callService(String shopID, String ruleType) {
        MsgRequestWaiterC2S msgRequestWaiterC2S = new MsgRequestWaiterC2S();
        msgRequestWaiterC2S.setType(ProtocolMSG.MSG_RequestWaiter_C2S);
        msgRequestWaiterC2S.setTimestamp(System.currentTimeMillis());
//        msgRequestWaiterC2S.setTempid();
//        msgRequestWaiterC2S.setSrvmsgid();
//        msgRequestWaiterC2S.setProtover();
        msgRequestWaiterC2S.setShopid(shopID);
        msgRequestWaiterC2S.setRuletype(ruleType);
        String userID   = CacheUtil.getInstance().getUserId();
        String userName = CacheUtil.getInstance().getUserName();
        if(!TextUtils.isEmpty(userID)){
            msgRequestWaiterC2S.setClientid(userID);
        }
        if(!TextUtils.isEmpty(userName)){
            msgRequestWaiterC2S.setClientname(userName);
        }
        //TODO 呼叫服务内容待定
        msgRequestWaiterC2S.setDesc("TODO:呼叫服务内容待定");
//        msgRequestWaiterC2S.setLocid();
//        msgRequestWaiterC2S.setOther();
        msgRequestWaiterC2S.setSessionid(sessionId);
//        msgRequestWaiterC2S.setSeqid();
//        msgRequestWaiterC2S.setIsreadack();
        final Gson gson = new Gson();
        String msgJson = gson.toJson(msgRequestWaiterC2S, MsgRequestWaiterC2S.class);
        WebSocketManager.getInstance().sendMessage(msgJson);
    }

    /**
     * 向服务器发送消息对象
     * @param messageUiVo
     */
    private void sendMessageVo(final MessageVo messageUiVo) {
        messageUiVo.setSessionId(sessionId);
        switch (messageUiVo.getMimeType()){
            case TEXT://文本
                final MsgCustomerServiceTextChat msgText = MessageFactory.getInstance().
                        buildMsgTextByMessageVo(messageUiVo);
                Gson gson = new Gson();
                String textJson = gson.toJson(msgText);
                WebSocketManager.getInstance().sendMessage(textJson);
                break;
            case AUDIO://语音
                final MsgCustomerServiceMediaChat msgMedia = MessageFactory.getInstance().
                        buildMsgMediaByMessageVo(messageUiVo);

                final String mediaUploadUrl = "http://mmm.zkjinshi.com:90/media/upload";
                DataRequestVolley mediaPost = new DataRequestVolley(HttpMethod.POST, mediaUploadUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //音频上传成功，获取URL
                                if(TextUtils.isEmpty(response)){
                                    return ;
                                }
                                try {
                                    JSONObject responseObject = new JSONObject(response);
                                    int result = (int) responseObject.get("result");
                                    // 文件上传成功
                                    if(Constants.POST_SUCCESS == result){
                                        String mediaUrl = (String) responseObject.get("url");//正常图的URL
                                        messageUiVo.setUrl(mediaUrl);
                                    }
                                    //更新数据库信息
                                    MessageDBUtil.getInstance().updateMessage(MessageFactory.getInstance().
                                            buildContentValues(messageUiVo));
                                    //获得待发送协议消息
                                    MsgCustomerServiceMediaChat msgMedia = MessageFactory.getInstance().
                                            buildMsgMediaByMessageVo(messageUiVo);

                                    Gson gson = new Gson();
                                    String mediaJson = gson.toJson(msgMedia);
                                    WebSocketManager.getInstance().sendMessage(mediaJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } , new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: 网络异常处理
                        Log.v(TAG, "error:" + error.toString());
                    }}){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("FromID", msgMedia.getFromid());
                        paramMap.put("SessionID", msgMedia.getSessionid());
                        paramMap.put("ShopID", msgMedia.getShopid());
                        paramMap.put("Format", "aac");
                        paramMap.put("Body", msgMedia.getBody());
                        return paramMap;
                    }
                };
                RequestQueueSingleton.getInstance(context).addToRequestQueue(mediaPost);
                break;
            case IMAGE://图片
                final MsgCustomerServiceImgChat msgImage = MessageFactory.getInstance().
                        buildMsgImgByMessageVo(messageUiVo);

                //2、http上传图片（注意需要更新监听成功状态，更新数据库,并通过IM发送图片消息）
                String imageUploadUrl = "http://mmm.zkjinshi.com:90/img/upload";
                DataRequestVolley imagePost = new DataRequestVolley(HttpMethod.POST, imageUploadUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(TextUtils.isEmpty(response)){
                                    return ;
                                }
                                try {
                                    JSONObject responseObject = new JSONObject(response);
                                    int result                = (int) responseObject.get("result");
                                    // 文件上传成功
                                    if(Constants.POST_SUCCESS == result){
                                        String imageUrl = (String) responseObject.get("url");//正常图的URL
                                        String scaleUrl = (String) responseObject.get("s_url");//缩略图的URL
                                        messageUiVo.setUrl(imageUrl);
                                        messageUiVo.setScaleUrl(scaleUrl);
                                        // 更新数据库url地址
                                        MessageDBUtil.getInstance().updateMessage(MessageFactory.getInstance().
                                                buildContentValues(messageUiVo));
                                    }

                                    //获得待发送协议消息
                                    Gson gson = new Gson();
                                    String imageJson = gson.toJson(msgImage);
                                    WebSocketManager.getInstance().sendMessage(imageJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } , new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.getInstance().info(LogLevel.INFO,"长传图片网络异常");
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("FromID", msgImage.getFromid());
                        paramMap.put("SessionID", msgImage.getSessionid());
                        paramMap.put("ShopID", msgImage.getShopid());
                        paramMap.put("Format", msgImage.getFormat());
                        paramMap.put("Body", msgImage.getBody());
                        return paramMap;
                    }
                };
                RequestQueueSingleton.getInstance(context).addToRequestQueue(imagePost);
                break;
            case VIDEO://视频
                break;
            case APPLICATION://文件
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case UPDATE_ADAPTER_UI:
                chatAdapter.setData(currentMessageList);
                scrollBottom();
                break;
        }
    }

    public MessageVo buildTextMessageVo(String shopID, String sessionId, String content,
                                        String tempMessageId, long sendTime,
                                        SendStatus sendStatus, String ruleType) {
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(tempMessageId);//未发送前的
        messageVo.setSessionId(sessionId);
        messageVo.setContent(content);
        messageVo.setTempId(tempMessageId);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setShopId(shopID);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.TEXT);
        messageVo.setRuleType(ruleType);
        return messageVo;
    }

    public MessageVo buildImageMessageVo(String shopID, String sessionId, String tempMessageId,
                                         long sendTime, SendStatus sendStatus,
                                         String attachId, String fileName,
                                         String filePath, String ruleType) {
        MessageVo messageVo = new MessageVo();
        messageVo.setShopId(shopID);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setMessageId(tempMessageId);
        messageVo.setSessionId(sessionId);
        messageVo.setTempId(tempMessageId);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.IMAGE);
        messageVo.setAttachId(attachId);
        messageVo.setFileName(fileName);
        messageVo.setFilePath(filePath);
        messageVo.setRuleType(ruleType);
        return messageVo;
    }

    public MessageVo buildAudioMessageVo(String shopID, String sessionId,
                                         String tempMessageId, long sendTime,
                                         SendStatus sendStatus, String attachId,
                                         String fileName, String filePath,
                                         int voiceTime, String ruleType) {

        MessageVo messageVo = new MessageVo();
        messageVo.setShopId(shopID);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setSessionId(sessionId);
        messageVo.setMessageId(tempMessageId);
        messageVo.setTempId(tempMessageId);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.AUDIO);
        messageVo.setAttachId(attachId);
        messageVo.setFileName(fileName);
        messageVo.setFilePath(filePath);
        messageVo.setVoiceTime(voiceTime);
        messageVo.setRuleType(ruleType);
        return messageVo;
    }

    /**
     * 响应获取历史聊天记录
     * @param messageList
     */
    public void responsePageMessages(List<MessageVo> messageList) {
        if (null != messageList && !messageList.isEmpty()) {
            int messageRecordSize = messageList.size();
            MessageVo messageVo = null;
            for (int i = 0; i < (messageRecordSize > 10 ? 10
                    : messageRecordSize); i++) {
                messageVo = messageList.get(i);
                String messageId = messageVo.getMessageId();
                if (!messageVector.contains(messageId) && i < 10) {
                    currentMessageList.add(0, messageVo);
                    messageVector.add(messageId);
                }
            }
        }
        chatAdapter.setData(currentMessageList);
        if (null != messageListView) {
            if (!currentMessageList.isEmpty()) {
                messageListView.setSelection(currentMessageList.size());
            }
            messageListView.stopRefresh();
        }
    }

    /**
     * 查询一页数据
     * @param sessionId
     * @param limitSize
     * @param lastSendTime
     * @param isFirstTime
     */
    public void queryPageMessages(String sessionId, int limitSize,
                                  long lastSendTime, boolean isFirstTime) {
        long startTime, endTime, midTime = 0;
        int localCount;
        requestMessageList = new ArrayList<MessageVo>();
        if (isFirstTime) {
            requestMessageList = (ArrayList<MessageVo>)MessageDBUtil.getInstance().
                    queryMessageListBySessionId(
                            sessionId, lastSendTime, limitSize, true);
        } else {
            requestMessageList = (ArrayList<MessageVo>)MessageDBUtil.getInstance().
                    queryMessageListBySessionId(
                            sessionId, lastSendTime, limitSize, false);
        }
        Collections.reverse(requestMessageList);
        localCount = requestMessageList.size();
        if (localCount > 0) {
            if (NetWorkUtil.isNetworkConnected(context)) {
                startTime = requestMessageList.get(localCount - 1).getSendTime();
                endTime   = requestMessageList.get(0).getSendTime();
                if (localCount > 10) {
                    midTime = requestMessageList.get(9).getSendTime();
                }
                // step6 从服务器获取最新数据并更新
                //TODO Jimmy 同步后台数据，防止数据丢失
            }
            loadSqlteData(localCount, requestMessageList, sessionId, isFirstTime);

        } else if (localCount == 0) {
            if (NetWorkUtil.isNetworkConnected(context)) {
                //TODO Jimmy 请求后台，获取一页数据
                // step7 从服务器获取历史记录
                postDelayed(stopRefreshRunnable, 3000);
            }
        }
    }

    Runnable stopRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != messageListView) {
                messageListView.stopRefresh();
            }
        }
    };

    /**
     * 加载本地数据库消息记录
     * @param localCount
     * @param requestMessageList
     * @param chatId
     * @param isFirstTime
     */
    public void loadSqlteData(int localCount, ArrayList<MessageVo> requestMessageList,
                              String chatId, boolean isFirstTime) {
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<MessageVo>();
        }
        if (requestMessageList.size() > 10) {
            removeRange(requestMessageList, 0, 10);
        }
        currentMessageList.addAll(0, requestMessageList);
        if (null == chatAdapter) {
            chatAdapter = new ChatAdapter(context, currentMessageList);
        } else {
            chatAdapter.setData(currentMessageList);
        }
        for (MessageVo messageVoo : requestMessageList) {
            String messageId = messageVoo.getMessageId();
            if (!messageVector.contains(messageId)) {
                messageVector.add(messageId);
            }
        }
        if (null != messageListView) {
            if (isFirstTime) {
                messageListView.setSelection(localCount);
            } else {
                int count = requestMessageList != null ? requestMessageList
                        .size() : 0;
                messageListView.setSelection(count > 0 ? count - 1 : 0);
            }
            messageListView.stopRefresh();
        }
    }

    /**
     * 移除预加载多余数据
     * @param requestMessageList
     * @param fromIndex
     * @param toIndex
     */
    public void removeRange(List<MessageVo> requestMessageList,
                            int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            requestMessageList.remove(fromIndex);
        }
    }

    /**
     * 设置滚动位置
     */
    public void scrollBottom() {
        if (currentMessageList != null && currentMessageList.size() > 0) {
            messageListView.setSelection(currentMessageList.size() - 1);
        }
    }

    @Override
    public void onRefresh() {
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<>();
        }
        if (!currentMessageList.isEmpty()) {
            lastSendTime = currentMessageList.get(0).getSendTime();
            // 查询数据库获得第一页数据
            queryPageMessages(sessionId, PRE_LOAD_PAGE_SIZE, lastSendTime, false);
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
    public void onResend(MessageVo messageVo) {
        //消息重发送处理
        //1.删除消息条目
        //2.更新消息条目并将消息状态置为发送中
        if(messageVector.contains(messageVo.getTempId())) {
            messageVector.remove(messageVo.getTempId());
        }
        String tempID = UUIDBuilder.getInstance().getRandomUUID();
        if(currentMessageList.contains(messageVo)){
            currentMessageList.remove(messageVo);
        }
        String messageID = messageVo.getMessageId();
        messageVo.setMessageId(tempID);
        messageVo.setTempId(tempID);
        messageVo.setSendStatus(SendStatus.SENDING);
        MessageDBUtil.getInstance().updateMessageVoByMessageID(messageVo, messageID);
        messageVector.add(tempID);//临时消息ID
        currentMessageList.add(messageVo);
        //界面刷新
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(messageVo);
    }

    public MsgListView getMessageListView() {
        return messageListView;
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

    @Override
    public void receive(String message) {
        LogUtil.getInstance().info(LogLevel.INFO, message.toString());
        if(TextUtils.isEmpty(message))
            return ;

        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            /** 呼叫服务消息的回复 */
            if(ProtocolMSG.MSG_RequestWaiter_C2S_RSP == type){
                if(!isCallService)
                    isCallService = true;
                //获得呼叫服务的响应包
                Gson gson = new Gson();
                MsgRequestWaiterC2SRSP msgRequestRsp = gson.fromJson(message,
                        MsgRequestWaiterC2SRSP.class);
                int rspResult = msgRequestRsp.getResult();
                if(Constants.PROTOCAL_SUCCESS == rspResult){
                    if(null != mMessageVo){
                        sendMessageVo(mMessageVo);//发送消息包
                    }
                }
            }

            /** 文本消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceTextChat_RSP == type) {
                Gson gson = new Gson();
                MsgCustomerServiceTextChatRSP msgTextRSP = gson.fromJson(message, MsgCustomerServiceTextChatRSP.class);
                // 更新数据库
                String realMsgID = msgTextRSP.getSrvmsgid() + "";//服务器返回msgID
                long   sendTime  = msgTextRSP.getTimestamp();//服务器返回发送时间
                String tempID    = msgTextRSP.getTempid();//临时消息ID
                messageVector.remove(tempID);//删除消息ID集合
                // 1、更新是否已读状态
                MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                //查找更新后的消息对象
                MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                // 2. 添加发送消息的集合
                messageVector.add(realMsgID);
                if (null != messageChatVo && !currentMessageList.isEmpty()) {
                    for (int i = 0; i < currentMessageList.size(); i++) {
                        if (null != currentMessageList.get(i)
                                && null != currentMessageList.get(i).getMessageId()
                                && currentMessageList.get(i).getMessageId().equals(tempID)) {
                            currentMessageList.set(i, messageChatVo);
                        }
                    }
                }
                // 3、刷新页面
                Message msg = Message.obtain();
                msg.what    = UPDATE_ADAPTER_UI;
                this.sendMessage(msg);
            }

            /** 音频消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceMediaChat_RSP == type) {
                Gson gson = new Gson();
                MsgCustomerServiceMediaChatRSP msgMediaRSP = gson.fromJson(message, MsgCustomerServiceMediaChatRSP.class);

                // 更新数据库
                String realMsgID = msgMediaRSP.getSrvmsgid() + "";//服务器返回msgID
                long   sendTime  = msgMediaRSP.getTimestamp();//服务器返回发送时间
                String tempID = msgMediaRSP.getTempid();//临时消息ID

                if(messageVector.contains(tempID)){
                    messageVector.remove(tempID);//删除消息ID集合
                }

                // 1、更新是否已读状态
                MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                //查找更新后的消息对象
                MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                // 2. 添加发送消息的集合
                messageVector.add(realMsgID);
                if (null != messageChatVo && !currentMessageList.isEmpty()) {
                    for (int i = 0; i < currentMessageList.size(); i++) {
                        if (null != currentMessageList.get(i)
                                && null != currentMessageList.get(i).getMessageId()
                                && currentMessageList.get(i).getMessageId().equals(tempID)) {
                            currentMessageList.set(i, messageChatVo);
                        }
                    }
                }
                Message msg = Message.obtain();
                msg.what    = UPDATE_ADAPTER_UI;
                this.sendMessage(msg);
            }

            /** 图片消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceImgChat_RSP == type) {
                Gson gson = new Gson();
                MsgCustomerServiceImgChatRSP msgImgRSP = gson.fromJson(message,
                        MsgCustomerServiceImgChatRSP.class);

                // 更新数据库
                String realMsgID = msgImgRSP.getSrvmsgid() + "";//服务器返回msgID
                long   sendTime  = msgImgRSP.getTimestamp();//服务器返回发送时间
                String tempID    = msgImgRSP.getTempid();//临时消息ID

                if(messageVector.contains(tempID)){
                    messageVector.remove(tempID);//删除消息ID集合
                }
                // 1、更新是否已读状态
                MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                //查找更新后的消息对象
                MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                // 2. 添加发送消息的集合
                messageVector.add(realMsgID);
                if (null != messageChatVo && !currentMessageList.isEmpty()) {
                    for (int i = 0; i < currentMessageList.size(); i++) {
                        if (null != currentMessageList.get(i)
                                && null != currentMessageList.get(i).getMessageId()
                                && currentMessageList.get(i).getMessageId().equals(tempID)) {
                            currentMessageList.set(i, messageChatVo);
                        }
                    }
                }

                // 3、刷新页面
                Message msg = Message.obtain();
                msg.what    = UPDATE_ADAPTER_UI;
                this.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}