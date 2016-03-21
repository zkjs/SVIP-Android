package com.zkjinshi.svip.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.manager.SSOManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;

import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.AESUtil;
import com.zkjinshi.svip.utils.CacheUtil;
;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PavoUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.PayloadVo;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends BaseActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码
    private final static int SEND_SMS_RECEIVE    = 13; //获取短信验证码

    public final static int WX_REQUEST_LOGIN    = 14; //获取微信个人信息后请求登录

    private int       mSmsVerifyStatus = SMS_UNSEND;//初始状态
    private int       mSmsCountSeconds = 60;//短信倒计时
    private Timer     mTimer;//计数器
    private TimerTask mSmsCountTask;//执行倒计时

    private EditText  mInputPhone;
    private EditText  mVerifyCode;
    private TextView  sendVerifyCode;
    private Button commitBtn;
    private ImageView clearPhoneIv,clearCodeIv;
    private Drawable leftPhoneDrawable,leftCodeDrawable;
    private TextView useDealTv,useDealTipTv;
    private ImageButton backIBtn;

    private Boolean   mSmsVerifySuccess = false;            //短信验证是否正确
    public Bundle thirdBundleData = null;

    private Map<String, Object>       mResultMap;
    private SmsReceiver smsReceiver;

    private Context mContext;


    private boolean isHomeBack;

     public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case WX_REQUEST_LOGIN:
                   // getUser(thirdBundleData.getString("openid"));
                    break;
                case  SEND_SMS_VERIFY:
                    //验证发送成功
                    sendVerifyCode.setEnabled(false);
                    mSmsVerifyStatus = SMS_SENDED;//验证码请求成功开启倒计时
                    if(mTimer != null){
                        mTimer.cancel();
                        mTimer = null;
                    }
                    if(mSmsCountTask != null){
                        mSmsCountTask.cancel();
                        mSmsCountTask = null;
                    }
                    mTimer = new Timer();
                    mSmsCountTask = new SmsCountTask();
                    mTimer.schedule(mSmsCountTask, 1000, 1000);
                    break;
                case SMS_COUNTING_DOWN:
                    int countSeconds = msg.arg1;
                    sendVerifyCode.setText("剩余:"+countSeconds+"s");
                    break;
                case SMS_COUNT_OVER:
                    if(mTimer != null){
                        mTimer.cancel();//停止
                    }
                    mSmsCountSeconds = 60;
                    sendVerifyCode.setText("验证码");
                    //验证码发送失败
                    if(mInputPhone.getText().toString().length() >= 11){
                        sendVerifyCode.setEnabled(true);
                    }
                    break;
                case SEND_SMS_RECEIVE:
                    if(null != mVerifyCode){
                        String vertifyCode = msg.obj.toString();
                        mVerifyCode.setText(vertifyCode);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(null != smsReceiver){
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.getInstance().info(LogLevel.INFO,"protected void onResume()");

    }

    private void initView() {
        mInputPhone     = (EditText)    findViewById(R.id.et_input_phone);
        mVerifyCode     = (EditText)    findViewById(R.id.et_verify_code);
        sendVerifyCode = (TextView)findViewById(R.id.tv_send_verify_code);
        commitBtn = (Button)findViewById(R.id.btn_confirm);
        clearPhoneIv = (ImageView)findViewById(R.id.login_iv_clear_phone);
        clearCodeIv = (ImageView)findViewById(R.id.login_iv_verify_code);
        useDealTv = (TextView)findViewById(R.id.login_tv_use_deal);
        useDealTipTv = (TextView)findViewById(R.id.login_tv_use_deal_tips);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
    }

    private void initData() {
        isHomeBack = getIntent().getBooleanExtra("isHomeBack",false);
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(Integer.MAX_VALUE);
            smsReceiver = new SmsReceiver();
            registerReceiver(smsReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {

        //注册
        findViewById(R.id.login_tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //使用协议
        useDealTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("webview_url","http://zkjinshi.com/about_us/use_agree.html");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom,
                        R.anim.slide_out_top);
            }
        });

        //清空手机号码
        clearPhoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputPhone.setText("");
            }
        });

        //清空验证码
        clearCodeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerifyCode.setText("");
            }
        });

        //发送验证码
        sendVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPhone = mInputPhone.getText().toString();
                if (TextUtils.isEmpty(inputPhone)) {
                    DialogUtil.getInstance().showCustomToast(view.getContext(), "输入的手机号码不能为空", Gravity.CENTER);
                    return;
                }
                if(!IntentUtil.isMobileNO(inputPhone)){
                    DialogUtil.getInstance().showCustomToast(view.getContext(), "请输入正确格式的手机号码", Gravity.CENTER);
                    return;
                }

                mVerifyCode.setFocusable(true);
                mVerifyCode.setFocusableInTouchMode(true);
                mVerifyCode.requestFocus();
                sendVerifyCode.setText("发送中");
                sendVerifyCodeForPhone(inputPhone);//发送验证码
            }
        });

        //提交
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPhone = mInputPhone.getText().toString();
                String verifyCode = mVerifyCode.getText().toString();
                if (TextUtils.isEmpty(inputPhone)) {
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "输入的手机号码不能为空", Gravity.CENTER);
                    return;
                }
                if(!IntentUtil.isMobileNO(inputPhone)){
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "请输入正确格式的手机号码", Gravity.CENTER);
                    return;
                }
                if (verifyCode.length() == 6) {
                    mSmsVerifySuccess = true;//verify success
                    mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                    getToken(inputPhone,verifyCode);
                }else{
                    DialogUtil.getInstance().showCustomToast(v.getContext(),"验证码输入有误", Gravity.CENTER);
                }
            }
        });

        //手机号输入监听事件
        mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable strPhone) {
                if (null != strPhone && strPhone.length() > 0) {
                    clearPhoneIv.setVisibility(View.VISIBLE);
                } else {
                    clearPhoneIv.setVisibility(View.GONE);
                }
                String phoneNumber = strPhone.toString();
                if (phoneNumber.length() != 11) {
                    sendVerifyCode.setBackgroundResource(R.drawable.bg_login_etv_default_shape);
                    sendVerifyCode.setTextColor(getResources().getColor(R.color.light_black));
                    sendVerifyCode.setEnabled(false);
                    leftPhoneDrawable = getResources().getDrawable(
                            R.mipmap.ic_yonghu_nor);
                    leftPhoneDrawable.setBounds(0, 0, leftPhoneDrawable.getMinimumWidth(),
                            leftPhoneDrawable.getMinimumHeight());
                    mInputPhone.setCompoundDrawables(leftPhoneDrawable, null, null, null);
                } else {
                    sendVerifyCode.setBackgroundResource(R.drawable.bg_login_etv_active_shape);
                    sendVerifyCode.setTextColor(getResources().getColor(android.R.color.white));
                    leftPhoneDrawable = getResources().getDrawable(
                            R.mipmap.ic_yonghu_pre);
                    leftPhoneDrawable.setBounds(0, 0, leftPhoneDrawable.getMinimumWidth(),
                            leftPhoneDrawable.getMinimumHeight());
                    mInputPhone.setCompoundDrawables(leftPhoneDrawable, null, null, null);
                    if (!StringUtil.isPhoneNumber(phoneNumber)) {
                        sendVerifyCode.setEnabled(false);
                    } else {
                        //手机号输入正确并且当前没有进入倒计时
                        if (mSmsCountSeconds >= 60) {
                            sendVerifyCode.setEnabled(true);
                        } else {
                            sendVerifyCode.setEnabled(false);
                        }
                    }
                }
            }
        });

        mVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && s.length() > 0) {
                    clearCodeIv.setVisibility(View.VISIBLE);
                } else {
                    clearCodeIv.setVisibility(View.GONE);
                }
                String code = s.toString();
                if(!TextUtils.isEmpty(code) && code.length() == 6){
                    leftCodeDrawable = getResources().getDrawable(
                            R.mipmap.ic_mima_pre);
                    leftCodeDrawable.setBounds(0, 0, leftCodeDrawable.getMinimumWidth(),
                            leftCodeDrawable.getMinimumHeight());
                    mVerifyCode.setCompoundDrawables(leftCodeDrawable, null, null, null);
                    useDealTipTv.setVisibility(View.VISIBLE);
                    commitBtn.setVisibility(View.VISIBLE);
                }else {
                    leftCodeDrawable = getResources().getDrawable(
                            R.mipmap.ic_mima_nor);
                    leftCodeDrawable.setBounds(0, 0, leftCodeDrawable.getMinimumWidth(),
                            leftCodeDrawable.getMinimumHeight());
                    mVerifyCode.setCompoundDrawables(leftCodeDrawable, null, null, null);
                    useDealTipTv.setVisibility(View.GONE);
                    commitBtn.setVisibility(View.GONE);
                }
            }
        });

        mVerifyCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String code = mVerifyCode.getText().toString();
                String inputPhone = mInputPhone.getText().toString();
                /*判断是否是“go”键*/
                if(actionId == EditorInfo.IME_ACTION_GO && !TextUtils.isEmpty(code) && code.length() == 6){
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken(), 0);
                    }
                    getToken(inputPhone,code);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 向手机发送验证码
     * @param phoneNumber
     */
    private void sendVerifyCodeForPhone(final String phoneNumber) {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            String phoneStr = AESUtil.encrypt(phoneNumber, AESUtil.PAVO_KEY);
            jsonObject.put("phone",phoneStr);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoVcodeLogin();
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
                        BaseResponseVo basePavoResponse = new Gson().fromJson(response,BaseResponseVo.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                handler.sendEmptyMessage(SEND_SMS_VERIFY);
                            }else{
                                PavoUtil.showErrorMsg(LoginActivity.this,basePavoResponse.getResDesc());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    RequestUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }



    private String strContent;
    private String patternCoder = "(?<!--\\d)\\d{6}(?!\\d)";

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    class  SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                byte[] pdu = (byte[]) obj;
                SmsMessage sms = SmsMessage.createFromPdu(pdu);
                String message = sms.getMessageBody();
                String from = sms.getOriginatingAddress();
                if (!TextUtils.isEmpty(from)) {
                    strContent = from + "   " + message;
                    if (!TextUtils.isEmpty(from)) {
                        String code = patternCode(message);
                        if (!TextUtils.isEmpty(code)) {
                            strContent = code;
                            Message receiveMessage = new Message();
                            receiveMessage.obj = strContent;
                            receiveMessage.what = SEND_SMS_RECEIVE;
                            handler.sendMessage(receiveMessage);
                        }
                    }
                }
            }
        }
    };

    /**
     * 短信倒数计时器
     */
    class SmsCountTask extends TimerTask{
        @Override
        public void run() {
            //当前验证码已发送，倒数计时中
            if(mSmsCountSeconds > 0 && mSmsVerifyStatus == SMS_SENDED){
                mSmsCountSeconds--;
                Message msg = Message.obtain();
                msg.what    = SMS_COUNTING_DOWN;
                msg.arg1    = mSmsCountSeconds;//倒数时间
                handler.sendMessage(msg);
            } else {
                Message msg = Message.obtain();
                msg.what    = SMS_COUNT_OVER;
                handler.sendMessage(msg);
                mSmsCountSeconds = 0;//短信倒数置为0
            }
        }
    }

    /**
     * 使用手机验证码创建Token
     * @param phone
     * @param code
     */
    private void getToken(final String phone,final String code){

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone",phone);
            jsonObject.put("code",code);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoToken();
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
                        BaseResponseVo basePavoResponse = new Gson().fromJson(response,BaseResponseVo.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                if(!StringUtil.isEmpty(basePavoResponse.getToken())){//成功
                                    PayloadVo payloadVo = SSOManager.getInstance().decodeToken(basePavoResponse.getToken());
                                    String userid = payloadVo.getSub();
                                    CacheUtil.getInstance().setExtToken(basePavoResponse.getToken());
                                    CacheUtil.getInstance().setUserId(userid);
                                    CacheUtil.getInstance().setLogin(true);
                                    DBOpenHelper.DB_NAME = userid +".db";

                                    LoginController.getInstance().getUserInfo(LoginActivity.this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
                                        @Override
                                        public void successCallback(JSONObject response) {
                                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                            LoginActivity.this.startActivity(mainIntent);
                                            LoginActivity.this.finish();
                                            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                                        }
                                    });
                                }
                            }else if(basePavoResponse.getRes() == 11){//资料不全
                                PayloadVo payloadVo = SSOManager.getInstance().decodeToken(basePavoResponse.getToken());
                                String userid = payloadVo.getSub();
                                CacheUtil.getInstance().setExtToken(basePavoResponse.getToken());
                                CacheUtil.getInstance().setUserId(userid);
                                Intent intent = new Intent(LoginActivity.this, CompleteInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                PavoUtil.showErrorMsg(LoginActivity.this,basePavoResponse.getResDesc());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    RequestUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}