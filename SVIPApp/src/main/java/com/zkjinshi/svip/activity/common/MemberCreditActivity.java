package com.zkjinshi.svip.activity.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 我的积分页面
 * 开发者：WinkyQin
 * 日期：2016/2/20 0020
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MemberCreditActivity extends BaseActivity {

    private ImageButton backIBtn;
    private TextView titleTv;
    private CircleImageView mCivAvatar;
    private DisplayImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_credit);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        mCivAvatar = (CircleImageView)findViewById(R.id.civ_avatar);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("积分");
    }

    private void initData() {
        String userID = CacheUtil.getInstance().getUserId();
        imageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_round_launcher)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

        ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(userID), mCivAvatar, imageOptions);

    }

    private void initListener() {
        //后退
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberCreditActivity.this.finish();
            }
        });
    }

}
