package com.zkjinshi.svip.activity.facepay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.PayRecordDataVo;

/**
 * Created by dujiande on 2016/3/8.
 */
public class PayDetailActivity extends Activity {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    private ImageButton backBtn;
    private TextView titleTv;

    private TextView hotelNameTv,priceTv,datetimeTv,orderNoTv;

    private PayRecordDataVo payRecordDataVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);

        hotelNameTv = (TextView)findViewById(R.id.shopname_tv);
        priceTv = (TextView)findViewById(R.id.price_tv);
        datetimeTv = (TextView)findViewById(R.id.time_tv);
        orderNoTv = (TextView)findViewById(R.id.orderno_tv);

    }

    private void initData() {
        titleTv.setText("支付明细");
        payRecordDataVo = (PayRecordDataVo)getIntent().getSerializableExtra("payRecordDataVo");
        if(payRecordDataVo != null){
            hotelNameTv.setText(payRecordDataVo.getShopname());
            priceTv.setText("¥ "+payRecordDataVo.getAmount());
            datetimeTv.setText(payRecordDataVo.getConfirmtime());
            orderNoTv.setText(payRecordDataVo.getPaymentno());
        }

    }
}
