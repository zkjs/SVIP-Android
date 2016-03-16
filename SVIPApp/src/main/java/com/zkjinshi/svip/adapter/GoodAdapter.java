package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.OrderForDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodInfoVo> goodList;
    private String selectId = "-1";
    private String selectName = "-1";

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public void setGoodList(ArrayList<GoodInfoVo> goodList) {
        if(null == goodList){
            this.goodList = new ArrayList<GoodInfoVo>();
        }else {
            this.goodList = goodList;
        }
        notifyDataSetChanged();
    }

    public ArrayList<GoodInfoVo> getGoodList() {
        return goodList;
    }

    public void loadMore(ArrayList<GoodInfoVo> morelist){
        goodList.addAll(morelist);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<GoodInfoVo> refreshlist){
        goodList = refreshlist;
        notifyDataSetChanged();
    }

    public GoodAdapter(Context context, ArrayList<GoodInfoVo> goodList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setGoodList(goodList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null ==  convertView){
            convertView = inflater.inflate(R.layout.item_list_good,null);
            viewHolder = new ViewHolder();
            viewHolder.roomTypeTv = (TextView)convertView.findViewById(R.id.list_room_type_tv);
            viewHolder.roomPicTv = (SimpleDraweeView)convertView.findViewById(R.id.list_room_pic_iv);
            viewHolder.selectedPicTv = (ImageView)convertView.findViewById(R.id.list_selected_pic_iv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GoodInfoVo goodInfoVo = goodList.get(position);

        viewHolder.roomTypeTv.setText(goodInfoVo.getName());
        String imageUrl = goodInfoVo.getImgurl();
        if(!TextUtils.isEmpty(imageUrl)){
            String logoUrl = ProtocolUtil.getHostImgUrl(imageUrl);
            viewHolder.roomPicTv.setImageURI(Uri.parse(logoUrl));
        }

        String id = goodInfoVo.getId();
        String name = goodInfoVo.getName();
        if(id.equals(selectId) || name.equals(selectName)){
            //选中
            viewHolder.roomPicTv.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.selectedPicTv.setVisibility(View.VISIBLE);
        }else{
            //未选中
            viewHolder.roomPicTv.setColorFilter(null);
            viewHolder.selectedPicTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder{
        TextView roomTypeTv;
        ImageView selectedPicTv;
        SimpleDraweeView roomPicTv;
    }
    @Override
    public int getCount() {
        return goodList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
