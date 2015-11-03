package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.ItemTitleView;

/**
 * 完善新用户资料
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeActivity extends Activity {

    private ItemTitleView mTitle;
    private EditText      mEtInviteCode;
    private TextView      mTvInviteCode;
    private ImageButton   mIbtnQianJin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        //进入主页面
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle        = (ItemTitleView) findViewById(R.id.itv_title);
        mEtInviteCode = (EditText)      findViewById(R.id.et_invite_code);
        mTvInviteCode = (TextView)      findViewById(R.id.tv_what_is_invite_code);
        mIbtnQianJin  = (ImageButton)   findViewById(R.id.ibtn_qian_jin);
    }

    private void initData() {
        mTitle.setTextTitle("");
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodeActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mTvInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(InviteCodeActivity.this, "TODO");
            }
        });

        mIbtnQianJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                String inviteCode = mEtInviteCode.getText().toString().trim();
                if(!TextUtils.isEmpty(inviteCode)){

                }else {
                    Intent goHome = new Intent(InviteCodeActivity.this, MainActivity.class);
                    startActivity(goHome);
                    InviteCodeActivity.this.finish();
                }
            }
        });
    }
}
