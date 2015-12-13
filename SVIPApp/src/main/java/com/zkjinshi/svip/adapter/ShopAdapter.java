package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.SvipBaseAdapter;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;

import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopAdapter  extends SvipBaseAdapter<ShopBean> {

    private DisplayImageOptions shopOptions;
    private DisplayImageOptions avatarOptions;

    public ShopAdapter(List datas, Activity activity) {
        super(datas, activity);
        this.shopOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_dingdanxiangqing)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_dingdanxiangqing)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_dingdanxiangqing)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        this.avatarOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_avatar_hotel)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_avatar_hotel)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_avatar_hotel)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null ==  convertView){
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_list_shop,null);
            viewHolder = new ViewHolder();
            viewHolder.ivShopLogo     = (ImageView)convertView.findViewById(R.id.iv_shop_logo);
            viewHolder.civSalerAvatar = (CircleImageView)convertView.findViewById(R.id.civ_saler_avatar);
            viewHolder.tvShopName     = (TextView) convertView.findViewById(R.id.tv_shop_name);
            viewHolder.tvShopBusiness = (TextView) convertView.findViewById(R.id.tv_shop_business);
            viewHolder.tvShopDes      = (TextView) convertView.findViewById(R.id.tv_shop_des);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ShopBean shopBean = mDatas.get(position);
        String   shopName = shopBean.getShopName();
        String   shopLogo = shopBean.getShopLogo();
        String   shopBusi = shopBean.getShopBusiness();
        String   shopDesc = shopBean.getShopDesc();
        String   shopID   = shopBean.getShopId();
        String   salesID  = shopBean.getSalesid();

        if(!TextUtils.isEmpty(shopName)){
            viewHolder.tvShopName.setText(shopName);
        }

        if(!TextUtils.isEmpty(shopBusi)){
            viewHolder.tvShopBusiness.setText(shopBusi);
        }

        if(!TextUtils.isEmpty(shopDesc)){
            viewHolder.tvShopDes.setText(shopDesc);
        }

//        if(!TextUtils.isEmpty(shopLogo)){
//            String logoUrl = ProtocolUtil.getShopLogoUrl(shopLogo);
//            ImageLoader.getInstance().displayImage(logoUrl, viewHolder.ivShopLogo, shopOptions);
//        }

        if(!TextUtils.isEmpty(salesID)){
            String avatarUrl = ProtocolUtil.getAvatarUrl(salesID);
            ImageLoader.getInstance().displayImage(avatarUrl,viewHolder.civSalerAvatar, avatarOptions);
        }
        return convertView;
    }

    static class ViewHolder{
        ImageView       ivShopLogo;
        CircleImageView civSalerAvatar;
        TextView        tvShopName;
        TextView        tvShopBusiness;
        TextView        tvShopDes;
    }
}
