package com.zkjinshi.svip.activity.tips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.vo.TipsResultVo;
import com.zkjinshi.svip.vo.WaiterVo;

/**
 * 小费支付结果页面
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TipSuccesActivity extends BaseActivity {

    private TipsResultVo tipsResultVo;
    private ImageButton backIBtn;
    private TextView titleTv;
    private SimpleDraweeView waiterPhotoDv;
    private TextView waiterNameTv,priceTv;
    private Button finishBtn;

    private void initView(){
        titleTv = (TextView) findViewById(R.id.title_tips_tv);
        backIBtn = (ImageButton)findViewById(R.id.btn_tips_back);
        waiterPhotoDv = (SimpleDraweeView)findViewById(R.id.iv_tip_succ_waiter_photo);
        waiterNameTv = (TextView)findViewById(R.id.tv_tip_succ_waiter_name);
        priceTv = (TextView)findViewById(R.id.tv_tip_succ_price);
        finishBtn = (Button)findViewById(R.id.tv_tip_finish);
    }

    private void initData(){
        titleTv.setText("成功");
        if(null != getIntent() && null != getIntent().getSerializableExtra("tipsResultVo")){
            tipsResultVo = (TipsResultVo)getIntent().getSerializableExtra("tipsResultVo");
            double price = tipsResultVo.getPrice();
            priceTv.setText("¥"+price);
            WaiterVo waiterVo = tipsResultVo.getWaiterVo();
            if(null != waiterVo){
                String userName = waiterVo.getUsername();
                if(!TextUtils.isEmpty(userName)){
                    waiterNameTv.setText(userName);
                }
                String userImg = waiterVo.getUserimage();
                if(!TextUtils.isEmpty(userImg)){
                    Uri photoUri = Uri.parse(ConfigUtil.getInst().getImgDomain()+userImg);
                    if(null != photoUri){
                        waiterPhotoDv.setImageURI(photoUri);
                    }
                }
            }
        }
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.getInst().clearLeaveTop();
            }
        });

        //完成
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.getInst().clearLeaveTop();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_succ);
        initView();
        initData();
        initListeners();
    }
}
