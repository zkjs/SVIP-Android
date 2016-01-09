package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.PayAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.bean.PayBean;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/1/9.
 */
public class PayTypeActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private final static String TAG = PayTypeActivity.class.getSimpleName();

    private OrderDetailForDisplay orderDetailForDisplay;
    private ListView payListView;
    private ArrayList<PayBean> payBeanList;
    private PayAdapter payAdapter;
    private PayBean selectPay;
    private int selelectId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_type);
        orderDetailForDisplay = (OrderDetailForDisplay)getIntent().getSerializableExtra("orderDetailForDisplay");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        payListView = (ListView)findViewById(R.id.pay_listview);
    }

    private void initData() {
        selelectId = orderDetailForDisplay.getPaytype();
        payBeanList = new ArrayList<PayBean>();
        String payTypeArr[] = {"在线支付","到店支付","挂账"};
        for(int i=0;i<payTypeArr.length;i++){
            PayBean payBean = new PayBean();
            payBean.setPay_id(i+1);
            payBean.setPay_name(payTypeArr[i]);
            payBeanList.add(payBean);
        }
        payAdapter = new PayAdapter(PayTypeActivity.this,payBeanList,selelectId);
        payListView.setAdapter(payAdapter);
        payListView.setOnItemClickListener(PayTypeActivity.this);
    }

    private void initListener() {
        findViewById(R.id.header_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    //获取支付方式名字
    public String getPayName(String id){
        String payName = "未设定";
        if(payBeanList != null){
            for(PayBean payBean : payBeanList){
                if(payBean.getPay_id() == Integer.parseInt(id)){
                    payName = payBean.getPay_name();
                    break;
                }
            }
        }
        return payName;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        payAdapter.setCheckidByPosition(position);
        payAdapter.notifyDataSetChanged();
        selectPay = payAdapter.gePayByPosition(position);
        selelectId = selectPay.getPay_id();

        Intent data = new Intent();
        data.putExtra("payment", selelectId+"");
        data.putExtra("payment_name", getPayName(selelectId+""));
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
