package com.zkjinshi.svip.factory;

import android.text.TextUtils;

import com.zkjinshi.svip.bean.ImageBean;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ImageFactory {

    private static ImageFactory instance;

    private ImageFactory(){}

    public synchronized static ImageFactory getInstance(){
        if(null == instance){
            instance = new ImageFactory();
        }
        return instance;
    }

    public ArrayList<String> buildImageList(ArrayList<ImageBean> imageVoList){
        ArrayList<String> imageList = new ArrayList<String>();
        if(null != imageVoList && !imageVoList.isEmpty()){
            String imageUrl = null;
            for(ImageBean imageVo : imageVoList){
                imageUrl = imageVo.getImageUrl();
                if(!TextUtils.isEmpty(imageUrl)){
                    imageList.add(imageUrl);
                }
            }
        }
        return imageList;
    }
}
