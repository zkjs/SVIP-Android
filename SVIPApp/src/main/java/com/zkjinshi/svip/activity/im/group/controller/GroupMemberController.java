package com.zkjinshi.svip.activity.im.group.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestListener;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GroupMemberController {

    public static final String TAG = GroupMemberController.class.getSimpleName();

    private GroupMemberController(){}

    private static GroupMemberController instance;

    public synchronized static GroupMemberController getInstance(){
        if(null == instance){
            instance = new GroupMemberController();
        }
        return instance;
    }

    public void requestGroupMembersTask(final String groupId, final NetRequestListener netRequestListener, final Context context){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getGroupMemberUrl(groupId));
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        if(null != netRequestListener){
            netRequestTask.setNetRequestListener(netRequestListener);
        }
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
