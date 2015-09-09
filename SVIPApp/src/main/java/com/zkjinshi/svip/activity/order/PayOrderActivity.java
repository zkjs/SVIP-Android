package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.MathUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.utils.PayResult;
import com.zkjinshi.svip.utils.SignUtils;
import com.zkjinshi.svip.view.ItemTitleView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 订单支付页面
 * 开发者：JimmyZhang
 * 日期：2015/7/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayOrderActivity extends Activity{

    //商户PID
    public static final String PARTNER = "2088021086279355";
    //商户收款账号
    public static final String SELLER = "caozhao@zkjinshi.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK3gPktnK5WlcVZRIFBOP6qk+xOqdECVaoFu4rxY7UWyLXE6tDcDDLHwK3QG58gwd47W9x3YOUMk5lOo84RzZa1RwqXcgaXcZwhquIqqRRCq846dR4sm1WhN5zubN2mIRSarSl3gEniX9X2C7JDkh+cpNJQ5s/pTq6hvHQr4dondAgMBAAECgYB9MGFpxOSaoD3UGiEb8NriMFseM7Hz9iBzBLV3Sse7UKhrSAsNOMLhMrz2kyo69rp+O8Q81ruT3nN/dLuwd62SRqPPJTZf3JdyyV7ap6R2iO5eyxxSri8V7Q0cAlpP0mrGSOhiOLcpR/RsS3KJp2bMyYan6Xkp9QexMJkfwOGduQJBANviki1mAD/4+1PJ3WjMuEcSaV7X+KWw7vsrZm59Bu4DUSlKdn2pAZA8yRlImBj4233eIe4numgpUqBoNQjuls8CQQDKbyTrkhC8CqmNSFi8tL06kOf+oLdUM0NGdLu7mOCYvuUlNjznSu5hrNq86jlYnXcRSGXcClv9VrQTmHsgVz+TAkAU0LH90jYb2DoSiH2JOjgHolqPO+qx5Ln61PTxaKyLQ40fV4k4BBO9z8NJvXGIi6Zbl/emT+R5j8/el37NxahJAkEAmQJpZaWCAKAOiDum1vvGC/57XHseFwaoUxjozWNAYDPp/7Z/UlPQ2wNMUn632cMrvGR8mVU7MsHWWvlmF4vbAQJAUUWguvqXVog0Z7uvGHLZdZKHulVoCLlSupzNTUDxCeU4viTjS0ZUyM/lGW163D2ISeG8XGh0z5nANgVbdB0vgA==";
    //支付宝公钥
    public static final String RSA_PUBLIC = "";

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

        //支付宝支付
        aliPayIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        initView();
        initData();
        initListeners();
    }

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    LogUtil.getInstance().info(LogLevel.DEBUG, "resultInfo:" + resultInfo);
                    LogUtil.getInstance().info(LogLevel.DEBUG, "resultStatus:" + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showPaySuccessDialog();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            DialogUtil.getInstance().showCustomToast(PayOrderActivity.this,"支付结果确认中...",Gravity.CENTER);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showPayFailsDialog();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayOrderActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay() {

//        String roomRateStr = orderDetailResponse.getRoom().getRoom_rate();
//        LogUtil.getInstance().info(LogLevel.DEBUG, "roomRate:" + roomRateStr);
//        int dayNum = bookOrder.getDayNum();
//        LogUtil.getInstance().info(LogLevel.DEBUG,"dayNum:"+dayNum);
//        double totalPrice = 0.00;
//        if(!TextUtils.isEmpty(roomRateStr)){
//            double roomRate = Double.parseDouble(roomRateStr);
//            LogUtil.getInstance().info(LogLevel.DEBUG,"roomRate:"+roomRate);
//            totalPrice = MathUtil.convertDouble((dayNum * roomRate));
//        }
//        LogUtil.getInstance().info(LogLevel.DEBUG,"totalPrice:"+totalPrice);
//        String roomType = bookOrder.getRoomType();
//        String remark =  bookOrder.getRemark();
//        String orderNo = bookOrder.getReservationNO();
        // 订单
//        String orderInfo = getOrderInfo(roomType, remark, ""+totalPrice,orderNo);
        String orderInfo = getOrderInfo(orderDetailResponse.getRoom().getRoom_type(), orderDetailResponse.getRoom().getRemark(),
                orderDetailResponse.getRoom().getRoom_rate(),orderDetailResponse.getRoom().getReservation_no());
        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayOrderActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(PayOrderActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    public String getOrderInfo(String subject, String body, String price,String tradeNo) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + tradeNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://api.zkjinshi.com/alipay/notify"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * 支付成功对话框
     */
    private void showPaySuccessDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(PayOrderActivity.this);
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("支付成功！");
        customBuilder.setIconVisibility(View.VISIBLE);
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(PayOrderActivity.this,HistoryOrderActivtiy.class);
                intent.putExtra("is_order", true);
                startActivity(intent);
                finish();
            }
        });
        customBuilder.create().show();
    }

    /**
     * 支付失败对话框
     */
    private void showPayFailsDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(PayOrderActivity.this);
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("支付失败，请重新支付！");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        });
        customBuilder.create().show();
    }

}
