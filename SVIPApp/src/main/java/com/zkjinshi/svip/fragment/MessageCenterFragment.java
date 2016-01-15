package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.im.group.ChatGroupActivity;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.adapter.ChatRoomAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.emchat.observer.EMessageSubject;
import com.zkjinshi.svip.emchat.observer.IEMessageObserver;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.utils.CacheUtil;
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
    private RelativeLayout unLoginLayout;

    private LinearLayout discoverLayout;
    private LinearLayout loginLayout;
    private Button discoverBtn;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_message_center, null);
        mRcvMsgCenter = (RecyclerView) view.findViewById(R.id.rcv_message_center);
        mTvDialog     = (TextView)     view.findViewById(R.id.tv_dialog);
        mRcvMsgCenter.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvMsgCenter.setLayoutManager(mLayoutManager);
        unLoginLayout = (RelativeLayout)view.findViewById(R.id.contacts_layout_unlogin);

        discoverLayout = (LinearLayout)view.findViewById(R.id.llt_discover);
        loginLayout = (LinearLayout)view.findViewById(R.id.llt_login);
        discoverBtn = (Button)view.findViewById(R.id.btn_discover_service);

        unLoginLayout.setVisibility(View.GONE);
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
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                EMConversation conversation = conversationList.get(position);
                EMMessage message = conversation.getLastMessage();
                String username = conversation.getUserName();
                Intent intent = new Intent();
                if (conversation.isGroup()) {
                    intent.setClass(getActivity(), ChatGroupActivity.class);
                    intent.putExtra("groupId",username);
                }else {
                    intent.setClass(getActivity(), ChatActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, username);
                    if (null != message) {
                        try {
                            String shopId = message.getStringAttribute("shopId");
                            String shopName = message.getStringAttribute("shopName");
                            String fromName = message.getStringAttribute("fromName");
                            String toName = message.getStringAttribute("toName");
                            if (!TextUtils.isEmpty(shopId)) {
                                intent.putExtra(Constants.EXTRA_SHOP_ID, shopId);
                            }
                            if (!TextUtils.isEmpty(shopName)) {
                                intent.putExtra(Constants.EXTRA_SHOP_NAME, shopName);
                            }
                            if (!toName.equals(CacheUtil.getInstance().getUserName())) {
                                intent.putExtra(Constants.EXTRA_TO_NAME, toName);
                            } else {
                                intent.putExtra(Constants.EXTRA_TO_NAME, fromName);
                            }
                            intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                }
                startActivity(intent);
            }
        });



        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof MainActivity){
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.setCurrentItem(1);
                }
            }
        });

        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        EMConversationHelper.getInstance().requestGroupListTask();
        conversationList = (ArrayList<EMConversation>) EMConversationHelper.getInstance().loadConversationList();
        mChatRoomAdapter.setConversationList(conversationList);
        checkViewVisiable();
    }

    public void checkViewVisiable(){
        if(!CacheUtil.getInstance().isLogin()){
            unLoginLayout.setVisibility(View.VISIBLE);
            discoverLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.VISIBLE);
        }else {
            unLoginLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            if(null == conversationList || conversationList.isEmpty()){
                mTvDialog.setVisibility(View.VISIBLE);
                mTvDialog.setText("暂无消息");
                discoverLayout.setVisibility(View.VISIBLE);
            }else {
                mTvDialog.setVisibility(View.GONE);
                discoverLayout.setVisibility(View.GONE);
            }
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
                checkViewVisiable();
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
