package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.GoodInfoVo;

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

    private DisplayImageOptions options;
    private Map<String,Boolean> selectMap;

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodInfoVo> goodList;

    public void setGoodList(ArrayList<GoodInfoVo> goodList) {
        if(null == goodList){
            this.goodList = new ArrayList<GoodInfoVo>();
        }else {
            this.goodList = goodList;
        }
        notifyDataSetChanged();
    }

    public GoodAdapter(Context context,ArrayList<GoodInfoVo> goodList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setGoodList(goodList);
        this.options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        this.selectMap = new HashMap<String,Boolean>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null ==  convertView){
            convertView = inflater.inflate(R.layout.item_list_good,null);
            viewHolder = new ViewHolder();
            viewHolder.roomTypeTv = (TextView)convertView.findViewById(R.id.list_room_type_tv);
            viewHolder.roomPicTv = (ImageView)convertView.findViewById(R.id.list_room_pic_iv);
            viewHolder.selectedPicTv = (ImageView)convertView.findViewById(R.id.list_selected_pic_iv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GoodInfoVo goodInfoVo = goodList.get(position);
        String roomStr = goodInfoVo.getRoom();
        String type = goodInfoVo.getType();

        viewHolder.roomTypeTv.setText(roomStr);

        String imageUrl = goodInfoVo.getImgurl();
        if(!TextUtils.isEmpty(imageUrl)){
            String logoUrl = ProtocolUtil.getGoodImgUrl(imageUrl);
            ImageLoader.getInstance().displayImage(logoUrl,viewHolder.roomPicTv,options);
        }

        String id = goodInfoVo.getId();

        if(null != selectMap && selectMap.containsKey(id)){
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
        ImageView roomPicTv,selectedPicTv;
    }

    public Map<String, Boolean> getSelectMap() {
        return selectMap;
    }

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
    }

    /**
     * 挑选商品
     * @param goodId
     */
    public void selectGood(String goodId){
        if(null != selectMap){
            if(selectMap.containsKey(goodId)){
                selectMap.clear();
            }else{
                selectMap.clear();
                selectMap.put(goodId, true);
            }
            notifyDataSetChanged();
        }
    }

    public boolean checkIsEmpty(){
        return  selectMap.isEmpty();
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
