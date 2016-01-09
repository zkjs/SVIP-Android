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
import com.zkjinshi.svip.response.PrivilegeResponse;

import java.util.ArrayList;

/**
 * Created by dujiande on 2015/12/15.
 */
public class PrivilegeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<PrivilegeResponse> datalist;
    private DisplayImageOptions options;


    public ArrayList<PrivilegeResponse> getDatalist() {
        return datalist;
    }

    public void setDatalist(ArrayList<PrivilegeResponse> datalist) {
        this.datalist = datalist;
    }

    public PrivilegeAdapter(ArrayList<PrivilegeResponse> datalist, Context context){
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
            convertView = inflater.inflate(R.layout.item_new_privilege, null, false);
            vh.iconIv = (ImageView)convertView.findViewById(R.id.logo_iv);
            vh.nameTv = (TextView)convertView.findViewById(R.id.privilege_tv);
            vh.descTv = (TextView)convertView.findViewById(R.id.ad_des_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        PrivilegeResponse privilegeResponse = datalist.get(position);

        if(position < datalist.size()){
            if(!TextUtils.isEmpty(privilegeResponse.getPrivilegeName())){
                vh.nameTv.setText(privilegeResponse.getPrivilegeName());
            }

            if(!TextUtils.isEmpty(privilegeResponse.getPrivilegeDesc())){
                vh.descTv.setText(privilegeResponse.getPrivilegeDesc());
            }
            if(!TextUtils.isEmpty(privilegeResponse.getPrivilegeIcon())){
                ImageLoader.getInstance().displayImage(privilegeResponse.getPrivilegeIcon(),vh.iconIv,options);
            }
        }
       return  convertView;
    }

    public static class ViewHolder {
        public TextView nameTv;
        public TextView descTv;
        public ImageView iconIv;
    }
}
