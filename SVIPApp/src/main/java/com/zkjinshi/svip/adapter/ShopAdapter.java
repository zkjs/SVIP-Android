package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.media.Image;
import android.support.v4.view.ViewPager;
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
import com.zkjinshi.svip.response.ShopListResponse;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopAdapter  extends SvipBaseAdapter<ShopListResponse> {

    private DisplayImageOptions options;

    public ShopAdapter(List datas, Activity activity) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null ==  convertView){
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_list_shop,null);
            viewHolder = new ViewHolder();
            viewHolder.shopLogoIv = (ImageView)convertView.findViewById(R.id.list_hotel_logo_civ);
            viewHolder.shopNamtTv = (TextView)convertView.findViewById(R.id.list_hotel_name_tv);
            viewHolder.park = (ImageView)convertView.findViewById(R.id.list_hotel_park_iv);
            viewHolder.wifi = (ImageView)convertView.findViewById(R.id.list_hotel_wifi_iv);
            viewHolder.shopStar = (TextView)convertView.findViewById(R.id.list_hotel_level_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ShopListResponse shopInfoVo = mDatas.get(position);
        String shopName = shopInfoVo.getFullname();
        if(!TextUtils.isEmpty(shopName)){
            viewHolder.shopNamtTv.setText(shopName);
        }
        String logo = shopInfoVo.getLogo();
        if(!TextUtils.isEmpty(logo)){
            String logoUrl = ProtocolUtil.getShopLogoUrl(logo);
            ImageLoader.getInstance().displayImage(logoUrl,viewHolder.shopLogoIv,options);
        }

        if(shopInfoVo.getPark() == 1){
            viewHolder.park.setVisibility(View.VISIBLE);
        }else{
            viewHolder.park.setVisibility(View.INVISIBLE);
        }

        if(shopInfoVo.getWifi() == 1){
            viewHolder.wifi.setVisibility(View.VISIBLE);
        }else{
            viewHolder.wifi.setVisibility(View.INVISIBLE);
        }
        viewHolder.shopStar.setText(shopInfoVo.getStar()+"星级");

        return convertView;
    }

    static class ViewHolder{
        ImageView shopLogoIv;
        TextView shopNamtTv;

        TextView shopStar;
        ImageView wifi;
        ImageView park;
    }
}
