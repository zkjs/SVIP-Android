package com.zkjinshi.svip.activity.common;


import android.content.Context;

import android.os.Bundle;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.qclCopy.BlurBehind;
import com.zkjinshi.svip.vo.YunBaMsgVo;

/**
 * Created by dujiande on 2016/4/5.
 */
public class BeaconMsgActivity extends BaseActivity{

    private final static String TAG = BeaconMsgActivity.class.getSimpleName();

    private Context mContext;
    private TextView titleTv,contentTv;
    private LinearLayout contentLlt;
    private RelativeLayout mohuRlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_msg);

        mContext = this;
        initView();
        initData();
        initListener();
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.ad_title_tv);
        contentTv = (TextView)findViewById(R.id.ad_des_tv);
        contentLlt = (LinearLayout)findViewById(R.id.content_llt);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
    }

    private void initData() {
        YunBaMsgVo yunBaMsgVo = (YunBaMsgVo) getIntent().getSerializableExtra("data");
        if(yunBaMsgVo != null){
            titleTv.setText(yunBaMsgVo.getTitle());
            contentTv.setText(yunBaMsgVo.getContent());
        }else{
            titleTv.setText("无标题");
            contentTv.setText("无内容");
        }
        BlurBehind.getInstance()
                .withAlpha(255)
                .withBlurRadius(5)
                .setBackground(this);
    }

    private void initListener() {
        contentLlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });
    }

}
