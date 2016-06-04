package com.zkjinshi.ritz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.activity.preview.ScanImagesActivity;
import com.zkjinshi.ritz.fragment.ShopFragment;
import com.zkjinshi.ritz.vo.ShopModeVo;
import com.zkjinshi.ritz.vo.ShopVo;

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
    private GestureDetector detector;
    FlingListeber listener;
    private ShopModeVo clickItem = null;

   public ShopFragment shopFragment = null;

    public ShopAdapter(Context context,ArrayList<ShopModeVo> shopModeList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setShopModeList(shopModeList);
        listener = new FlingListeber();
        detector = new GestureDetector(listener);
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
        final ShopModeVo shopModeVo = shopModeList.get(position);
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
            Uri photoUri = Uri.parse(ConfigUtil.getInst().getCdnDomain()+photoList.get(0));
            viewHolder.logoIv.setImageURI(photoUri);
            viewHolder.logoIv.setVisibility(View.VISIBLE);
            viewHolder.logoIv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    clickItem = shopModeVo;
                    return detector.onTouchEvent(motionEvent);
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

    class FlingListeber implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if(e2.getX()-e1.getX()>20){
               // Toast.makeText(context, "左滑", Toast.LENGTH_SHORT).show();
                if(shopFragment != null){
                    //shopFragment.hideAction();
                }

            }else if(e1.getX()-e2.getX()>20){
                //Toast.makeText(context, "右滑", Toast.LENGTH_SHORT).show();

            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {


        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {


        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(clickItem == null){
                return false;
            }
            ArrayList<String> photoList = clickItem.getPhotos();
            Intent it = new Intent(context,
                    ScanImagesActivity.class);
            it.putStringArrayListExtra(
                    ScanImagesActivity.EXTRA_IMAGE_URLS, photoList);
            it.putExtra(ScanImagesActivity.EXTRA_IMAGE_INDEX,
                    0);
            context.startActivity(it);
            return false;
        }

    }
}
