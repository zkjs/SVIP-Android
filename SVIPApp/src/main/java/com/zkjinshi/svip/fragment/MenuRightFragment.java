package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.adapter.ChatRoomAdapter;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.vo.MessageVo;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * 右侧消息中心菜单界面
 * Created by djd on 2015/8/10.
 */
public class MenuRightFragment extends Fragment {

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;

    public static final String TAG = "MenuRightFragment";

    private Activity                mActivity;
    private LinearLayout            mRightFragment;
    private TextView                mEmptyView;
    private ListView                mListViewChatRoom;
    private List<MessageVo>         mChatRoomLists;
    private ChatRoomAdapter         mChatRoomAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mActivity      = this.getActivity();
        if (mRightFragment == null){
            initView(inflater, container);
        }
        return mRightFragment;
    }

    public void onStart() {
        super.onStart();
        initData();
        initListener();
    }

    public void onResume(){
        super.onResume();
        mChatRoomLists   = MessageDBUtil.getInstance().queryHistoryMessageList();
        mChatRoomAdapter.setData(mChatRoomLists);
        mListViewChatRoom.invalidate();
    }

    private void initView(LayoutInflater inflater, ViewGroup container){
        mRightFragment    = (LinearLayout) inflater.inflate(R.layout.activity_message_center, container, false);
        mEmptyView        = (TextView)  mRightFragment.findViewById(R.id.tv_empty_view);
        mListViewChatRoom = (ListView)  mRightFragment.findViewById(R.id.lv_history_order);
        mListViewChatRoom.setEmptyView(mEmptyView);
    }

    private void initData() {
        //获得当前Fragment宿主Activity
        mChatRoomLists = new ArrayList<MessageVo>();
        //查询所有可显示的聊天室
        mChatRoomLists   = MessageDBUtil.getInstance().queryHistoryMessageList();
        mChatRoomAdapter = new ChatRoomAdapter(mChatRoomLists, mActivity);
        mListViewChatRoom.setAdapter(mChatRoomAdapter);
    }

    private void initListener() {
        /** 消息listView长点击事件 */
        mListViewChatRoom.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtil.getInstance().showToast(mActivity, "长点击消息操作");
                return false;
            }
        });

        mListViewChatRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageVo messageVo = mChatRoomLists.get(position);
                String shopId = messageVo.getShopId();
                String shopName = ShopListManager.getInstance().getShopName(shopId);
                Intent goChat = new Intent(mActivity, ChatActivity.class);
                goChat.putExtra("shop_id", shopId);
                goChat.putExtra("shop_name", shopName);
                mActivity.startActivity(goChat);
                mActivity.overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                //((MainActivity) mActivity).toggleMenu();
            }
        });
    }

    @Subscribe
    public void onEvent(MessageVo messageVo){
        Message message = new Message();
        message.what = NOTIFY_UPDATE_VIEW;
        handler.sendMessage(message);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOTIFY_UPDATE_VIEW:
                    mChatRoomLists = MessageDBUtil.getInstance().queryHistoryMessageList();
                    if(null != mChatRoomAdapter){
                        mChatRoomAdapter.setData(mChatRoomLists);
                    }
                    break;
            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
