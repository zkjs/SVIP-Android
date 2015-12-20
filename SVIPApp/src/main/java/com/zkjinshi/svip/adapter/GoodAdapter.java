package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
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
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.ShopInfoVo;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodAdapter extends SvipBaseAdapter<GoodInfoVo> {

    private DisplayImageOptions options;
    private Map<String,Boolean> selectMap;

    public GoodAdapter(List datas, Activity activity) {
        super(datas, activity);
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_list_good,null);
            viewHolder = new ViewHolder();
            viewHolder.roomTypeTv = (TextView)convertView.findViewById(R.id.list_room_type_tv);
            viewHolder.roomPicTv = (ImageView)convertView.findViewById(R.id.list_room_pic_iv);
            viewHolder.selectedPicTv = (ImageView)convertView.findViewById(R.id.list_selected_pic_iv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GoodInfoVo goodInfoVo = mDatas.get(position);
        String roomStr = goodInfoVo.getRoom();
        String type = goodInfoVo.getType();

        viewHolder.roomTypeTv.setText(roomStr + type);

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
            selectMap.clear();
            selectMap.put(goodId, true);
            notifyDataSetChanged();
        }
    }
}
