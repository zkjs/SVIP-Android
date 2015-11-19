package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zkjinshi.svip.R;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 开发者：dujiande
 * 日期：2015/11/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityActivity extends Activity{
    private final static String TAG = CityActivity.class.getSimpleName();

    private TagView gpsTagView;
    private TagView lastTagView;
    private TagView hotTagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        gpsTagView   = (TagView)findViewById(R.id.tagview_gps_city);
        lastTagView   = (TagView)findViewById(R.id.tagview_last_city);
        hotTagView   = (TagView)findViewById(R.id.tagview_hot_city);
    }

    private void initData() {
        String gpsCity[] = {"深圳","清远","长沙"};
        String lastCity[] = {"深圳","广州","长沙"};
        String hotCity[] = {"深圳","上海","长沙","北京","广州","杭州","南京","厦门","郑州"};

        for(int i=0;i<gpsCity.length;i++){
            gpsTagView.addTag(createTag(i,gpsCity[i]));
        }

        for(int i=0;i<lastCity.length;i++){
            lastTagView.addTag(createTag(i,lastCity[i]));
        }

        for(int i=0;i<hotCity.length;i++){
            hotTagView.addTag(createTag(i,hotCity[i]));
        }

    }

    private void initListener() {
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private Tag createTag(int id,String tagstr){
        Tag tag = new Tag(id,tagstr);
        tag.tagTextColor = Color.parseColor("#333333");
        tag.layoutColor =  Color.parseColor("#ffffff");
        tag.layoutColorPress = Color.parseColor("#DDDDDD");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 5f;
        tag.tagTextSize = 15f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#bbbbbb");
        tag.isDeletable = false;
        return tag;
    }
}
