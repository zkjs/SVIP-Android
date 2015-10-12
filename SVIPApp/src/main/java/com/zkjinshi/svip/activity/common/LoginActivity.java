package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineActivity;
import com.zkjinshi.svip.response.GetUserResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.SmsUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends Activity{

    private final static String TAG = LoginActivity.class.getSimpleName();

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
    private TextView  mNeedHelp;
    private ImageView mImgPhoneRight;
    private ImageView mImgPhoneError;
    private ImageView mImgVerifyRight;
    private ImageView mImgVerifyError;
    private ImageButton mIbtnWeibo;
    private ImageButton mIbtnWeixin;
    private ImageButton mIbtnQQ;
    //    private String    mStrVerifyCode;                       //自定义的短信验证码
    private Boolean   mSmsVerifySuccess = false;            //短信验证是否正确
    private Response.Listener<String> registerListener;     //注册成功监听事件
    private Response.ErrorListener    registerErrorListener;//注册失败监听事件
    private Response.Listener<String> getUserListener;     //获取用户成功监听事件
    private Response.ErrorListener    getUserErrorListener;//获取用户失败监听事件
    private Map<String, String>       mPhoneVerifyMap;//指定手机对应验证码
    private Map<String, Object>       mResultMap;

    private Bundle thirdBundleData = null;   //从第三方或得的用户数据



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
                        if(mSmsCountTask != null){
                            mSmsCountTask.cancel();
                            mSmsCountTask = null;
                        }
                        mTimer = new Timer();
                        mSmsCountTask = new SmsCountTask();
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
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mInputPhone     = (EditText)    findViewById(R.id.et_input_phone);
        mVerifyCode     = (EditText)    findViewById(R.id.et_verify_code);
        mBtnConfirm     = (Button)      findViewById(R.id.btn_confirm);
        mNeedHelp       = (TextView)    findViewById(R.id.tv_need_help);
        mUserAgreement  = (TextView)    findViewById(R.id.tv_user_agreement);
        mImgPhoneRight  = (ImageView)   findViewById(R.id.iv_phone_right);
        mImgPhoneError  = (ImageView)   findViewById(R.id.iv_phone_error);
        mImgVerifyRight = (ImageView)   findViewById(R.id.iv_verify_right);
        mImgVerifyError = (ImageView)   findViewById(R.id.iv_verify_error);
        mIbtnWeibo       = (ImageButton) findViewById(R.id.enter_weibo_ibtn);
        mIbtnWeixin      = (ImageButton) findViewById(R.id.enter_weixin_ibtn);
        mIbtnQQ           = (ImageButton) findViewById(R.id.enter_qq_ibtn);
        mBtnConfirm.setText("点击发送验证码");//按钮初始状态
    }

    private void initData() {
        LoginController.getInstance().init(this);
    }

    private void initListener() {
        //注册按钮及发送验证码点击事件
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPhone = mInputPhone.getText().toString();
                getUser(inputPhone);
//                if (mSmsVerifySuccess) {
//                    thirdBundleData = null;
//                    getUser(inputPhone);//判断用户是否已经存在
//                } else {
//                    mVerifyCode.setHint("请输入验证码");//1.请求验证码
//                    mVerifyCode.setFocusable(true);
//                    mVerifyCode.setFocusableInTouchMode(true);
//                    mVerifyCode.requestFocus();
//                    mBtnConfirm.setText("正在发送中...");
//                    sendVerifyCodeForPhone(inputPhone);//发送验证码
//                }
            }
        });



        //需要帮助-文本点击事件
        mNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:需要帮助
            }
        });

        //用户协议-文本点击事件
        mUserAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:用户协议
            }
        });

        //微信登录
        mIbtnWeixin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loginWithWeixin();
            }
        });


        //微博登录
        mIbtnWeibo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               // loginWithWeibo();
            }
        });

        //QQ登录
        mIbtnQQ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               // loginWithQQ();
            }
        });

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

    public void logoutWithWeixin(){
        Log.d("TestData", "logoutWithWeixin-------");
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");       ;
        mController.deleteOauth(LoginActivity.this, SHARE_MEDIA.WEIXIN,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        if (status == 200) {
                            Toast.makeText(LoginActivity.this, "删除成功.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "删除失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginWithWeixin(){
       final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this,Constants.WX_APP_ID,Constants.WX_APP_SECRE);
        wxHandler.addToSocialSDK();

        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
               // Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                //Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();

                //获取相关授权信息
                mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        // Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (status == 200 && info != null) {

                            thirdBundleData = new Bundle();
                            //StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for (String key : keys) {
                                //sb.append(key + "=" + info.get(key).toString() + "\r\n");
                                thirdBundleData.putString(key, info.get(key).toString());
                            }
                            LogUtil.getInstance().info(LogLevel.DEBUG, thirdBundleData.toString());
                            getUser(thirdBundleData.getString("openid"));

                        } else {

                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                //Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loginWithWeibo(){
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();
                    mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMDataListener() {
                        @Override
                        public void onStart() {
                            Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete(int status, Map<String, Object> info) {
                            if (status == 200 && info != null) {
                                StringBuilder sb = new StringBuilder();
                                Set<String> keys = info.keySet();
                                for (String key : keys) {
                                    sb.append(key + "=" + info.get(key).toString() + "\r\n");
                                }
                                // Log.d("TestData", sb.toString());
                            } else {
                                // Log.d("TestData", "发生错误：" + status);
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }

            @Override
            public void onStart(SHARE_MEDIA platform) {
            }
        });
    }

    public void loginWithQQ(){
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, Constants.QQ_APP_ID,
               Constants.QQ_APP_KEY);
        qqSsoHandler.addToSocialSDK();

        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
                //获取相关授权信息
                mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (status == 200 && info != null) {
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for (String key : keys) {
                                sb.append(key + "=" + info.get(key).toString() + "\r\n");
                            }
                            //Log.d("TestData", sb.toString());
                        } else {
                            // Log.d("TestData", "发生错误：" + status);
                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
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
                    String token  = (String) regMap.get("token");
                    Boolean isOld = (Boolean) regMap.get("old");

                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(token);
                    CacheUtil.getInstance().setUserId(userid);
                    CacheUtil.getInstance().setLogin(true);
                    DBOpenHelper.DB_NAME = userid +".db";
                    LoginController.getInstance().getUserDetailInfo(userid, token, true,null);

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
                DeviceUtils.init(LoginActivity.this);
                Map<String, String> map = new HashMap<String, String>();
                map.put("phone", phoneNumber);
                map.put("userstatus", "2");
                map.put("phone_os", DeviceUtils.getOS()+" "+DeviceUtils.getSdk() );
                LogUtil.getInstance().info(LogLevel.INFO, map.toString());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.INFO,signUpRequest.toString());
        LoginController.getInstance().addToRequestQueue(signUpRequest);
    }



    /**
     * create listener for getuser
     */
    private void createGetuserListenr() {
        getUserListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.getInstance().info(LogLevel.INFO,response.toString());
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response))
                    return ;
                //解析json数据

                Gson gson = new Gson();
                GetUserResponse getUserResponse = gson.fromJson(response,GetUserResponse.class);
                //如果用户不存在
                if(!getUserResponse.isSet()){
                    LogUtil.getInstance().info(LogLevel.INFO, "LoginActivity_用户不存在！");

                    if(thirdBundleData != null){
                        Intent intent = new Intent(LoginActivity.this, OauthLoginActivity.class);
                        intent.putExtra("from_third", true);
                        intent.putExtras(thirdBundleData);
                        startActivity(intent);
                        finish();
                    }
                    else{
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
                    LoginController.getInstance().getUserDetailInfo(userid, token, false,null);

                }
            }
        };

        //getuser error listener
        getUserErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                volleyError.printStackTrace();
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.ERROR, "获取用户失败。"+volleyError.toString());
            }
        };
    }

    /**
     * 获取用户
     */
    public void getUser(String idValue){
        String url =  Constants.POST_GET_USER_URL+"id="+idValue;
        createGetuserListenr();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.GET, url,getUserListener,getUserErrorListener);
        DialogUtil.getInstance().showProgressDialog(this);
        LoginController.getInstance().addToRequestQueue(request);
    }

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