package com.zkjinshi.svip.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
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
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PavoUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.PayloadVo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CheckActivity extends BaseActivity {

    private final static String TAG = CheckActivity.class.getSimpleName();

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码
    private final static int SEND_SMS_RECEIVE    = 13; //获取短信验证码


    private int       mSmsVerifyStatus = SMS_UNSEND;//初始状态
    private int       mSmsCountSeconds = 60;//短信倒计时
    private Timer     mTimer;//计数器
    private TimerTask mSmsCountTask;//执行倒计时


    private EditText  mVerifyCodeEt;
    private TextView phoneTv;
    private TextView countdownTv;
    private Button commitBtn;
    private ImageButton backIBtn;

    private SmsReceiver smsReceiver;
    private Context mContext;
    private boolean isLogin;

     public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case  SEND_SMS_VERIFY:
                    //验证发送成功
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
                    countdownTv.setText(countSeconds+"s");
                    break;
                case SMS_COUNT_OVER:
                    if(mTimer != null){
                        mTimer.cancel();//停止
                    }
                    mSmsCountSeconds = 60;
                    break;
                case SEND_SMS_RECEIVE:
                    if(null != mVerifyCodeEt){
                        String vertifyCode = msg.obj.toString();
                        mVerifyCodeEt.setText(vertifyCode);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mContext = this;
        isLogin = getIntent().getBooleanExtra("isLogin",false);
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

    }

    private void initView() {
        mVerifyCodeEt     = (EditText)    findViewById(R.id.inputEt);
        commitBtn = (Button)findViewById(R.id.btn_login);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        phoneTv = (TextView)findViewById(R.id.phone_tv);
        countdownTv = (TextView)findViewById(R.id.countdown_tv);
    }

    private void initData() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(Integer.MAX_VALUE);
            smsReceiver = new SmsReceiver();
            registerReceiver(smsReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        phoneTv.setText(CacheUtil.getInstance().getUserPhone());
        handler.sendEmptyMessage(SEND_SMS_VERIFY);

    }

    private void initListener() {

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });


        //提交
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPhone = phoneTv.getText().toString();
                String verifyCode = mVerifyCodeEt.getText().toString();
                if (TextUtils.isEmpty(inputPhone)) {
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "输入的手机号码不能为空", Gravity.CENTER);
                    return;
                }
                if(!IntentUtil.isMobileNO(inputPhone)){
                    DialogUtil.getInstance().showCustomToast(v.getContext(), "请输入正确格式的手机号码", Gravity.CENTER);
                    return;
                }
                if (verifyCode.length() == 6) {
                    mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                    if(isLogin){
                        getToken(inputPhone,verifyCode);
                    }else{
                        registerSi(inputPhone,verifyCode);
                    }
                }else{
                    DialogUtil.getInstance().showCustomToast(v.getContext(),"验证码输入有误", Gravity.CENTER);
                }
            }
        });





        mVerifyCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String code = mVerifyCodeEt.getText().toString();
                String inputPhone = phoneTv.getText().toString();
                /*判断是否是“go”键*/
                if(actionId == EditorInfo.IME_ACTION_GO && !TextUtils.isEmpty(code) && code.length() == 6){
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken(), 0);
                    }
                    if(isLogin){
                        getToken(inputPhone,code);
                    }else{
                        registerSi(inputPhone,code);
                    }

                    return true;
                }
                return false;
            }
        });
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

                                    LoginController.getInstance().getUserInfo(CheckActivity.this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
                                        @Override
                                        public void successCallback(JSONObject response) {
                                            Intent mainIntent = new Intent(CheckActivity.this, MainActivity.class);
                                            CheckActivity.this.startActivity(mainIntent);
                                            CheckActivity.this.finish();
                                            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                                        }
                                    });
                                }
                            }else if(basePavoResponse.getRes() == 11){//资料不全
                                PayloadVo payloadVo = SSOManager.getInstance().decodeToken(basePavoResponse.getToken());
                                String userid = payloadVo.getSub();
                                CacheUtil.getInstance().setExtToken(basePavoResponse.getToken());
                                CacheUtil.getInstance().setUserId(userid);
                                Intent intent = new Intent(CheckActivity.this, UploadInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                PavoUtil.showErrorMsg(CheckActivity.this,basePavoResponse.getResDesc());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(CheckActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
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
                    BaseResponseVo registerResponse = gson.fromJson(result.rawResult,BaseResponseVo.class);
                    if(registerResponse.getRes() == 0){//注册成功
                        String token = registerResponse.getToken();
                        PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                        String userid = payloadVo.getSub();
                        CacheUtil.getInstance().setUserId(userid);
                        DBOpenHelper.DB_NAME = userid +".db";
                        CacheUtil.getInstance().setExtToken(token);
                        CacheUtil.getInstance().setUserPhone(phone);
                        CacheUtil.getInstance().setActivate(false);
                        Intent intent = new Intent(mContext, UploadInfoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (registerResponse.getRes() == 30001){//用户已经存在
                        Toast.makeText(mContext,"该手机号码已经注册。",Toast.LENGTH_SHORT).show();
                    }else if (registerResponse.getRes() == 30002) {//添加数据出错
                        Toast.makeText(mContext,"注册失败。",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext,registerResponse.getResDesc(),Toast.LENGTH_SHORT).show();
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

}