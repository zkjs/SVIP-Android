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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.emchat.EasemobIMManager;
import com.zkjinshi.svip.manager.SSOManager;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BasePavoResponse;
import com.zkjinshi.svip.response.RegisterResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.AESUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PavoUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.PayloadVo;
import com.zkjinshi.svip.wxapi.WXEntryActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开发者：dujiande
 * 日期：2016/3/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RegisterActivity extends BaseActivity {

    private final static String TAG = RegisterActivity.class.getSimpleName();

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


    private Map<String, Object>       mResultMap;
    private SmsReceiver smsReceiver;


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
        setContentView(R.layout.activity_register);
        WXEntryActivity.resp = null;
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
        WXLoginController.getInstance().callback();
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
        WXLoginController.getInstance().init(this);
        LoginController.getInstance().init(this);
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
                Intent intent = new Intent(RegisterActivity.this, WebViewActivity.class);
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
                    registerSi(inputPhone,verifyCode);
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
                    registerSi(inputPhone,code);
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
            String url = ProtocolUtil.ssoVcodeRegister();
            NetRequest netRequest = new NetRequest(url);
            HashMap<String,Object> bizMap = new HashMap<String,Object>();
            String phoneStr = AESUtil.encrypt(phoneNumber, AESUtil.PAVO_KEY);
            bizMap.put("phone",phoneStr);
            netRequest.setObjectParamMap(bizMap);
            NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
            netRequestTask.methodType = MethodType.JSONPOST;
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
                    try{
                        BasePavoResponse basePavoResponse = new Gson().fromJson(result.rawResult,BasePavoResponse.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                handler.sendEmptyMessage(SEND_SMS_VERIFY);
                            }else{
                                PavoUtil.showErrorMsg(RegisterActivity.this,basePavoResponse.getResDesc());
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void beforeNetworkRequestStart() {

                }
            });
            netRequestTask.isShowLoadingDialog = true;
            netRequestTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 注册si用户
     */
    public void registerSi(final String phone,final String code){
        String url = ProtocolUtil.registerSi();
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone", phone);
        bizMap.put("code", code);
        netRequest.setBizParamMap(bizMap);
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
                LogUtil.getInstance().info(LogLevel.INFO, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    RegisterResponse registerResponse = gson.fromJson(result.rawResult,RegisterResponse.class);
                    if(registerResponse.getRes() == 0){//注册成功
                        String token = registerResponse.getToken();
                        PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                        String userid = payloadVo.getSub();
                        CacheUtil.getInstance().setUserId(userid);
                        CacheUtil.getInstance().setLogin(true);
                        CacheUtil.getInstance().setExtToken(token);
                        DBOpenHelper.DB_NAME = userid +".db";
                        //LoginController.getInstance().getUserDetailInfo(userid, CacheUtil.getInstance().getExtToken(), true,isHomeBack, null);
                        CacheUtil.getInstance().setUserPhone(phone);
                        CacheUtil.getInstance().setActivate(false);
                        YunBaSubscribeManager.getInstance().setAlias(RegisterActivity.this,CacheUtil.getInstance().getUserId());
                        EasemobIMManager.getInstance().loginHxUser();
                        MobclickAgent.onProfileSignIn(userid);
                        Intent intent = new Intent(RegisterActivity.this, CompleteInfoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (registerResponse.getRes() == 30001){//用户已经存在
                        Toast.makeText(RegisterActivity.this,"该手机号码已经注册。",Toast.LENGTH_SHORT).show();
                    }else if (registerResponse.getRes() == 30002) {//添加数据出错
                        Toast.makeText(RegisterActivity.this,"注册失败。",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RegisterActivity.this,registerResponse.getResDesc(),Toast.LENGTH_SHORT).show();
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



}