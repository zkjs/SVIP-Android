package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.fragment.contacts.SortModel;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.menu.vo.Menu;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.MimeType;

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

    private Context         mContext;
    private List<MessageVo> mDatas;

    private DisplayImageOptions       options;
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public ChatRoomAdapter(Context mContext, List<MessageVo> datas) {
        this.mContext = mContext;
        this.mDatas   = datas;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_logo_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_logo_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_logo_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(mContext, R.layout.item_chat_room, null);
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
        MessageVo messageVo = mDatas.get(position);
        String shopID = messageVo.getShopId();
        String shopLogoUrl  = Constants.GET_SHOP_LOGO + shopID + Constants.FORMAT_PNG;
        ImageLoader.getInstance().displayImage(shopLogoUrl, ((ChatRoomViewHolder)holder).shopIcon, options);

        //查询出最后一条聊天对象
        ((ChatRoomViewHolder)holder).shopName.setText(ShopListManager.getInstance().getShopName(shopID));//暂时显示shopID
        //设置消息未读条数
        long unReadCount = MessageDBUtil.getInstance().queryNotifyCountByShopID(shopID);
        if(unReadCount <= 0){
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.GONE);
        }else if(unReadCount > 99) {
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.VISIBLE);
            ((ChatRoomViewHolder)holder).unReadCount.setText(99+"");
        } else {
            ((ChatRoomViewHolder)holder).unReadCount.setVisibility(View.VISIBLE);
            ((ChatRoomViewHolder)holder).unReadCount.setText(unReadCount+"");
        }
        //设置消息内容
        MimeType mimeType = messageVo.getMimeType();
        if (mimeType == MimeType.CARD) {
            ((ChatRoomViewHolder)holder).chatContent.setText("[订单]");
        } else if (mimeType == MimeType.IMAGE) {
            ((ChatRoomViewHolder)holder).chatContent.setText("[图片]");
        } else if (mimeType == MimeType.AUDIO) {
            ((ChatRoomViewHolder)holder).chatContent.setText("[语音]");
        } else {
            String lastMsg = messageVo.getContent();
            ((ChatRoomViewHolder)holder).chatContent.setText(lastMsg);
        }
        //设置消息时间
        long chatTime      = MessageDBUtil.getInstance().queryLastSendTimeByShopID(shopID);
        String strChatTime = TimeUtil.getChatTime(chatTime);
        ((ChatRoomViewHolder)holder).latestChatTime.setText(strChatTime);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder{

        CircleImageView shopIcon;
        TextView        unReadCount;
        TextView        shopName;
        TextView        chatContent;
        TextView        latestChatTime;
        RecyclerItemClickListener mItemClickListener;

        public ChatRoomViewHolder(View view, RecyclerItemClickListener itemClickListener) {

            super(view);
            shopIcon       = (CircleImageView) view.findViewById(R.id.iv_shop_icon);
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


    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<MessageVo> list) {
        if (list == null) {
            this.mDatas = new ArrayList<>();
        } else {
            this.mDatas = list;
        }
        this.notifyDataSetChanged();
    }
}
