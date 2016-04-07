package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.preview.ScanImagesActivity;
import com.zkjinshi.svip.vo.ShopModeVo;
import com.zkjinshi.svip.vo.ShopVo;

import java.util.ArrayList;

/**
 * 商家详情适配器
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ShopModeVo> shopModeList;

    public ShopAdapter(Context context,ArrayList<ShopModeVo> shopModeList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setShopModeList(shopModeList);
    }

    public void setShopModeList(ArrayList<ShopModeVo> shopModeList) {
        if(null == shopModeList){
            this.shopModeList = new ArrayList<ShopModeVo>();
        }else {
            this.shopModeList = shopModeList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return shopModeList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopModeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.shop_desc_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.titleTv = (TextView)convertView.findViewById(R.id.shop_desc_item_tv_title);
            viewHolder.contentTv = (TextView)convertView.findViewById(R.id.shop_desc_item_tv_content);
            viewHolder.logoIv = (SimpleDraweeView)convertView.findViewById(R.id.shop_desc_item_iv_logo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ShopModeVo shopModeVo = shopModeList.get(position);
        final String title = shopModeVo.getTitle();
        if(!TextUtils.isEmpty(title)){
            viewHolder.titleTv.setText(title);
            viewHolder.titleTv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.titleTv.setVisibility(View.GONE);
        }
        String body = shopModeVo.getBody();
        if(!TextUtils.isEmpty(body)){
            viewHolder.contentTv.setText(body);
            viewHolder.contentTv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.contentTv.setVisibility(View.GONE);
        }
        final ArrayList<String> photoList = shopModeVo.getPhotos();
        if(null != photoList && !photoList.isEmpty()){
            Uri photoUri = Uri.parse(ConfigUtil.getInst().getImgDomain()+photoList.get(0));
            viewHolder.logoIv.setImageURI(photoUri);
            viewHolder.logoIv.setVisibility(View.VISIBLE);
            viewHolder.logoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context,
                            ScanImagesActivity.class);
                    it.putStringArrayListExtra(
                            ScanImagesActivity.EXTRA_IMAGE_URLS, photoList);
                    it.putExtra(ScanImagesActivity.EXTRA_IMAGE_INDEX,
                            0);
                    context.startActivity(it);
                }
            });
        }else {
            viewHolder.logoIv.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
        TextView titleTv,contentTv;
        SimpleDraweeView logoIv;
    }
}
