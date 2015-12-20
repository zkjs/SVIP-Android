package com.zkjinshi.svip.adapter;

import android.content.Context;
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
import com.zkjinshi.svip.vo.HomeMsgVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2015/12/15.
 */
public class HomeMsgAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<HomeMsgVo> datalist;
    private DisplayImageOptions options;


    public ArrayList<HomeMsgVo> getDatalist() {
        return datalist;
    }

    public void setDatalist(ArrayList<HomeMsgVo> datalist) {
        this.datalist = datalist;
    }

    public HomeMsgAdapter(ArrayList<HomeMsgVo> datalist, Context context){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.datalist = datalist;
        this.options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_dingwei_orange)// 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.mipmap.ic_dingwei_orange)// 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.mipmap.ic_dingwei_orange)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_home_card, null, false);
            vh.iconIv = (ImageView)convertView.findViewById(R.id.item_icon_iv);
            vh.clickIv = (ImageView)convertView.findViewById(R.id.item_click_iv);
            vh.majorText = (TextView)convertView.findViewById(R.id.major_text_tv);
            vh.minorText = (TextView)convertView.findViewById(R.id.minor_text_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if(position < datalist.size()){
            HomeMsgVo homeMsgVo = datalist.get(position);
            if(TextUtils.isEmpty(homeMsgVo.getMajorText())){
                vh.majorText.setText("");
            }else{
                vh.majorText.setText(homeMsgVo.getMajorText());
            }

            if(TextUtils.isEmpty(homeMsgVo.getMinorText())){
                vh.minorText.setText("");
            }else{
                vh.minorText.setText(homeMsgVo.getMinorText());
            }

            if(homeMsgVo.isClickAble()){
                vh.clickIv.setVisibility(View.VISIBLE);

            }else{
                vh.clickIv.setVisibility(View.INVISIBLE);
            }

            if(TextUtils.isEmpty(homeMsgVo.getIcon())){
                if(homeMsgVo.getMsgType() == HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT){
                    vh.iconIv.setImageResource(R.mipmap.ic_liwu_orange);
                }else if(homeMsgVo.getMsgType() == HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION){
                    vh.iconIv.setImageResource(R.mipmap.ic_dingwei_orange);
                }else if(homeMsgVo.getMsgType() == HomeMsgVo.HomeMsgType.HOME_MSG_PRIVILEDGE){
                    vh.iconIv.setImageResource(R.mipmap.ic_v_orange);
                }
            }else{
                String path = homeMsgVo.getIcon();
                ImageLoader.getInstance().displayImage(path,vh.iconIv,options);
            }
        }
       return  convertView;
    }

    public static class ViewHolder {
        public ImageView iconIv;
        public ImageView clickIv;
        public TextView majorText;
        public TextView minorText;
    }
}
