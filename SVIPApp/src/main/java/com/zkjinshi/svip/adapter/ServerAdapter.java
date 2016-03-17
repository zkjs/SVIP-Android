package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现中的服务员适配器
 * 开发者：WinkyQin
 * 日期：2015/11/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {

    private Activity                  mActivity;
    private List<CustomerServiceBean> mDatas;
    private DisplayImageOptions       mOptions;

    public ServerAdapter(Activity activity, List<CustomerServiceBean> datas) {
        this.mActivity = activity;
        if (null == datas) {
            this.mDatas = new ArrayList<>();
        } else {
            this.mDatas = datas;
        }
        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.bg_biaoqian)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.bg_biaoqian)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.bg_biaoqian)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public ServerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_server, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ServerAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        CustomerServiceBean server = mDatas.get(position);
        // Set item views based on the data model
        String avatarUrl = ProtocolUtil.getAvatarUrl(server.getUserid());
        ImageLoader.getInstance().displayImage(avatarUrl, holder.ivServerAvatar, mOptions);
        holder.tvServerName.setText(server.getUsername());
        holder.tvServerDesc.setText(server.getPhone());
        holder.tvShopName.setText(server.getShopid());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivServerAvatar;//服务员头像
        TextView  tvServerName;  //服务员姓名
        TextView  tvServerDesc;  //服务员描述
        TextView  tvShopName;    //商家名称

        public ViewHolder(View itemView) {
            super(itemView);
            ivServerAvatar = (ImageView) itemView.findViewById(R.id.iv_server_avatar);
            tvServerName   = (TextView)  itemView.findViewById(R.id.tv_server_name);
            tvServerDesc   = (TextView)  itemView.findViewById(R.id.tv_server_desc);
            tvShopName     = (TextView)  itemView.findViewById(R.id.tv_shop_name);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<CustomerServiceBean> list) {
        mDatas.addAll(list);
        notifyDataSetChanged();
    }
}