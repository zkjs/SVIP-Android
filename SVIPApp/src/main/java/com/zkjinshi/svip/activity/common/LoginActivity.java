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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.GetUserResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.SmsUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.wxapi.WXEntryActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Map<String, String>       mPhoneVerifyMap = new HashMap<String, String>();//指定手机对应验证码
    private Map<String, Object>       mResultMap;
    private SmsReceiver smsReceiver;

    public Bundle thirdBundleData = null;   //从第三方或得的用户数据
    private boolean isHomeBack;

     public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case WX_REQUEST_LOGIN:
                    getUser(thirdBundleData.getString("openid"));
                    break;
                case  SEND_SMS_VERIFY:
                    mResultMap = (Map<String, Object>) msg.obj;
                    String statusCode = (String) mResultMap.get("statusCode");
                    LogUtil.getInstance().info(LogLevel.INFO,  "短信验证码验证状态" + statusCode);
                    if("000000".equals(statusCode)){
                        //验证发送成功
                        sendVerifyCode.setEnabled(false);
                        if(mPhoneVerifyMap == null){
                            mPhoneVerifyMap = new HashMap<>();
                        } else {
                            mPhoneVerifyMap.clear();//清空之前数据
                        }

                        Bundle bundle = msg.getData();//获得手机和对应验证码
                        if(null != bundle){
                            String phoneNumber = (String) bundle.get("phone_number");
                            String verifyCode  = (String) bundle.get("verify_code");
                            mPhoneVerifyMap.put(phoneNumber, verifyCode);
                        }

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
                    }
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WXEntryActivity.resp = null;
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != smsReceiver){
            unregisterReceiver(smsReceiver);
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
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver, filter);
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
                Intent intent = new Intent(LoginActivity.this,UseDealActivity.class);
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
                if (TextUtils.isEmpty(inputPhone)) {
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "输入的手机号码不能为空", Gravity.CENTER);
                    return;
                }
                if(!IntentUtil.isMobileNO(inputPhone)){
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "请输入正确格式的手机号码", Gravity.CENTER);
                    return;
                }
                //是否开启短信验证
                if(Constants.SMS_CHECK_ENABLE){
                    String verifyCode = mVerifyCode.getText().toString();
                    String phoneNumber = mInputPhone.getText().toString();
                    if (verifyCode.length() == 6) {
                        if (mPhoneVerifyMap.containsKey(phoneNumber) && StringUtil.isEquals(verifyCode, mPhoneVerifyMap.get(phoneNumber))) {
                            mSmsVerifySuccess = true;//verify success
                            mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                        } else {
                            mSmsVerifySuccess = false;//verify failed
                        }
                    }
                    if (mSmsVerifySuccess) {
                        thirdBundleData = null;
                        getUser(inputPhone);//判断用户是否已经存在
                    }else {
                        DialogUtil.getInstance().showCustomToast(v.getContext(),"验证码输入有误", Gravity.CENTER);
                    }
                }else{
                    getUser(inputPhone);
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
    }

    /**
     * 向手机发送验证码
     * @param phoneNumber
     */
    private void sendVerifyCodeForPhone(final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //生成成员变量随机验证码
                String verifyCode = SmsUtil.getInstance().generateVerifyCode();
                Map<String, Object> result =  SmsUtil.getInstance().sendTemplateSMS(
                        phoneNumber, verifyCode);
                Message msg = Message.obtain();
                msg.what    = SEND_SMS_VERIFY;
                msg.obj     = result;
                Bundle bundle = new Bundle();
                bundle.putString("phone_number", phoneNumber);
                bundle.putString("verify_code", verifyCode);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 用户请求登录或注册
     * @param phoneNumber   手机号
     */
    public void requestLogin(final String phoneNumber){
        String url =  Constants.POST_LOGIN_URL;
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone",phoneNumber);
        bizMap.put("userstatus","2");
        DeviceUtils.init(LoginActivity.this);
        bizMap.put("phone_os", DeviceUtils.getOS()+" "+DeviceUtils.getSdk());
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
                    if(JsonUtil.isJsonNull(result.rawResult))
                        return ;
                    //解析json数据
                    Map regMap = JsonUtil.toMap(result.rawResult);
                    Boolean isRegSuccess = (Boolean) regMap.get("set");
                    if(isRegSuccess){
                        String userid = (String) regMap.get("userid");
                        String token  = (String) regMap.get("token");
                        Boolean isOld = (Boolean) regMap.get("old");

                        //更新为最新的token和userid
                        CacheUtil.getInstance().setToken(token);
                        CacheUtil.getInstance().setUserId(userid);
                        CacheUtil.getInstance().setLogin(true);
                        DBOpenHelper.DB_NAME = userid +".db";
                        LoginController.getInstance().getUserDetailInfo(userid, token, true,isHomeBack, null);

                    }else {
                        LogUtil.getInstance().info(LogLevel.INFO, "loginin-注册失败！");
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
     * 获取用户
     */
    public void getUser(String idValue){
        String url = Constants.POST_GET_USER_URL+"id="+idValue;
        LogUtil.getInstance().info(LogLevel.INFO, "url:" +url);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    GetUserResponse getUserResponse = gson.fromJson(result.rawResult,GetUserResponse.class);
                    //如果用户不存在
                    if(!getUserResponse.isSet()){
                        LogUtil.getInstance().info(LogLevel.INFO, "LoginActivity_用户不存在！");

                        if (thirdBundleData != null) {
                            Intent intent = new Intent(LoginActivity.this, OauthLoginActivity.class);
                            intent.putExtra("from_third", true);
                            intent.putExtras(thirdBundleData);
                            startActivity(intent);
                            finish();
                        } else {
                            //TODO: 加入完善资料操作
                            String inputPhone = mInputPhone.getText().toString();
                            requestLogin(inputPhone);//验证码输入正确，请求登录
                        }

                    }
                    else if (getUserResponse.isSet()){//用户已经存在
                        LogUtil.getInstance().info(LogLevel.INFO, "LoginActivity_用户已经存在！");
                        String userid = getUserResponse.getUserid();
                        String token  = getUserResponse.getToken();

                        //更新为最新的token和userid
                        CacheUtil.getInstance().setToken(token);
                        CacheUtil.getInstance().setUserId(userid);
                        CacheUtil.getInstance().setLogin(true);
                        DBOpenHelper.DB_NAME = userid +".db";
                        LoginController.getInstance().getUserDetailInfo(userid, token, false,isHomeBack,null);

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
        LogUtil.getInstance().info(LogLevel.INFO,  " netRequestTask.execute()");
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