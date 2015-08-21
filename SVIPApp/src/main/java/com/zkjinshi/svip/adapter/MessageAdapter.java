package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.SvipBaseAdapter;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.MessageVo;
import java.util.List;

/**
 * 消息中心
 * 开发者：vincent
 * 日期：2015/8/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageAdapter extends SvipBaseAdapter<MessageVo>{

    private DisplayImageOptions options;

    public MessageAdapter(List<MessageVo> datas, Activity activity) {
        super(datas, activity);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_hotel_anli03)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_hotel_anli03)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_hotel_anli03)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MessageVo messageVo = mDatas.get(position);
        ViewHolder holder = null;
        if(convertView == null){
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_message, null);
            holder.messageIcon        = (ImageView) convertView.findViewById(R.id.iv_message_icon);
            holder.lastesTime         = (TextView) convertView.findViewById(R.id.tv_time);
            holder.unReadMessageCount = (TextView) convertView.findViewById(R.id.tv_unread_message_count);
            holder.name               = (TextView) convertView.findViewById(R.id.tv_name);
            holder.lastesContent      = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String userAvatarUrl = Constants.GET_USER_AVATAR + messageVo.getContactId() + ".jpg";
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.messageIcon, options);
        holder.messageIcon.setImageBitmap(BitmapFactory.decodeResource(
                mActivity.getResources(), R.drawable.ic_launcher));
        holder.lastesTime.setText("时间显示");

        /** 设置未读消息数量 查询未读消息数量 */
//        if(messageVo.getUnreadCount() > 0){
//            holder.unReadMessageCount.setVisibility(View.VISIBLE);
//            holder.unReadMessageCount.setText(""+messageVo.getUnreadCount());
//        }else {
//            holder.unReadMessageCount.setVisibility(View.GONE);
//            holder.unReadMessageCount.setText("0");
//        }
        /** 如果未读消息数量大于99 显示99+ */

        holder.name.setText(messageVo.getContactName());//消息发送者姓名
        holder.lastesContent.setText(messageVo.getContent());//消息内容
        return convertView;
    }

    static class ViewHolder{
        ImageView messageIcon;
        TextView  lastesTime;//最近一次消息事件
        TextView  unReadMessageCount;//未读消息数
        TextView  name;//发送消息的姓名
        TextView  lastesContent;//最后一聊天内容
    }
}
