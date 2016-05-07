package com.zkjinshi.svip.activity.common;

import android.content.Context;
import android.os.Bundle;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

/**
 * Created by dujiande on 2016/5/7.
 */
public class AreaDetailActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {


    }

    private void initListener() {

    }
}
