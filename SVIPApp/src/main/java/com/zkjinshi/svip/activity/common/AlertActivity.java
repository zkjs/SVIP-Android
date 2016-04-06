package com.zkjinshi.svip.activity.common;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;


/**
 * Created by dujiande on 2016/4/5.
 */
public class AlertActivity extends BaseActivity{

    private final static String TAG = AlertActivity.class.getSimpleName();

    private Context mContext;
    private TextView titleTv,contentTv;
    private LinearLayout contentLlt;
    private RelativeLayout mohuRlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.ad_title_tv);
        contentTv = (TextView)findViewById(R.id.ad_des_tv);
        contentLlt = (LinearLayout)findViewById(R.id.content_llt);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
    }

    private void initData() {
        String titleStr = getIntent().getStringExtra("title");
        String contentStr = getIntent().getStringExtra("content");
        titleTv.setText(titleStr);
        contentTv.setText(contentStr);

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
