package com.zkjinshi.svip.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.StringUtil;

/**
 * Created by dujiande on 2015/12/28.
 */
public class ItemCbxView extends LinearLayout {

    public String titleStr;
    public boolean valueB;
    public Drawable drawable;

    public boolean haveUpLine;
    public boolean haveDownLine;

    public TextView titleTv;
    public CheckBox valueCbx;

    public ItemCbxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemCbxView);
        titleStr = typedArray.getString(R.styleable.ItemCbxView_icvTitle);
        valueB = typedArray.getBoolean(R.styleable.ItemCbxView_icvValue,true);
        haveUpLine = typedArray.getBoolean(R.styleable.ItemCbxView_icvUpLine,false);
        haveDownLine = typedArray.getBoolean(R.styleable.ItemCbxView_icvDownLine,true);
        int resourceId = typedArray.getResourceId(R.styleable.ItemCbxView_icvLeftImage, R.mipmap.ic_zaocan_gary);
        drawable =  context.getResources().getDrawable(resourceId);
        initView(context);
    }

    /**
     * 自定义view的初始化
     */
    private void initView(Context context){
        View.inflate(context, R.layout.item_cbx, this);
        titleTv    = (TextView)  findViewById(R.id.title_tv);
        valueCbx = (CheckBox)  findViewById(R.id.value_cbx);

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        titleTv.setCompoundDrawables(drawable,null,null,null);

        if(!StringUtil.isEmpty(titleStr)){
            titleTv.setText(titleStr);
        }

        if(haveUpLine){
            findViewById(R.id.upline_v).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.upline_v).setVisibility(View.GONE);
        }

        if(haveDownLine){
            findViewById(R.id.downline_v).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.downline_v).setVisibility(View.GONE);
        }
        valueCbx.setChecked(valueB);
    }


}
