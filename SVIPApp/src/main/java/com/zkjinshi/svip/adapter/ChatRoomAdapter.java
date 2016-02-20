package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.EmotionType;
import com.zkjinshi.svip.utils.EmotionUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.TxtExtType;

import java.util.ArrayList;
import java.util.List;

/**
 * 右侧消息中心聊天室适配器
 * 开发者：vincent
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<EMConversation> conversationList;
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public ChatRoomAdapter(ArrayList<EMConversation> conversationList,Context context) {
        this.context = context;
        this.setConversationList(conversationList);
    }

    public void setConversationList(ArrayList<EMConversation> conversationList) {
        if(null == conversationList){
            this.conversationList = new ArrayList<EMConversation>();
        }else {
            this.conversationList = conversationList;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_chat_room, null);
        //设置条目宽度满足屏幕
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ChatRoomViewHolder chatRoomHolder = new ChatRoomViewHolder(view,
                                            mRecyclerItemClickListener);
        return chatRoomHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EMConversation conversation = conversationList.get(position);
        String username = conversation.getUserName();
        EMConversation.EMConversationType chatType = conversation.getType();
        if (chatType == EMConversation.EMConversationType.GroupChat) {
            EMGroup group = EMGroupManager.getInstance().getGroup(username);
            ((ChatRoomViewHolder)holder).shopName.setText(group != null ? group.getGroupName() : username);
        } else if(chatType == EMConversation.EMConversationType.ChatRoom){
            EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
            ((ChatRoomViewHolder)holder).shopName.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
        }else {
            ((ChatRoomViewHolder)holder).shopName.setText(username);
        }
        //设置消息未读条数
        long unReadCount = conversation.getUnreadMsgCount();
        if(unReadCount <= 0){
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.GONE);
        }else if(unReadCount > 99) {
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.VISIBLE);
            ((ChatRoomViewHolder)holder).unReadCount.setText(99+"");
        } else {
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.VISIBLE);
            ((ChatRoomViewHolder)holder).unReadCount.setText(unReadCount+"");
        }
        if (conversation.getMsgCount() != 0) {
            EMMessage message = conversation.getLastMessage();
            EMMessage.Type msgType = message.getType();
            long chatTime = message.getMsgTime();
            String strChatTime = TimeUtil.getChatTime(chatTime);
            ((ChatRoomViewHolder)holder).latestChatTime.setText(strChatTime);
            if(chatType != EMConversation.EMConversationType.Chat){
                ((ChatRoomViewHolder)holder).shopIcon.setImageURI(Uri.parse("res:///"+R.mipmap.ic_launcher));
                if (msgType == EMMessage.Type.IMAGE) {
                    ((ChatRoomViewHolder)holder).chatContent.setText("[图片]");
                } else if (msgType ==  EMMessage.Type.VOICE) {
                    ((ChatRoomViewHolder)holder).chatContent.setText("[语音]");
                } else if(msgType == EMMessage.Type.TXT){
                    try {
                        int extType = message.getIntAttribute(Constants.MSG_TXT_EXT_TYPE);
                        if(TxtExtType.DEFAULT.getVlaue() == extType){
                            TextMessageBody txtBody = (TextMessageBody) message.getBody();
                            String content  = txtBody.getMessage();
                            CharSequence txt = EmotionUtil.getInstance()
                                    .convertStringToSpannable(context,
                                            content,
                                            EmotionType.CHAT_LIST);
                            ((ChatRoomViewHolder)holder).chatContent.setText(txt);
                        }else{
                            ((ChatRoomViewHolder)holder).chatContent.setText("[订单]");
                        }
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                try {
                    String userId = conversation.getUserName();
                    if(!TextUtils.isEmpty(userId) && chatType == EMConversation.EMConversationType.Chat){
                        String shopLogoUrl = ProtocolUtil.getAvatarUrl(userId);
                        ((ChatRoomViewHolder)holder).shopIcon.setImageURI(Uri.parse(shopLogoUrl));
                    }else {
                        ((ChatRoomViewHolder)holder).shopIcon.setImageURI(Uri.parse("res:///"+R.mipmap.ic_launcher));
                    }
                    String fromName = message.getStringAttribute("fromName");
                    String toName = message.getStringAttribute("toName");

                    if(!TextUtils.isEmpty(fromName) && !fromName.equals(CacheUtil.getInstance().getUserName())){
                        ((ChatRoomViewHolder)holder).shopName.setText(fromName);
                    }else {
                        if(!TextUtils.isEmpty(toName)){
                            ((ChatRoomViewHolder)holder).shopName.setText(toName);
                        }
                    }
                    if (msgType == EMMessage.Type.IMAGE) {
                        ((ChatRoomViewHolder)holder).chatContent.setText("[图片]");
                    } else if (msgType ==  EMMessage.Type.VOICE) {
                        ((ChatRoomViewHolder)holder).chatContent.setText("[语音]");
                    } else if(msgType == EMMessage.Type.TXT){
                        try {
                            int extType = message.getIntAttribute(Constants.MSG_TXT_EXT_TYPE);
                            if(TxtExtType.DEFAULT.getVlaue() == extType){
                                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                                String content  = txtBody.getMessage();
                                CharSequence txt = EmotionUtil.getInstance()
                                        .convertStringToSpannable(context,
                                                content,
                                                EmotionType.CHAT_LIST);
                                ((ChatRoomViewHolder)holder).chatContent.setText(txt);
                            }else{
                                ((ChatRoomViewHolder)holder).chatContent.setText("[订单]");
                            }
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView shopIcon;
        TextView        unReadCount;
        TextView        shopName;
        TextView        chatContent;
        TextView        latestChatTime;
        RecyclerItemClickListener mItemClickListener;

        public ChatRoomViewHolder(View view, RecyclerItemClickListener itemClickListener) {

            super(view);
            shopIcon       = (SimpleDraweeView) view.findViewById(R.id.iv_shop_icon);
            unReadCount    = (TextView) view.findViewById(R.id.tv_unread_message_count);
            shopName       = (TextView) view.findViewById(R.id.tv_shop_name);
            chatContent    = (TextView) view.findViewById(R.id.tv_chat_content);
            latestChatTime = (TextView) view.findViewById(R.id.tv_latest_chat_time);

            this.mItemClickListener = itemClickListener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }

    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }
}
