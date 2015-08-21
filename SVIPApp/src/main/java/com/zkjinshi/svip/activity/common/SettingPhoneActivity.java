package com.zkjinshi.svip.activity.common;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.http.HttpRequest;
import com.zkjinshi.svip.http.HttpRequestListener;
import com.zkjinshi.svip.http.HttpResponse;
import com.zkjinshi.svip.response.BaseResponse;
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
 * 日期：2015/8/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SettingPhoneActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码

    private int       mSmsVerifyStatus = SMS_UNSEND;//初始状态
    private int       mSmsCountSeconds = 60;//短信倒计时
    private Timer mTimer;//计数器
    private TimerTask mSmsCountTask;//执行倒计时

    private ItemTitleView mTitle;//返回
    private LinearLayout mOrgLlt;
    private RelativeLayout mInputRlt;
    private TextView  mOrgPhone; //原来的电话号码
    private EditText  mInputPhone;
    private EditText  mVerifyCode;
    private Button    mBtnConfirm;

    private ImageView mImgPhoneRight;
    private ImageView mImgPhoneError;
    private ImageView mImgVerifyRight;
    private ImageView mImgVerifyError;


    private Boolean   mSmsVerifySuccess = false;            //短信验证是否正确
    private Response.Listener<String> getPhoneListener;     //获取用户成功监听事件
    private Response.ErrorListener getPhoneErrorListener;//获取用户失败监听事件
    private Map<String, String> mPhoneVerifyMap;//指定手机对应验证码
    private Map<String, Object>       mResultMap;


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
        setContentView(R.layout.activity_setting_phone);

        initView();//初始化view
        initData();//初始化data加入
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mOrgLlt         = (LinearLayout)findViewById(R.id.llyt_org);
        mInputRlt       = (RelativeLayout)findViewById(R.id.rl_input_phone);
        mOrgPhone       = (TextView)findViewById(R.id.tv_org_phone);
        mInputPhone     = (EditText)    findViewById(R.id.et_input_phone);
        mVerifyCode     = (EditText)    findViewById(R.id.et_verify_code);
        mBtnConfirm     = (Button)      findViewById(R.id.btn_confirm);
        mImgPhoneRight  = (ImageView)   findViewById(R.id.iv_phone_right);
        mImgPhoneError  = (ImageView)   findViewById(R.id.iv_phone_error);
        mImgVerifyRight = (ImageView)   findViewById(R.id.iv_verify_right);
        mImgVerifyError = (ImageView)   findViewById(R.id.iv_verify_error);

    }

    private void initData() {
        mBtnConfirm.setText("点击发送验证码");//按钮初始状态
        mBtnConfirm.setEnabled(true);

        mTitle.setTextTitle("修改手机号");
        mTitle.getmRight().setVisibility(View.GONE);
        mOrgLlt.setVisibility(View.VISIBLE);
        mInputRlt.setVisibility(View.GONE);
        mOrgPhone.setText(CacheUtil.getInstance().getUserPhone());

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

    private void initListener() {
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
                String phoneNumber;
                boolean isOrg;//是否验证原手机号码
                if (mOrgLlt.getVisibility() == View.VISIBLE) {
                    phoneNumber = CacheUtil.getInstance().getUserPhone();
                    isOrg = true;
                } else {
                    phoneNumber = mInputPhone.getText().toString();
                    isOrg = false;
                }

                if (inputVerifyCode.length() == 6) {
                    //确认手机号对应的验证码
                    if (mPhoneVerifyMap != null && StringUtil.isEquals(verifyCode, mPhoneVerifyMap.get(phoneNumber))) {
                        //设置验证码输入正确后的图标
                        mImgVerifyRight.setVisibility(View.VISIBLE);
                        mImgVerifyError.setVisibility(View.GONE);
                        mSmsVerifySuccess = true;//verify success
                        mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                        //输入正确切换按钮状态,关闭倒计时
                        if (mTimer != null) {
                            mTimer.cancel();//停止倒计时
                        }
                        mBtnConfirm.setEnabled(true);
                        mBtnConfirm.setText("确定");
                    } else {
                        mSmsVerifySuccess = false;//verify failed
                        mImgVerifyError.setVisibility(View.VISIBLE);
                        mImgVerifyRight.setVisibility(View.GONE);
                    }
                }
            }
        });

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //注册按钮及发送验证码点击事件
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrgLlt.getVisibility() == View.VISIBLE) {
                    dealOrgPhone();
                } else {
                    dealNewPhone();
                }
            }
        });
    }

    private void dealOrgPhone(){
        if (mSmsVerifySuccess) {
            mSmsCountSeconds = 60;
            mSmsVerifySuccess = false;
            mBtnConfirm.setText("发送验证码");
            mBtnConfirm.setEnabled(false);

            mOrgLlt.setVisibility(View.GONE);
            mInputRlt.setVisibility(View.VISIBLE);

            mInputPhone.setFocusable(true);
            mInputPhone.setFocusableInTouchMode(true);
            mInputPhone.requestFocus();

            mVerifyCode.setText("");
            mVerifyCode.setFocusable(false);
            mVerifyCode.setFocusableInTouchMode(false);
            mImgVerifyRight.setVisibility(View.GONE);
            mImgVerifyError.setVisibility(View.GONE);

        }else{
            mVerifyCode.setHint("请输入验证码");//1.请求验证码
            mVerifyCode.setFocusable(true);
            mVerifyCode.setFocusableInTouchMode(true);
            mVerifyCode.requestFocus();
            mBtnConfirm.setText("正在发送中...");
            sendVerifyCodeForPhone(CacheUtil.getInstance().getUserPhone());//发送验证码
        }

    }

    private void dealNewPhone(){
        String inputPhone = mInputPhone.getText().toString();
        if (mSmsVerifySuccess) {
            getPhone(inputPhone);//判断手机是否已经存在

        } else {
            mVerifyCode.setHint("请输入验证码");//1.请求验证码
            mVerifyCode.setFocusable(true);
            mVerifyCode.setFocusableInTouchMode(true);
            mVerifyCode.requestFocus();
            mBtnConfirm.setText("正在发送中...");
            sendVerifyCodeForPhone(inputPhone);//发送验证码
        }
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
                LogUtil.getInstance().info(LogLevel.DEBUG,"------verifyCode:"+verifyCode);
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

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){
        HttpRequest httpRequest = new HttpRequest();
        HashMap<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("userid",CacheUtil.getInstance().getUserId());
        stringMap.put("token", CacheUtil.getInstance().getToken());
        stringMap.put(fieldKey, fieldValue);
        httpRequest.setRequestUrl(ConfigUtil.getInst().getHttpDomain());
        httpRequest.setRequestMethod(Constants.MODIFY_USER_INFO_METHOD);
        httpRequest.setStringParamMap(stringMap);
        MineNetController.getInstance().init(this);
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
                        String inputPhone = mInputPhone.getText().toString();
                        CacheUtil.getInstance().setUserPhone(inputPhone);
                        DialogUtil.getInstance().showToast(SettingPhoneActivity.this,"修改手机号码成功。");
                        finish();
                    }
                }

            }
        });
    }

    /**
     * create listener for getphone
     */
    private void createGetPhoneListenr() {
        getPhoneListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.v("msg", "response：" + response);
                if(JsonUtil.isJsonNull(response))
                    return ;
                //解析json数据
                Map regMap = JsonUtil.toMap(response);
                //如果用户不存在
                if(regMap.containsKey("set") &&  regMap.get("set").equals("false")){
                    LogUtil.getInstance().info(LogLevel.INFO, "用户不存在！");
                    String inputPhone = mInputPhone.getText().toString();
                    submitInfo("phone",inputPhone);

                }
                else{//用户已经存在
                    LogUtil.getInstance().info(LogLevel.INFO, "用户已经存在！");
                    DialogUtil.getInstance().showToast(SettingPhoneActivity.this, "该手机号码已经注册过，不能修改。");
                }
            }
        };

        //getuser error listener
        getPhoneErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户失败。"+volleyError.toString());
            }
        };
    }

    /**
     * 获取用户
     *
     */
    public void getPhone(String phone){

        String url =  Constants.POST_GET_PHONE_URL+"phone="+phone;
        Log.v("msg", "url：" + url.toString());
        createGetPhoneListenr();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.GET, url, getPhoneListener, getPhoneErrorListener);
        //Log.v("msg", "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
