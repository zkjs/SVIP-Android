package com.zkjinshi.svip.fragment.contacts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.RandomDrawbleUtil;
import com.zkjinshi.svip.view.CircleImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsSortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                                  implements SectionIndexer {

    private List<SortModel> mList;
    private Context         mContext;
    private DisplayImageOptions options;

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public ContactsSortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.mList    = list;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(Color.TRANSPARENT)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(Color.TRANSPARENT)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();

    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_cilent, null);
        //设置条目宽度满足屏幕
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT));
        ClientViewHolder clientHolder = new ClientViewHolder(view, mRecyclerItemClickListener);
        return clientHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        //根据position获取分类的首字母的Char ascii值
        final SortModel sortModel = mList.get(position);
        int   section = getSectionForPosition(position);

        ClientViewHolder myHolder = (ClientViewHolder)holder;

        //是否显示首字母
        if (position == getPositionForSection(section)) {
            myHolder.tvLetter.setVisibility(View.VISIBLE);
            String sortLetter = sortModel.sortLetters;
            myHolder.tvLetter.setText(sortLetter);
        } else {
            myHolder.tvLetter.setVisibility(View.GONE);
        }

        ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(sortModel.fuid),
                myHolder.civContactAvatar,options);

        //显示客户名称
        String clientName = sortModel.name;
        if(!TextUtils.isEmpty(clientName)){
            myHolder.tvContactAvatar.setText(clientName.substring(0, 1));
            myHolder.tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
            myHolder.tvContactName.setText(clientName);
        }
        //显示酒店名
        if(!TextUtils.isEmpty(sortModel.shopName)){
            myHolder.tvContactOnShop.setText(sortModel.shopName);
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactDes;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
        public TextView         tvContactOnShop;

        private RecyclerItemClickListener mItemClickListener;

        public ClientViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar  = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            tvContactDes     = (TextView) view.findViewById(R.id.tv_contact_des);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
            tvContactOnShop    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
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

    public int getSectionForPosition(int position) {
        return mList.get(position).sortLetters.charAt(0);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
