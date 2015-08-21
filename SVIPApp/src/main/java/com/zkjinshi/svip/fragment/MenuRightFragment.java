package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ChatRoomAdapter;
import com.zkjinshi.svip.factory.ChatRoomFactory;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.view.swipelistview.SwipeMenu;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuCreator;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuItem;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuListView;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.MessageVo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * 右侧消息中心菜单界面
 * Created by djd on 2015/8/10.
 */
public class MenuRightFragment extends Fragment {

    public static final String TAG = "MenuRightFragment";

    private Activity                mActivity;
    private LinearLayout            mRightFragment;
    private TextView                mEmptyView;
    private SwipeMenuListView       mListViewChatRoom;
    private List<ChatRoomVo>        mChatRoomLists;
    private Map<String, ChatRoomVo> mChatRoomMap;
    private ChatRoomAdapter         mChatRoomAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
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

    private void initView(LayoutInflater inflater, ViewGroup container){
        mRightFragment    = (LinearLayout) inflater.inflate(R.layout.activity_message_center,
                                                            container, false);
        mEmptyView        = (TextView)  mRightFragment.findViewById(R.id.tv_empty_view);
        mListViewChatRoom = (SwipeMenuListView)  mRightFragment.findViewById(R.id.slv_history_order);
        mListViewChatRoom.setEmptyView(mEmptyView);
    }

    private void initData() {
        //获得当前Fragment宿主Activity
        mActivity = this.getActivity();
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DisplayUtil.dip2px(getActivity(), 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListViewChatRoom.setMenuCreator(creator);
        mChatRoomLists = new ArrayList<>();
        mChatRoomMap   = new HashMap<>();
        //查询所有可显示的聊天室
        mChatRoomLists   = ChatRoomDBUtil.getInstance().queryAllChatRoomList();
        mChatRoomAdapter = new ChatRoomAdapter(mChatRoomLists, mActivity);
        mListViewChatRoom.setAdapter(mChatRoomAdapter);
    }

    private void initListener() {
        /** 禁止列表上下滑动事件 */
        mListViewChatRoom.setOnTouchListener(null);
        /** list点击事件 step 2. listener item click event */
        mListViewChatRoom.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //db中更新数据为不显示
                ChatRoomVo chatRoomVo = mChatRoomLists.get(position);
                ChatRoomDBUtil.getInstance().updateChatRoomInVisible(chatRoomVo);
                //界面刷新
                return false;
            }
        });
    }

    @Subscribe
    public void onEvent(MessageVo messageVo){
        DialogUtil.getInstance().showToast(mActivity, "消息中心接收到消息:"+messageVo.getContent());
        // TODO：判断当前UI是否存在此对象
        String shopID = messageVo.getShopId();
        /** case 1: 当前聊天室当前不存在 */
        if(!mChatRoomMap.containsKey(shopID)){
            ChatRoomVo chatRoom = ChatRoomFactory.getInstance().
                            buildChatRoomByMessageVo(messageVo);
            long updResult = ChatRoomDBUtil.getInstance().updateChatRoomInVisible(chatRoom);
            if(updResult > 0){
                mChatRoomMap.put(shopID, chatRoom);
                mChatRoomLists.add(chatRoom);
            }
        } else {
        /** case 2: 当前聊天室当前已存在 */
            ChatRoomVo chatRoom = mChatRoomMap.get(shopID);
            if(null != chatRoom) {
                long updResult = ChatRoomDBUtil.getInstance().updateChatRoomInVisible(chatRoom);
                if(updResult > 0) {
                    mChatRoomLists.remove(chatRoom);
                    mChatRoomLists.add(chatRoom);
                    mChatRoomMap.put(chatRoom.getShopid(), chatRoom);
                }
            }
        }
        mChatRoomAdapter.setData(mChatRoomLists);
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
