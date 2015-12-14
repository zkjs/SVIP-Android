package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.vo.HomeMsgVo;

import java.util.ArrayList;



/**
 * Created by dujiande on 2015/12/13.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HomeMsgVo> datalist;
    private DisplayImageOptions options;



    public HomeAdapter(ArrayList<HomeMsgVo> datalist, Context context){
        this.context = context;
        this.datalist = datalist;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_dingwei_orange)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_dingwei_orange)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_dingwei_orange)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_home_card, null);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeMsgVo homeMsgVo = datalist.get(position);
        if(TextUtils.isEmpty(homeMsgVo.getMajorText())){
            holder.majorText.setText("");
        }else{
            holder.majorText.setText(homeMsgVo.getMajorText());
        }

        if(TextUtils.isEmpty(homeMsgVo.getMinorText())){
            holder.minorText.setText("");
        }else{
            holder.minorText.setText(homeMsgVo.getMinorText());
        }

        if(homeMsgVo.isClickAble()){
            holder.clickIv.setVisibility(View.VISIBLE);
        }else{
            holder.clickIv.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(homeMsgVo.getIcon())){
            if(homeMsgVo.getMsgType() == HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT){
                holder.iconIv.setImageResource(R.mipmap.ic_liwu_orange);
            }if(homeMsgVo.getMsgType() == HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION){
                holder.iconIv.setImageResource(R.mipmap.ic_dingwei_orange);
            }
        }else{
            String path = homeMsgVo.getIcon();
            ImageLoader.getInstance().displayImage(path,holder.iconIv,options);
        }


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconIv;
        public ImageView clickIv;
        public TextView majorText;
        public TextView minorText;

        public ViewHolder(View view) {
            super(view);
            iconIv = (ImageView)view.findViewById(R.id.item_icon_iv);
            clickIv = (ImageView)view.findViewById(R.id.item_click_iv);
            majorText = (TextView)view.findViewById(R.id.major_text_tv);
            minorText = (TextView)view.findViewById(R.id.minor_text_tv);
        }
    }



}
