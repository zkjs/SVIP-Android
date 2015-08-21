package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.SvipBaseAdapter;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.ChatRoomVo;

import java.util.List;

/**
 * 右侧消息中心聊天室适配器
 * 开发者：vincent
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomAdapter extends SvipBaseAdapter<ChatRoomVo> {

    private DisplayImageOptions options;//图片显示

    public ChatRoomAdapter(List<ChatRoomVo> datas, Activity activity) {
        super(datas, activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ChatRoomVo chatRoomVo = mDatas.get(position);
        ViewHolder holder = null;
        if(null == convertView) {
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_chat_room, null);

            holder.shopIcon    = (CircleImageView) convertView.findViewById(R.id.iv_shop_icon);
            holder.unReadCount = (TextView) convertView.findViewById(R.id.tv_unread_message_count);
            holder.shopName    = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.chatContent = (TextView) convertView.findViewById(R.id.tv_chat_content);
            holder.latestChatTime = (TextView) convertView.findViewById(R.id.tv_latest_chat_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String shopLogoUrl = Constants.GET_SHOP_LOGO + chatRoomVo.getShopid()+
                             Constants.FORMAT_PNG;
        ImageLoader.getInstance().displayImage(shopLogoUrl, holder.shopIcon, options);

        String shopID = chatRoomVo.getShopid();
        //查询出最后一条聊天对象
        holder.shopName.setText(shopID);//暂时显示shopID
        //设置消息未读条数
        long unReadCount = MessageDBUtil.getInstance().queryNotifyCountByShopID(shopID);
        if(unReadCount <= 0){
            holder.unReadCount.setVisibility(View.GONE);
        }else if(unReadCount > 99) {
            holder.unReadCount.setVisibility(View.VISIBLE);
            holder.unReadCount.setText(99+"");
        } else {
            holder.unReadCount.setVisibility(View.VISIBLE);
            holder.unReadCount.setText(unReadCount+"");
        }
        //设置消息内容
        String lastMsg = MessageDBUtil.getInstance().queryLastMsgByShopID(shopID);
        holder.chatContent.setText(lastMsg);
        //设置消息时间
        long chatTime      = MessageDBUtil.getInstance().queryLastSendTimeByShopID(shopID);
        String strChatTime = TimeUtil.getChatTime(chatTime);
        holder.latestChatTime.setText(strChatTime);
        return convertView;
    }

    static class ViewHolder{
        CircleImageView shopIcon;
        TextView        unReadCount;
        TextView        shopName;
        TextView        chatContent;
        TextView        latestChatTime;
    }
}
