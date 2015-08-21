package com.zkjinshi.svip.activity.base;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;

import java.io.File;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseUiController {
    private DisplayImageOptions options;
    public BaseUiController(){
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    /**
     * 设置头像图片
     * @param photoUrl
     * @param photoImageView
     */
    public void setUserPhoto(String photoUrl,ImageView photoImageView){
        ImageLoader.getInstance().displayImage(photoUrl, photoImageView, options);
    }

    public DisplayImageOptions getOptions(){
        return options;
    }

}
