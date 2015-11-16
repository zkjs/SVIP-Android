package com.zkjinshi.svip.activity.order;



import com.zkjinshi.svip.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pingplusplus.android.PaymentActivity;


import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.view.ItemTitleView;



/**
 * 订单支付页面
 * 开发者：杜健德
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderPayActivity extends Activity implements View.OnClickListener{

    /**
     *开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
     *服务端生成charge 的方式可以参考ping++官方文档，地址 https://pingxx.com/guidance/server/import
     *
     *【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url 。
     * 改 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
     */
    private static String YOUR_URL ="http://218.244.151.190/demo/charge";
    public static final String URL = YOUR_URL;

    private static final int REQUEST_CODE_PAYMENT = 1;

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";



    private ItemTitleView itemTitleView;
    private TextView orderPriceTv,roomTypeTv,roomTagTv;
    private ImageButton aliPayIBtn, weChatPayIBtn;
    private TextView ccPayTv;
    private OrderDetailResponse orderDetailResponse;

    private void initView(){
        itemTitleView = (ItemTitleView)findViewById(R.id.pay_order_title_layout);
        orderPriceTv = (TextView)findViewById(R.id.pay_order_price_tv);
        roomTypeTv = (TextView)findViewById(R.id.pay_order_room_type_tv);
        roomTagTv = (TextView)findViewById(R.id.pay_order_room_tag);
        aliPayIBtn = (ImageButton)findViewById(R.id.pay_order_ali_pay_ibtn);
        weChatPayIBtn = (ImageButton)findViewById(R.id.pay_order_we_chat_pay_ibtn);
        ccPayTv = (TextView)findViewById(R.id.pay_order_cc_pay_tv);

        aliPayIBtn.setOnClickListener(this);
        weChatPayIBtn.setOnClickListener(this);
    }

    private void initData(){
        itemTitleView.setTextTitle(getString(R.string.pay));
        itemTitleView.setTextColor(this, R.color.White);
        orderDetailResponse = (OrderDetailResponse)getIntent().getSerializableExtra("orderDetailResponse");
        orderPriceTv.setText(orderDetailResponse.getRoom().getRoom_rate());
        roomTypeTv.setText(orderDetailResponse.getRoom().getRoom_type());
        roomTagTv.setText(orderDetailResponse.getRoom().getRemark());
    }

    private void initListeners(){

        //返回
        itemTitleView.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });


    }

    public void onClick(View view) {
        String amountText = orderDetailResponse.getRoom().getRoom_rate();
        if (amountText.equals("")) return;

        String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
        String cleanString = amountText.toString().replaceAll(replaceable, "");
        int amount = Integer.valueOf(new BigDecimal(cleanString).toString());

        // 支付宝，微信支付 按键的点击响应处理
       if (view.getId() == R.id.pay_order_ali_pay_ibtn) {
            new PaymentTask().execute(new PaymentRequest(CHANNEL_ALIPAY, amount));
        } else if (view.getId() == R.id.pay_order_we_chat_pay_ibtn) {
            new PaymentTask().execute(new PaymentRequest(CHANNEL_WECHAT, amount));
        }
    }

    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

        @Override
        protected void onPreExecute() {

            //按键点击之后的禁用，防止重复点击
            weChatPayIBtn.setOnClickListener(null);
            aliPayIBtn.setOnClickListener(null);
        }

        @Override
        protected String doInBackground(PaymentRequest... pr) {

            PaymentRequest paymentRequest = pr[0];
            String data = null;
            String json = new Gson().toJson(paymentRequest);
            try {
                //向Your Ping++ Server SDK请求数据
                data = postJson(URL, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        /**
         * 获得服务端的charge，调用ping++ sdk。
         */
        @Override
        protected void onPostExecute(String data) {
            if(null==data){
                showMsg("请求出错", "请检查URL", "URL无法获取charge");
                return;
            }
            Log.d("charge", data);
            Intent intent = new Intent();
            String packageName = getPackageName();
            ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
            intent.setComponent(componentName);
            intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        }

    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        weChatPayIBtn.setOnClickListener(this);
        aliPayIBtn.setOnClickListener(this);

        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        initView();
        initData();
        initListeners();
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null !=msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null !=msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private static String postJson(String url, String json) throws IOException {
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, json);
        Request request = new Request.Builder().url(url).post(body).build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    class PaymentRequest {
        String channel;
        int amount;

        public PaymentRequest(String channel, int amount) {
            this.channel = channel;
            this.amount = amount;
        }
    }

}
