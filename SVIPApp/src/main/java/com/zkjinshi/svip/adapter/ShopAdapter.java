package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.utils.ProtocolUtil;
import java.util.ArrayList;


/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopAdapter extends BaseAdapter {

    private final static int ITEM_VIEW_COUNT  = 3;
    public  final static int ITEM_ADVERTISE   = 0x00;
    public  final static int ITEM_NORMAL_SHOP = 0x01;
    public  final static int ITEM_OTHER       = 0x02;

    private ArrayList<ShopBean> shopList;
    private Context context;
    private LayoutInflater inflater;

    public void setShopList(ArrayList<ShopBean> shopList) {
        if(null == shopList){
            this.shopList = new ArrayList<ShopBean>();
        }else {
            this.shopList = shopList;
        }
        notifyDataSetChanged();
    }

    public ShopAdapter(ArrayList<ShopBean> shopList,Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setShopList(shopList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null ==  convertView){
            convertView = inflater.inflate(R.layout.item_list_shop, null);
            viewHolder = new ViewHolder();
            viewHolder.ivShopLogo     = (SimpleDraweeView)convertView.findViewById(R.id.iv_shop_logo);
            viewHolder.civSalerAvatar = (SimpleDraweeView)convertView.findViewById(R.id.civ_saler_avatar);
            viewHolder.vWhiteLine     = convertView.findViewById(R.id.v_white_line);
            viewHolder.tvShopName     = (TextView) convertView.findViewById(R.id.tv_shop_name);
            viewHolder.tvShopBusiness = (TextView) convertView.findViewById(R.id.tv_shop_business);
            viewHolder.tvShopDes      = (TextView) convertView.findViewById(R.id.tv_shop_des);
            viewHolder.tvShopAdd      = (TextView) convertView.findViewById(R.id.tv_shop_add);
            viewHolder.llShopInfo     = (LinearLayout) convertView.findViewById(R.id.ll_shop_info);
            viewHolder.rlSalerInfo    = (RelativeLayout) convertView.findViewById(R.id.rl_saler_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ShopBean shopBean = shopList.get(position);
        String   shopName = shopBean.getShopname();
        String   shopBusi = shopBean.getShopbusiness();
        String   shopAdd  = shopBean.getShopaddress();
        final String   salesID  = shopBean.getSalesid();
        String   imgUrl   = shopBean.getBgImgUrl();

        if (ITEM_ADVERTISE == getItemViewType(position)) {
            viewHolder.vWhiteLine.setVisibility(View.VISIBLE);
            viewHolder.llShopInfo.setVisibility(View.GONE);
            viewHolder.rlSalerInfo.setVisibility(View.GONE);

            if(!TextUtils.isEmpty(imgUrl)){
                viewHolder.ivShopLogo.setImageURI(Uri.parse(ProtocolUtil.getHostImgUrl(imgUrl)));
            }

            if(!TextUtils.isEmpty(shopName)){
                viewHolder.tvShopName.setText(shopName);
            }

            if(!TextUtils.isEmpty(shopBean.getShoptitle())){
                viewHolder.tvShopBusiness.setText(shopBean.getShoptitle());
            }

        } else {

            if(!TextUtils.isEmpty(shopName)){
                viewHolder.tvShopName.setText(shopName);
            }

            if(!TextUtils.isEmpty(shopBean.getShoptitle())){
                viewHolder.tvShopBusiness.setText(shopBean.getShoptitle());
            }

            if(!TextUtils.isEmpty(shopBean.getRecommtitle())){
                viewHolder.tvShopDes.setText(shopBean.getRecommtitle());
            }

            if(!TextUtils.isEmpty(shopAdd)){
                viewHolder.tvShopAdd.setText(shopAdd);
            }

            if(!TextUtils.isEmpty(imgUrl)){
                viewHolder.ivShopLogo.setImageURI(Uri.parse(ProtocolUtil.getHostImgUrl(imgUrl)));
            }

            String shopLogo = shopBean.getShoplogo();
            if(!TextUtils.isEmpty(shopLogo)){
                viewHolder.civSalerAvatar.setImageURI(Uri.parse(ProtocolUtil.getHostImgUrl(shopLogo)));
            }
        }

        return convertView;
    }

    static class ViewHolder{
        SimpleDraweeView ivShopLogo;
        SimpleDraweeView civSalerAvatar;
        TextView        tvShopName;
        TextView        tvShopBusiness;
        TextView        tvShopDes;
        TextView        tvShopAdd;
        View            vWhiteLine;
        LinearLayout    llShopInfo;
        RelativeLayout  rlSalerInfo;
    }



    @Override
    public int getItemViewType(int position) {
        Object obj = shopList.get(position);
        if(obj instanceof ShopBean){
            String shopID = ((ShopBean) obj).getShopid();
            if(!TextUtils.isEmpty(shopID)){
                return ITEM_NORMAL_SHOP;//商家对象
            } else {
                return ITEM_ADVERTISE;//广告栏
            }
        } else {
            return ITEM_OTHER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_COUNT;
    }

    @Override
    public int getCount() {
        return shopList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
