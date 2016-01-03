package com.zkjinshi.svip.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.StringUtil;

/**
 * Created by dujiande on 2015/12/28.
 */
public class ItemNumView extends LinearLayout {

    public String titleStr;
    public String valueStr;
    public String hintStr;

    public boolean isShowBtn;
    public boolean haveUpLine;
    public boolean haveDownLine;
    public boolean isMust;

    public TextView titleTv;
    public EditText valueEt;


    public ItemNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemNumView);
        titleStr = typedArray.getString(R.styleable.ItemNumView_invTitle);
        valueStr = typedArray.getString(R.styleable.ItemNumView_invValue);
        hintStr = typedArray.getString(R.styleable.ItemNumView_invHint);

        haveUpLine = typedArray.getBoolean(R.styleable.ItemNumView_invUpLine,false);
        haveDownLine = typedArray.getBoolean(R.styleable.ItemNumView_invDownLine,true);
        isMust = typedArray.getBoolean(R.styleable.ItemNumView_invIsMust,true);
        isShowBtn = typedArray.getBoolean(R.styleable.ItemNumView_invShowBtn,true);
        initView(context);
    }

    public void setValue(String valueStr){
       valueEt.setText(valueStr);
        this.valueStr = valueStr;
    }

    public String getValue(){
        this.valueStr = valueEt.getText().toString();
        return this.valueStr;
    }


    /**
     * 自定义view的初始化
     */
    private void initView(Context context){
        View.inflate(context, R.layout.item_add_num, this);
        titleTv    = (TextView)  findViewById(R.id.title_tv);
        valueEt = (EditText)  findViewById(R.id.input_et);

        setValue(valueStr);

        if(!StringUtil.isEmpty(titleStr)){
            titleTv.setText(titleStr);
        }

        if(!StringUtil.isEmpty(hintStr)){
            valueEt.setHint(hintStr);
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

        if(isMust){
            findViewById(R.id.must_tv).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.must_tv).setVisibility(View.GONE);
        }
        if(isShowBtn){
            findViewById(R.id.add_ibtn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int num = 1;
                    try{
                        num = Integer.parseInt(getValue());
                    }catch (Exception e){
                        num = 1;
                    }
                    num +=1;
                    setValue(num+"");
                }
            });
            findViewById(R.id.sub_ibtn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int num = 1;
                    try{
                        num = Integer.parseInt(getValue());
                    }catch (Exception e){
                        num = 1;
                    }
                    num -=1;
                    if(num <=0){
                        num = 1;
                    }
                    setValue(num+"");
                }
            });

        }else{
            findViewById(R.id.add_ibtn).setVisibility(View.GONE);
            findViewById(R.id.sub_ibtn).setVisibility(View.GONE);
        }


    }


}
