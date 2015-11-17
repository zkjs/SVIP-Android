package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.adapter.ChatRoomAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.emchat.observer.EMessageSubject;
import com.zkjinshi.svip.emchat.observer.IEMessageObserver;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.manager.OrderManager;
import com.zkjinshi.svip.utils.Constants;
import java.util.ArrayList;

/**
 * 采用toolbar显示的服务中心效果界面
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageCenterFragment extends BaseFragment implements IEMessageObserver {

    private RecyclerView mRcvMsgCenter;
    private LinearLayoutManager mLayoutManager;
    private ChatRoomAdapter mChatRoomAdapter;
    private ArrayList<EMConversation> conversationList;
    private TextView mTvDialog;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_message_center, null);
        mRcvMsgCenter = (RecyclerView) view.findViewById(R.id.rcv_message_center);
        mTvDialog     = (TextView)     view.findViewById(R.id.tv_dialog);
        mRcvMsgCenter.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvMsgCenter.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        addAllObserver();
        mChatRoomAdapter = new ChatRoomAdapter(conversationList,mActivity);
        mRcvMsgCenter.setAdapter(mChatRoomAdapter);
        //条目点击事件，进入聊天界面
        mChatRoomAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMConversation conversation = conversationList.get(position);
                String username = conversation.getUserName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                if (conversation.isGroup()) {
                    if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                        intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_CHATROOM);
                    } else {
                        intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
                    }
                }
                intent.putExtra(Constants.EXTRA_USER_ID, username);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        conversationList = (ArrayList<EMConversation>) EMConversationHelper.getInstance().loadConversationList();
        mChatRoomAdapter.setConversationList(conversationList);
        if(null == conversationList || conversationList.isEmpty()){
            mTvDialog.setVisibility(View.VISIBLE);
            mTvDialog.setText(mActivity.getString(R.string.current_none));
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conversationList.clear();
                conversationList.addAll(EMConversationHelper.getInstance().loadConversationList());
                mChatRoomAdapter.setConversationList(conversationList);
            }
        });
    }

    /**
     * 添加观察者
     */
    private void addAllObserver(){
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().removeObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    @Override
    public void receive(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            case EventOfflineMessage:
            case EventConversationListChanged:
                refresh();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllObserver();
    }

}
