package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.SvipBaseAdapter;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.CommentVo;

import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentAdapter extends SvipBaseAdapter<CommentVo> {

    private DisplayImageOptions mOptions;

    public CommentAdapter(List<CommentVo> datas, Activity activity) {
        super(datas, activity);

        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        CommentViewHolder holder = null;
        if (view != null) {
            holder = (CommentViewHolder) view.getTag();
        } else {
            holder = new CommentViewHolder();
            view   = View.inflate(mActivity, R.layout.item_comment_list, null);
            holder.civUserAvatar = (CircleImageView) view.findViewById(R.id.civ_user_avatar);
            holder.tvUserName    = (TextView) view.findViewById(R.id.tv_user_name);
            holder.tvCreateDate  = (TextView) view.findViewById(R.id.tv_created_date);
            holder.tvContent     = (TextView) view.findViewById(R.id.tv_comment_content);
            view.setTag(holder);
        }

        CommentVo commentVo = mDatas.get(position);
        String userID   = commentVo.getUserid();
        long   created  = commentVo.getCreateDate();
        String userName = commentVo.getUserName();
        String content  = commentVo.getContent();

        if(!TextUtils.isEmpty(userID)){
            String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
            ImageLoader.getInstance().displayImage(avatarUrl, holder.civUserAvatar, mOptions);
        }

        String createdDate = TimeUtil.getChatTime(created);
        if (!TextUtils.isEmpty(createdDate)) {
            holder.tvCreateDate.setText(createdDate);
        }

        if (!TextUtils.isEmpty(userName)) {
            holder.tvUserName.setText(userName);
        } else {
            holder.tvUserName.setText(mActivity.getString(R.string.no_name));
        }

        if (!TextUtils.isEmpty(content)) {
            holder.tvContent.setText(content);
        } else {
            holder.tvContent.setText(mActivity.getString(R.string.the_user_do_not_give_a_comment));
        }

        return view;
    }

    static class CommentViewHolder{
        CircleImageView civUserAvatar;
        TextView        tvUserName;
        TextView        tvCreateDate;
        TextView        tvContent;
    }
}
