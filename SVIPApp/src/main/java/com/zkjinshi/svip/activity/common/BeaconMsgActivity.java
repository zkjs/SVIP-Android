package com.zkjinshi.svip.activity.common;


import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.qclCopy.BlurBehind;
import com.zkjinshi.svip.utils.qclCopy.OnBlurCompleteListener;
import com.zkjinshi.svip.vo.YunBaMsgVo;

/**
 * Created by dujiande on 2016/4/5.
 */
public class BeaconMsgActivity extends BaseActivity{

    private TextView titleTv,contentTv;
    private RelativeLayout mohuRlt;
    private TextView openTv;
    private LinearLayout openLayout;
    private SimpleDraweeView logoDv;
    private YunBaMsgVo yunBaMsgVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_msg);
        initView();
        initData();
        initListener();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.ad_title_tv);
        contentTv = (TextView)findViewById(R.id.ad_des_tv);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
        openTv = (TextView)findViewById(R.id.ad_open_tv);
        openLayout = (LinearLayout)findViewById(R.id.ad_open_layout);
        logoDv = (SimpleDraweeView)findViewById(R.id.imageSdv);
    }

    private void initData() {
        yunBaMsgVo = (YunBaMsgVo) getIntent().getSerializableExtra("data");
        if(yunBaMsgVo != null){
            String title = yunBaMsgVo.getTitle();
            if(!TextUtils.isEmpty(title)){
                titleTv.setText(title);
            }
            String content = yunBaMsgVo.getContent();
            if(!TextUtils.isEmpty(content)){
                contentTv.setText(content);
            }
            String marketingLogoUrl = yunBaMsgVo.getImg_url();
            if(!TextUtils.isEmpty(marketingLogoUrl)){
                Uri marketingLogoUri = Uri.parse(ConfigUtil.getInst().getImgDomain()+marketingLogoUrl);
                if(null != marketingLogoUri){
                    logoDv.setImageURI(marketingLogoUri);
                }
            }
            final String buttonUrl = yunBaMsgVo.getButton_url();
            if(!TextUtils.isEmpty(buttonUrl)){
                openLayout.setVisibility(View.VISIBLE);
                String buttonTitle = yunBaMsgVo.getButton();
                if(!TextUtils.isEmpty(buttonTitle)){
                    openTv.setText(buttonTitle);
                }
                openTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BeaconMsgActivity.this,WebViewActivity.class);
                        intent.putExtra("webview_url",buttonUrl);
                        startActivity(intent);
                        finish();
                    }
                });
            }else {
                openLayout.setVisibility(View.GONE);
            }
        }
        BlurBehind.getInstance()
                .withAlpha(255)
                .withBlurRadius(5)
                .setBackground(this);
    }


    private void initListener() {
        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(yunBaMsgVo.getInsert_time() > 0){
                    //overridePendingTransition(R.anim.anim_small_big, R.anim.anim_big_small);
                }else{
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }

            }
        });
    }

}
