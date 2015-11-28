package com.zkjinshi.svip.emchat.observer;

import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.svip.utils.CacheUtil;

import java.util.UUID;

/**
 * 群相关事件监听
 * 开发者：JimmyZhang
 * 日期：2015/11/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EGroupReomveListener extends GroupReomveListener {

    public static final String TAG = EGroupReomveListener.class.getSimpleName();

    @Override
    public void onInvitationReceived(String groupId, String groupName,String inviter, String reason) {
        super.onInvitationReceived(groupId, groupName, inviter, reason);
        Log.i(TAG,"onInvitationReceived");
        //当前用户收到加入群组邀请
        //收到加入群聊的邀请
        boolean hasGroup = false;
        for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
            if (group.getGroupId().equals(groupId)) {
                hasGroup = true;
                break;
            }
        }
        if (!hasGroup)
            return;
        // 被邀请
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        msg.setChatType(EMMessage.ChatType.GroupChat);
        msg.setFrom(inviter);
        msg.setTo(groupId);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
        // 保存邀请消息
        EMChatManager.getInstance().saveMessage(msg);
        // 提醒新消息
        EMNotifier.getInstance(BaseContext.getInstance().getContext()).notifyOnNewMsg();
        //TODO Jimmy 默认同意加群
        try {
            EMGroupManager.getInstance().acceptApplication(CacheUtil.getInstance().getUserId(),groupId);
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationReceived(String s, String s1, String s2, String s3) {
        super.onApplicationReceived(s, s1, s2, s3);
        Log.i(TAG,"onApplicationReceived");
        // 用户申请加入群
    }

    @Override
    public void onApplicationAccept(String groupId, String groupName,String accepter) {
        super.onApplicationAccept(groupId, groupName, accepter);
        Log.i(TAG,"onApplicationAccept");
        //加群申请被接受
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        msg.setChatType(EMMessage.ChatType.GroupChat);
        msg.setFrom(accepter);
        msg.setTo(groupId);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
        // 保存同意消息
        EMChatManager.getInstance().saveMessage(msg);
        // 提醒新消息
        EMNotifier.getInstance(BaseContext.getInstance().getContext()).notifyOnNewMsg();
    }

    @Override
    public void onApplicationDeclined(String s, String s1, String s2, String s3) {
        super.onApplicationDeclined(s, s1, s2, s3);
        Log.i(TAG,"onApplicationDeclined");
        //加群申请被拒绝
    }

    @Override
    public void onInvitationAccpted(String s, String s1, String s2) {
        super.onInvitationAccpted(s, s1, s2);
        Log.i(TAG,"onInvitationAccpted");
        //群组邀请被接受
    }

    @Override
    public void onInvitationDeclined(String s, String s1, String s2) {
        super.onInvitationDeclined(s, s1, s2);
        Log.i(TAG,"onInvitationDeclined");
        //  群组邀请被拒绝
    }

    @Override
    public void onUserRemoved(String s, String s1) {
        Log.i(TAG,"onUserRemoved");
        //当前登录用户被管理员移除出群组
    }

    @Override
    public void onGroupDestroy(String s, String s1) {
        Log.i(TAG,"onGroupDestroy");
        //群组被解散
    }
}
