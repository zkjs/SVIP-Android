package com.zkjinshi.svip.activity.call;



import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.zkjinshi.svip.R;

import com.zkjinshi.svip.base.BaseActivity;


/**
 * Created by dujiande on 2016/4/5.
 */
public class CallEvaluateActivity extends BaseActivity{

    private TextView nameTv,serviceTv;
    private RelativeLayout mohuRlt;
    private SimpleDraweeView avatarSdv;
    private RatingBar ratingBar;
    private Button confirmBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_evaluate);

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
        nameTv = (TextView)findViewById(R.id.name_tv);
        serviceTv = (TextView)findViewById(R.id.service_tv);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
        avatarSdv = (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {
        confirmBtn.setVisibility(View.GONE);
    }


    private void initListener() {
        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                confirmBtn.setVisibility(View.VISIBLE);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
