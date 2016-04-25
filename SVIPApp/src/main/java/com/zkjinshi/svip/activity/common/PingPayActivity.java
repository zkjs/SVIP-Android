package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pingplusplus.android.PaymentActivity;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.PayDialog;


import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/4/25.
 */
public class PingPayActivity extends BaseActivity {

    private final static String TAG = PingPayActivity.class.getSimpleName();

    /**
     *开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
     *服务端生成charge 的方式可以参考ping++官方文档，地址 https://pingxx.com/guidance/server/import
     *
     *【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url 。
     * 改 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
     */
    private static String TEST_URL ="http://218.244.151.190/demo/charge";
    public static final String URL = TEST_URL;

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

    private Context mContext;
    private int amount = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_pay);

        mContext = this;
        initView();
        initData();
        initListeners();
    }

    private void initView() {
        aliPayIBtn = (ImageButton)findViewById(R.id.pay_order_ali_pay_ibtn);
        weChatPayIBtn = (ImageButton)findViewById(R.id.pay_order_we_chat_pay_ibtn);
    }

    private void initData() {

    }

    private void initListeners() {
        //返回
        findViewById(R.id.pay_order_top_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        aliPayIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPay(CHANNEL_ALIPAY, amount);
            }
        });

        weChatPayIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPay(CHANNEL_WECHAT, amount);
            }
        });
    }

    private void submitPay(String channal,int amount){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channel",channal);
            jsonObject.put("amount",amount);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = URL;
            client.post(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        Intent intent = new Intent();
                        String packageName = getPackageName();
                        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                        intent.setComponent(componentName);
                        intent.putExtra(PaymentActivity.EXTRA_CHARGE,  response);
                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    showPaySuccessDialog();
                }else if(result.equals("fail")){
                    showPayFailsDialog();
                }else if(result.equals("invalid")){
                    if(errorMsg.equals("wx_app_not_installed")){
                        showPayInvalidDialog("您没有安装微信客户端");
                    }else{
                        showPayInvalidDialog("您没有安装对应的支付控件");
                    }
                }

            }
        }
    }

    /**
     * 支付控件没安装对话框
     */
    private void showPayInvalidDialog(String msg){
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(getApplicationContext());
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage(msg);
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
        final PayDialog payDialog = new PayDialog(this,false);
        payDialog.show();
        payDialog.lookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.dismiss();
            }
        });

    }

    /**
     * 支付成功对话框
     */
    private void showPaySuccessDialog(){
        final PayDialog payDialog = new PayDialog(this,true);
        payDialog.show();
        payDialog.lookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.dismiss();
                finish();
            }
        });

    }
}
