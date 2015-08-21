package com.zkjinshi.svip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;

/**
 * 说明：自定义标题样式
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ItemTitleView extends RelativeLayout {

    private RelativeLayout  mLeft;
    private RelativeLayout  mRight;
    private TextView        mTextTitle;//标题
    public  TextView        mRightText;//右侧文字
    private ImageView       mLeftImage;//左侧图片
    public  ImageView       mRightImage;//右侧图片

    public ItemTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemTitleView(Context context) {
        this(context, null);
    }

    public ItemTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

        String attrsNameSpace = "http://schemas.android.com/apk/res-auto";
        int    leftImage      = attrs.getAttributeResourceValue(attrsNameSpace, "leftImage", 0);
        int    rightImage     = attrs.getAttributeResourceValue(attrsNameSpace, "rightImage", 0);

        mLeftImage.setImageResource(leftImage);
        mRightImage.setImageResource(rightImage);
    }

    /**
     * 自定义view的初始化
     */
    private void initView(Context context){
        View.inflate(context, R.layout.item_title, this);
        mLeft       = (RelativeLayout)  findViewById(R.id.rl_left);
        mRight      = (RelativeLayout)  findViewById(R.id.rl_right);
        mTextTitle  = (TextView)        findViewById(R.id.tv_item_title);
        mLeftImage  = (ImageView)       findViewById(R.id.iv_left_image);
        mRightImage = (ImageView)       findViewById(R.id.iv_right_image);
        mRightText  =  (TextView)       findViewById(R.id.iv_right_text);
    }

    /**
     * 设置资源性文本内容
     * @param context
     * @param resTitle
     */
    public void setResTitle(Context context, int resTitle){
        mTextTitle.setText(context.getString(resTitle));
    }

    /**
     * 设置资源性文本内容
     * @param textTitle
     */
    public void setTextTitle(String textTitle){
        mTextTitle.setText(textTitle);
    }

    /**
     * 设置资源性文本内容
     * @param color
     */
    public void setTextColor(Context context, int color){
        mTextTitle.setTextColor(context.getResources().getColor(color));
    }

    public void setTextSize(float textSize){
        mTextTitle.setTextSize(textSize);
    }

    public RelativeLayout getmLeft() {
        return mLeft;
    }

    public RelativeLayout getmRight() {
        return mRight;
    }

}
