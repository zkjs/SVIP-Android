package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.adapter.ChatRoomAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.fragment.contacts.SortModel;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.MessageVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 采用toolbar显示的服务中心效果界面
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageCenterFragment extends BaseFragment{

    private RecyclerView        mRcvMsgCenter;
    private LinearLayoutManager mLayoutManager;
    private ChatRoomAdapter     mChatRoomAdapter;
    private List<MessageVo>     mChatRoomLists;
    private TextView            mTvDialog;

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
        mChatRoomLists   = MessageDBUtil.getInstance().queryHistoryMessageList();
        mChatRoomAdapter = new ChatRoomAdapter(mActivity, mChatRoomLists);
        mRcvMsgCenter.setAdapter(mChatRoomAdapter);

        //条目点击事件，进入聊天界面
        mChatRoomAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                MessageVo messageVo = mChatRoomLists.get(position);
                String shopId = messageVo.getShopId();
                String shopName = ShopListManager.getInstance().getShopName(shopId);
                Intent goChat = new Intent(mActivity, ChatActivity.class);
                goChat.putExtra("shop_id", shopId);
                goChat.putExtra("shop_name", shopName);
                mActivity.startActivity(goChat);
                mActivity.overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }

    /**
     * 更新listview界面展示
     * @param messageVos
     */
    private void updateListView(List<MessageVo> messageVos) {
        if(null == messageVos || messageVos.isEmpty()){
            mTvDialog.setVisibility(View.VISIBLE);
            mTvDialog.setText(mActivity.getString(R.string.current_none));
        }else {
            // 根据a-z进行排序源数据
            mTvDialog.setVisibility(View.GONE);
            mChatRoomAdapter.updateListView(messageVos);
        }
    }
}
