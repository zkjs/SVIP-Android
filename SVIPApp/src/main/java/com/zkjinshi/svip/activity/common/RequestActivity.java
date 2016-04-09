package com.zkjinshi.svip.activity.common;


import android.content.Context;
import android.content.Intent;
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
public class RequestActivity extends BaseActivity{

    private final static String TAG = RequestActivity.class.getSimpleName();

    private Context mContext;
    private TextView titleTv,contentTv;
    private LinearLayout contentLlt;
    private RelativeLayout mohuRlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

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
//        titleTv.setText("您注册的手机号码无激活权限！\n" +
//                "若需激活，需得到本平台内合作商家的认证推荐！\n" +
//                "如果您是平台内任一合作商家的协议客户或者VIP会员，请您联系您在该商家的专属经理并提出激活申请。");
//        contentTv.setVisibility(View.GONE);

    }

    private void initListener() {
        contentLlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        contentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, WebViewActivity.class);
//                intent.putExtra("webview_url","http://zkjinshi.com/about_us/use_agree.html");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_bottom,
//                        R.anim.slide_out_top);
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
