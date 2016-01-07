package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.pingplusplus.android.PaymentActivity;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.AddOrderResponse;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * 订单支付页面
 * 开发者：杜健德
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderPayActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = OrderPayActivity.class.getSimpleName();

    /**
     *开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
     *服务端生成charge 的方式可以参考ping++官方文档，地址 https://pingxx.com/guidance/server/import
     *
     *【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url 。
     * 改 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
     */
    private static String YOUR_URL ="http://218.244.151.190/demo/charge";
    public static final String URL = ProtocolUtil.getPingPayUrl();

    private static final int REQUEST_CODE_PAYMENT = 1;

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";

    private ImageButton aliPayIBtn, weChatPayIBtn;
    private OrderDetailForDisplay orderDetailForDisplay;

    private void initView(){
        aliPayIBtn = (ImageButton)findViewById(R.id.pay_order_ali_pay_ibtn);
        weChatPayIBtn = (ImageButton)findViewById(R.id.pay_order_we_chat_pay_ibtn);
        aliPayIBtn.setOnClickListener(this);
        weChatPayIBtn.setOnClickListener(this);
    }

    private void initData(){
        orderDetailForDisplay = (OrderDetailForDisplay)getIntent().getSerializableExtra("orderDetailForDisplay");
    }

    private void initListeners(){
        //返回
        findViewById(R.id.pay_order_top_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onClick(View view) {
        DecimalFormat df = new DecimalFormat(".00");
        String amountText = df.format(orderDetailForDisplay.getRoomprice());

        String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
        String cleanString = amountText.toString().replaceAll(replaceable, "");
        int amount = Integer.valueOf(new BigDecimal(cleanString).toString());
       // amount = amount*100;

        // 支付宝，微信支付 按键的点击响应处理
       if (view.getId() == R.id.pay_order_ali_pay_ibtn) {
            //new PaymentTask().execute(getRequest(CHANNEL_ALIPAY, amount));
           submitPay(CHANNEL_ALIPAY, amount);
        } else if (view.getId() == R.id.pay_order_we_chat_pay_ibtn) {
           // new PaymentTask().execute(getRequest(CHANNEL_WECHAT, amount));
           submitPay(CHANNEL_WECHAT, amount);
        }
    }

    private PaymentRequest getRequest(String channel,int amount){
        String orderNo = orderDetailForDisplay.getOrderno();
        PaymentRequest paymentRequest = new PaymentRequest(channel, amount);
        paymentRequest.setOrder_no(orderDetailForDisplay.getOrderno());
        paymentRequest.setClient_ip(NetWorkUtil.getLocalIpAddress(this));
       paymentRequest.setBody(orderDetailForDisplay.getShopname());
        if(orderNo.startsWith("H")){
            paymentRequest.setSubject(orderDetailForDisplay.getRoomtype() + "*" + orderDetailForDisplay.getRoomcount());
        }else if(orderNo.startsWith("K")){
            paymentRequest.setSubject(orderDetailForDisplay.getRoomtype() + "*" + orderDetailForDisplay.getRoomcount());
        }else if(orderNo.startsWith("O")){
            paymentRequest.setSubject(orderDetailForDisplay.getPersoncount()+"人");
        }
        return paymentRequest;
    }
//
//    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {
//        @Override
//        protected void onPreExecute() {
//            //按键点击之后的禁用，防止重复点击
//            weChatPayIBtn.setOnClickListener(null);
//            aliPayIBtn.setOnClickListener(null);
//        }
//
//        @Override
//        protected String doInBackground(PaymentRequest... pr) {
//
//            PaymentRequest paymentRequest = pr[0];
//            String data = null;
//            String json = new Gson().toJson(paymentRequest);
//            try {
//                //向Your Ping++ Server SDK请求数据
//                data = postJson(URL, json);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return data;
//        }
//
//        /**
//         * 获得服务端的charge，调用ping++ sdk。
//         */
//        @Override
//        protected void onPostExecute(String data) {
//            if(null==data){
//                showMsg("请求出错", "请检查URL", "URL无法获取charge");
//                return;
//            }
//            Log.d("charge", data);
//            Intent intent = new Intent();
//            String packageName = getPackageName();
//            ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
//            intent.setComponent(componentName);
//            intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
//            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//        }
//    }

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

                if(result.equals("success")){
                    paySuccessCallBack();
                }else if(result.equals("fail")){
                    showPayFailsDialog();
                }else if(result.equals("invalid")){
                    showMsg(result, errorMsg, extraMsg);
                }

            }
        }
    }

    private void paySuccessCallBack(){
        String url =  ProtocolUtil.orderPayUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("orderno", orderDetailForDisplay.getOrderno());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    AddOrderResponse addOrderResponse = new Gson().fromJson(result.rawResult,AddOrderResponse.class);
                    if(addOrderResponse.isResult()){
                        showPaySuccessDialog();
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 支付失败对话框
     */
    private void showPaySuccessDialog(){
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(getApplicationContext());
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("支付成功！");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 支付失败对话框
     */
    private void showPayFailsDialog(){
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(getApplicationContext());
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("支付失败，请重新支付！");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
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
//
//    private static String postJson(String url, String json) throws IOException {
//        MediaType type = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(type, json);
//        Request request = new Request.Builder().url(url).post(body).build();
//
//        OkHttpClient client = new OkHttpClient();
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }

    class PaymentRequest {
        /*
        order_no: string订单号
        channel:       string  支付平台    alipay:支付宝手机支 wx:微信支付
        amount :      int  人民币分单位  2元=200
        client_ip:     string    IP
        subject:       string    商店名+商品名+数量+单位   max 32 个 Unicode 字符
        body:           string    商品属性+简介+发票+其他描述+订单人  max 128 个 Unicode 字符
         */
        String order_no;
        String channel;
        int amount;
        String client_ip;
        String subject;
        String body;

        public PaymentRequest(String channel, int amount) {
            this.channel = channel;
            this.amount = amount;
        }

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getClient_ip() {
            return client_ip;
        }

        public void setClient_ip(String client_ip) {
            this.client_ip = client_ip;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    private void submitPay(String channal,int amount){
        /*
        order_no: string订单号
        channel:       string  支付平台    alipay:支付宝手机支 wx:微信支付
        amount :      int  人民币分单位  2元=200
        client_ip:     string    IP
        subject:       string    商店名+商品名+数量+单位   max 32 个 Unicode 字符
        body:           string    商品属性+简介+发票+其他描述+订单人  max 128 个 Unicode 字符
         */
        PaymentRequest paymentRequest = getRequest(channal,amount);
        String url = ProtocolUtil.getPingPayUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("order_no", paymentRequest.getOrder_no());
        bizMap.put("channel", paymentRequest.getChannel());
        bizMap.put("amount", paymentRequest.getAmount()+"");
        bizMap.put("client_ip", paymentRequest.getClient_ip());
        bizMap.put("subject", paymentRequest.getSubject());
        bizMap.put("body", paymentRequest.getBody());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                    intent.setComponent(componentName);
                    intent.putExtra(PaymentActivity.EXTRA_CHARGE,  result.rawResult);
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}
