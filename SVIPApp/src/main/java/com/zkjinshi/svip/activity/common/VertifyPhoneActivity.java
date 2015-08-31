package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineActivity;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.http.HttpRequest;
import com.zkjinshi.svip.http.HttpRequestListener;
import com.zkjinshi.svip.http.HttpResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.GetUserResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.SmsUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 开发者：杜健德
 * 日期：2015/8/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VertifyPhoneActivity extends Activity{

    private final static String TAG = VertifyPhoneActivity.class.getSimpleName();

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码

    private int       mSmsVerifyStatus = SMS_UNSEND;//初始状态
    private int       mSmsCountSeconds = 60;//短信倒计时
    private Timer     mTimer;//计数器
    private TimerTask mSmsCountTask;//执行倒计时

    private EditText  mInputPhone;
    private EditText  mVerifyCode;
    private Button    mBtnConfirm;
    private TextView  mUserAgreement;

    private ImageView mImgPhoneRight;
    private ImageView mImgPhoneError;
    private ImageView mImgVerifyRight;
    private ImageView mImgVerifyError;

    //    private String    mStrVerifyCode;                       //自定义的短信验证码
    private Boolean   mSmsVerifySuccess = false;            //短信验证是否正确
    private Response.Listener<String> registerListener;     //注册成功监听事件
    private Response.ErrorListener    registerErrorListener;//注册失败监听事件
    private Response.Listener<String> getUserListener;     //获取用户成功监听事件
    private Response.ErrorListener    getUserErrorListener;//获取用户失败监听事件
    private Map<String, String>       mPhoneVerifyMap;//指定手机对应验证码
    private Map<String, Object>       mResultMap;
    private ItemTitleView             mTitle;
    private Bundle                   thirdBundleData;   //从第三方平台获得的资料数据。


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case  SEND_SMS_VERIFY:
                    mResultMap = (Map<String, Object>) msg.obj;
                    String statusCode = (String) mResultMap.get("statusCode");
                    LogUtil.getInstance().info(LogLevel.INFO,  "短信验证码验证状态" + statusCode);
                    if("000000".equals(statusCode)){
                        //验证发送成功
                        mBtnConfirm.setEnabled(false);
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
                        mTimer = new Timer();
                        mTimer.schedule(mSmsCountTask, 1000, 1000);
                    } else {
                        //验证码发送失败
                        if(mInputPhone.getText().toString().length() >= 11){
                            mBtnConfirm.setEnabled(true);
                        }
                        mSmsCountSeconds = 60;//重新置为60s
                        mBtnConfirm.setText("发送失败，重新发送？");
                    }
                    break;
                case SMS_COUNTING_DOWN:
                    int countSeconds = msg.arg1;
                    mBtnConfirm.setText("倒计时:"+countSeconds+"s");
                    break;
                case SMS_COUNT_OVER:
                    if(mTimer != null){
                        mTimer.cancel();//停止
                    }
                    mSmsCountSeconds = 60;//重新置为60s
                    mBtnConfirm.setText("重新发送？");
                    //验证码发送失败
                    if(mInputPhone.getText().toString().length() >= 11){
                        mBtnConfirm.setEnabled(true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertify_phone);

        thirdBundleData = getIntent().getExtras();

//        Intent intent = new Intent(VertifyPhoneActivity.this, MineActivity.class);
//        intent.putExtra("from_third", true);
//        intent.putExtras(thirdBundleData);
//        startActivity(intent);
//        finish();

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mInputPhone     = (EditText)    findViewById(R.id.et_input_phone);
        mVerifyCode     = (EditText)    findViewById(R.id.et_verify_code);
        mBtnConfirm     = (Button)      findViewById(R.id.btn_confirm);
        mUserAgreement  = (TextView)    findViewById(R.id.tv_user_agreement);
        mImgPhoneRight  = (ImageView)   findViewById(R.id.iv_phone_right);
        mImgPhoneError  = (ImageView)   findViewById(R.id.iv_phone_error);
        mImgVerifyRight = (ImageView)   findViewById(R.id.iv_verify_right);
        mImgVerifyError = (ImageView)   findViewById(R.id.iv_verify_error);
        mBtnConfirm.setText("点击发送验证码");//按钮初始状态
    }

    private void initData() {
        mTitle.setTextTitle(getString(R.string.phone_verify_title));
        mTitle.setTextColor(this, R.color.White);

        //用于倒计时全局倒数计时 1.发送中60s倒计时 2.倒计时结束后
        mSmsCountTask = new TimerTask() {
            @Override
            public void run(){
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
        };

        //手机号输入监听事件
        mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImgPhoneRight.setVisibility(View.GONE);//图标隐藏
                mImgPhoneError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable strPhone) {
                //1.监听手机输入
                String phoneNumber = strPhone.toString();
                if (phoneNumber.length() != 11) {
                    mBtnConfirm.setEnabled(false);
                } else {
                    if (!StringUtil.isPhoneNumber(phoneNumber)) {
                        mImgPhoneError.setVisibility(View.VISIBLE);
                        mBtnConfirm.setEnabled(false);
                    } else {
                        mImgPhoneRight.setVisibility(View.VISIBLE);
                        mImgPhoneError.setVisibility(View.GONE);
                        //手机号输入正确并且当前没有进入倒计时
                        if (mSmsCountSeconds >= 60) {
                            mBtnConfirm.setEnabled(true);
                        } else {
                            mBtnConfirm.setEnabled(false);
                        }
                    }
                }
            }
        });

        //验证码输入框添加输入监听事件
        mVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImgVerifyRight.setVisibility(View.GONE);
                mImgVerifyError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable inputVerifyCode) {
                String verifyCode = inputVerifyCode.toString();
                String phoneNumber = mInputPhone.getText().toString();
                if (inputVerifyCode.length() == 6) {
                    //确认手机号对应的验证码
                    if (StringUtil.isEquals(verifyCode, mPhoneVerifyMap.get(phoneNumber))) {
                        //设置验证码输入正确后的图标
                        mImgVerifyRight.setVisibility(View.VISIBLE);
                        mImgVerifyError.setVisibility(View.GONE);
                        mSmsVerifySuccess = true;//verify success
                        mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                        mBtnConfirm.setEnabled(true);
                        mBtnConfirm.setText("确定");
                        //输入正确切换按钮状态,关闭倒计时
                        if (mTimer != null) {
                            mTimer.cancel();//停止倒计时
                        }
                    } else {
                        mSmsVerifySuccess = false;//verify failed
                        mImgVerifyError.setVisibility(View.VISIBLE);
                        mImgVerifyRight.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(VertifyPhoneActivity.this,LoginActivity.class));
               finish();
            }
        });

        //注册按钮及发送验证码点击事件
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPhone = mInputPhone.getText().toString();
                //getUser();
                if (mSmsVerifySuccess) {
                    getUser();//判断用户是否已经存在
                } else {
                    mVerifyCode.setHint("请输入验证码");//1.请求验证码
                    mVerifyCode.setFocusable(true);
                    mVerifyCode.setFocusableInTouchMode(true);
                    mVerifyCode.requestFocus();
                    mBtnConfirm.setText("正在发送中...");
                    sendVerifyCodeForPhone(inputPhone);//发送验证码
                }
            }
        });



        //用户协议-文本点击事件
        mUserAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:用户协议
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
     * create listener for register
     */
    private void createRegisterListenr() {
        registerListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(JsonUtil.isJsonNull(response))
                    return ;
                //解析json数据
                Map regMap = JsonUtil.toMap(response);
                Boolean isRegSuccess = (Boolean) regMap.get("set");
                if(isRegSuccess){
                    String userid = (String) regMap.get("userid");
                    String token = (String) regMap.get("token");

                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(token);
                    CacheUtil.getInstance().setUserId(userid);
                    CacheUtil.getInstance().setLogin(true);
                    DBOpenHelper.DB_NAME = userid +".db";
                    //跳转到个人资料设置页面
                    Intent intent = new Intent(VertifyPhoneActivity.this, MineActivity.class);
                    intent.putExtra("from_third", true);
                    intent.putExtras(thirdBundleData);
                    startActivity(intent);
                    finish();
                }else {
                    LogUtil.getInstance().info(LogLevel.INFO, "loginin-注册失败！");
                }
            }
        };

        //register error listener
        registerErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.INFO, "网络登录失败。"+volleyError.toString());
            }
        };
    }

    /**
     * 用户请求注册或注册
     * @param phoneNumber   手机号
     */
    public void requestLogin(final String phoneNumber){
        createRegisterListenr();
        DataRequestVolley signUpRequest = new DataRequestVolley(
                HttpMethod.POST, Constants.POST_LOGIN_URL, registerListener, registerErrorListener){
            @Override
            protected Map<String, String> getParams() {
                DeviceUtils.init(VertifyPhoneActivity.this);
                Map<String, String> map = new HashMap<String, String>();
                map.put("phone", phoneNumber);
                map.put("userstatus", "2");
                map.put("phone_os", DeviceUtils.getOS()+" "+DeviceUtils.getSdk() );
                map.put("openid", thirdBundleData.getString("openid"));
                return map;
            }
        };
        Log.v("msg", "request：" + signUpRequest.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(signUpRequest);
    }

    private void goHome() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    /**
     * create listener for getuser
     */
    private void createGetuserListenr() {
        getUserListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.v("msg", "response：" +response);
                if(JsonUtil.isJsonNull(response))
                    return ;
                //解析json数据
                Gson gson = new Gson();
                GetUserResponse getUserResponse = gson.fromJson(response,GetUserResponse.class);
                //如果用户不存在
                if(!getUserResponse.isSet()){
                    LogUtil.getInstance().info(LogLevel.INFO, "VertifyPhoneActivity_用户不存在！");
                    String inputPhone = mInputPhone.getText().toString();
                    requestLogin(inputPhone);//验证码输入正确，请求登录
                }
                else if(getUserResponse.isSet()) {//用户已经存在
                    String userid = getUserResponse.getUserid();
                    String token  = getUserResponse.getToken();
                    LogUtil.getInstance().info(LogLevel.INFO, "VertifyPhoneActivity_用户已经存在！userid:" + userid + "token:" + token);
                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(token);
                    CacheUtil.getInstance().setUserId(userid);
                    CacheUtil.getInstance().setLogin(true);
                    DBOpenHelper.DB_NAME = userid +".db";

                    submitUserInfo(VertifyPhoneActivity.this, "wechart", thirdBundleData.getString("openid"));

                }



            }
        };

        //getuser error listener
        getUserErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户失败。"+volleyError.toString());
            }
        };
    }

    /**
     * 修改个人资料
     * @param context
     * @param fieldKey
     * @param fieldValue
     */
    public void submitUserInfo(final Context context,final String fieldKey,final String fieldValue){
        HttpRequest httpRequest = new HttpRequest();
        HashMap<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("userid",CacheUtil.getInstance().getUserId());
        stringMap.put("token", CacheUtil.getInstance().getToken());
        stringMap.put(fieldKey,fieldValue);
        httpRequest.setRequestUrl(ConfigUtil.getInst().getHttpDomain());
        httpRequest.setRequestMethod(Constants.MODIFY_USER_INFO_METHOD);
        httpRequest.setStringParamMap(stringMap);
        MineNetController.getInstance().init(context);
        MineNetController.getInstance().requestSetInfoTask(httpRequest, new HttpRequestListener<HttpResponse>() {
            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                LogUtil.getInstance().info(LogLevel.ERROR, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "errorCode:" + errorCode);
            }

            @Override
            public void onNetworkResponseSucceed(HttpResponse result) {

                if (null != result && null != result.rawResult) {
                    LogUtil.getInstance().info(LogLevel.INFO, "rawResult:" + result.rawResult);
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult, BaseResponse.class);
                    if (null != baseResponse && baseResponse.isSet()) {
                        LogUtil.getInstance().info(LogLevel.ERROR,"修改用户信息成功。");
                        goHome();
                    } else {
                        LogUtil.getInstance().info(LogLevel.ERROR,"修改用户信息错误。");
                    }
                } else {
                    LogUtil.getInstance().info(LogLevel.ERROR,"修改用户信息错误。");
                }

            }
        });
    }

    /**
     * 获取用户
     *
     */
    public void getUser(){
        Bundle bundle = getIntent().getExtras();
        String url =  Constants.POST_GET_USER_URL+"id="+mInputPhone.getText().toString();
        //Log.v("msg", "url：" + url.toString());
        createGetuserListenr();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.GET, url,getUserListener,getUserErrorListener);
        //Log.v("msg", "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}