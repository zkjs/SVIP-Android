package com.zkjinshi.ritz.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.utils.StringUtil;

/**
 * Created by dujiande on 2015/12/28.
 */
public class ItemShowView extends LinearLayout {

    public String titleStr;
    public String valueStr;
    public String hintStr;

    public boolean haveUpLine;
    public boolean haveDownLine;
    public boolean isMust;
    public boolean isShowIcon;

    public TextView titleTv;
    public TextView valueTv;

    public ItemShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemShowView);
        titleStr = typedArray.getString(R.styleable.ItemShowView_isvTitle);
        valueStr = typedArray.getString(R.styleable.ItemShowView_isvValue);
        hintStr = typedArray.getString(R.styleable.ItemShowView_isvHint);

        haveUpLine = typedArray.getBoolean(R.styleable.ItemShowView_haveUpLine,false);
        haveDownLine = typedArray.getBoolean(R.styleable.ItemShowView_haveDownLine,false);
        isMust = typedArray.getBoolean(R.styleable.ItemShowView_isMust,false);
        isShowIcon = typedArray.getBoolean(R.styleable.ItemShowView_isShowIcon,true);
        initView(context);
    }

    public void setValue(String valueStr){
        if(!StringUtil.isEmpty(valueStr)){
            valueTv.setText(valueStr);
            valueTv.setTextColor(Color.parseColor("#666666"));
            this.valueStr = valueStr;
        }else if(!StringUtil.isEmpty(hintStr)){
            valueTv.setText(hintStr);
            valueTv.setTextColor(Color.parseColor("#666666"));
            this.valueStr = "";
        }
    }

    public String getValue(){
        return this.valueStr;
    }


    /**
     * 自定义view的初始化
     */
    private void initView(Context context){
        View.inflate(context, R.layout.item_show, this);
        titleTv    = (TextView)  findViewById(R.id.title_tv);
        valueTv = (TextView)  findViewById(R.id.value_tv);

        if(!StringUtil.isEmpty(titleStr)){
            titleTv.setText(titleStr);
        }

        setValue(valueStr);

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

        if(isMust){
            findViewById(R.id.must_tv).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.must_tv).setVisibility(View.GONE);
        }

        if(isShowIcon){
            findViewById(R.id.icon_iv).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.icon_iv).setVisibility(View.GONE);
        }
    }

    public void setUnClick(){
        findViewById(R.id.icon_iv).setVisibility(View.GONE);
        isShowIcon = false;
    }


}
