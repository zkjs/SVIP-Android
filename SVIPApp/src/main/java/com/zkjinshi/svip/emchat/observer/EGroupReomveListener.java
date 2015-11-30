package com.zkjinshi.svip.emchat.observer;

import android.util.Log;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.GroupReomveListener;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.svip.utils.CacheUtil;

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
