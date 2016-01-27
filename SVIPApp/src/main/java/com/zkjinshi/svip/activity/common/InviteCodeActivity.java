package com.zkjinshi.svip.activity.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 输入邀请码页面
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeActivity extends BaseActivity {

    private final static String TAG = InviteCodeActivity.class.getSimpleName();

    private Context       mContext;
    private String        mUserID;
    private String        mPhoneNum;
    private String        mToken;
    private String        mUserName;
    private String        mSalerPhone;

    private EditText      mEtInviteCode;
    private CircleImageView mCivSalerAvatar;
    private TextView      mTvSalerName;
    private Button commitBtn;
    private RelativeLayout headLayout;
    private Drawable leftInviteDrawable;
    private ImageView clearInviteIv;

    private DisplayImageOptions mOptions;
    private String              mSalesID;
    private String              mSalesName;
    private String              mShopID;
    private String              mInviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mEtInviteCode = (EditText)      findViewById(R.id.et_invite_code);
        mCivSalerAvatar = (CircleImageView)     findViewById(R.id.civ_saler_avatar);
        mTvSalerName    = (TextView)      findViewById(R.id.tv_saler_name);
        commitBtn = (Button)findViewById(R.id.btn_confirm);
        headLayout = (RelativeLayout)findViewById(R.id.invite_head_layout);
        clearInviteIv = (ImageView)findViewById(R.id.login_iv_clear_invite);
    }

    private void initData() {
        mContext  = InviteCodeActivity.this;
        mUserID   = CacheUtil.getInstance().getUserId();
        mPhoneNum = CacheUtil.getInstance().getUserPhone();
        mToken    = CacheUtil.getInstance().getToken();
        mUserName = CacheUtil.getInstance().getUserName();
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_press)
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_press)
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_press)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private void initListener() {

        //清空邀请码
        clearInviteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInviteCode.setText("");
            }
        });

        //邀请码输入
        mEtInviteCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable inviteCode) {
                String inviteCodeStr = inviteCode.toString();
                if(TextUtils.isEmpty(inviteCodeStr)){
                    clearInviteIv.setVisibility(View.GONE);
                }else {
                    clearInviteIv.setVisibility(View.VISIBLE);
                }
                if (inviteCodeStr.length() >= 6) {
                    findSalerByInviteCode(inviteCodeStr.toUpperCase());
                    leftInviteDrawable = getResources().getDrawable(
                            R.mipmap.ic_duanxin_pre);
                    leftInviteDrawable.setBounds(0, 0, leftInviteDrawable.getMinimumWidth(),
                            leftInviteDrawable.getMinimumHeight());
                    mEtInviteCode.setCompoundDrawables(leftInviteDrawable, null, null, null);
                }else{
                    leftInviteDrawable = getResources().getDrawable(
                            R.mipmap.ic_duanxin_nor);
                    leftInviteDrawable.setBounds(0, 0, leftInviteDrawable.getMinimumWidth(),
                            leftInviteDrawable.getMinimumHeight());
                    mEtInviteCode.setCompoundDrawables(leftInviteDrawable, null, null, null);
                }
                if (TextUtils.isEmpty(inviteCodeStr)) {
                    showSalerInfo(false, null, null);
                }

            }
        });

        //提交
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInviteCode = mEtInviteCode.getText().toString().trim();
                if (!TextUtils.isEmpty(mInviteCode) && (headLayout.getVisibility() == View.VISIBLE)) {
                    if(TextUtils.isEmpty(mInviteCode) || TextUtils.isEmpty(mInviteCode)){
                        return ;
                    } else {
                        //邀请码验证成功 开始确认并绑定邀请码
                        bindTheSalerWithCode(mInviteCode.toUpperCase(), mSalesID, mSalesName, mShopID, mSalerPhone);
                    }
                } else {
                    Intent goHome = new Intent(InviteCodeActivity.this, MainActivity.class);
                    startActivity(goHome);
                    InviteCodeActivity.this.finish();
                }
            }
        });

        if(!TextUtils.isEmpty(mInviteCode)){
            mEtInviteCode.setText(mInviteCode);
            findSalerByInviteCode(mInviteCode);
        }
    }

    /**
     * 根据邀请码绑定客服
     */
    private void bindTheSalerWithCode(String inviteCode, String salerID, String salerName, String shopID, String salerPhone) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getUserBindInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();
        bizMap.put("userid", mUserID);
        bizMap.put("token", mToken);
        bizMap.put("username", mUserName);
        bizMap.put("phone", mPhoneNum);
        bizMap.put("code", inviteCode);
        bizMap.put("user_salesid", salerID);
        bizMap.put("sales_name", salerName);
        bizMap.put("shopid", shopID);
        if(TextUtils.isEmpty(salerPhone)){
            bizMap.put("sales_phone", salerPhone);
        }

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.methodType     = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,  "网络异常", Gravity.CENTER);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,  "请求取消", Gravity.CENTER);
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    JSONObject responseObj = new JSONObject(jsonResult);
                    Boolean    isSuccess   = responseObj.getBoolean("set");
                    if(isSuccess) {
                        CacheUtil.getInstance().setActivate(true);
                        //本地缓存绑定邀请码
                        if(!TextUtils.isEmpty(mSalesID)) {
                            EMConversationHelper.getInstance().sendInviteCmdMessage(
                                    mUserID,
                                    mUserName,
                                    mPhoneNum,
                                    System.currentTimeMillis(),
                                    mSalesID,
                                    new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            LogUtil.getInstance().info(LogLevel.INFO, TAG + "信息发送成功");
                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            LogUtil.getInstance().info(LogLevel.INFO, TAG + "信息发送失败");
                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    }
                            );
                        }
                        Intent mainIntent = new Intent(InviteCodeActivity.this, MainActivity.class);
                        InviteCodeActivity.this.startActivity(mainIntent);
                        InviteCodeActivity.this.finish();
                        InviteCodeActivity.this.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

                    } else {
                        int errCode = responseObj.getInt("err");
                        if(400 == errCode){
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                    "token不对，请勿重复使用。", Gravity.CENTER);
                        }
                        if(301 == errCode){
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                    "提交信息不足。", Gravity.CENTER);
                        }
                        if(300 == errCode){
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                    "邀请码已经使用，请勿重复使用。", Gravity.CENTER);
                        } else {
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                    "绑定失败。", Gravity.CENTER);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * @param inviteCodeStr
     */
    private void findSalerByInviteCode(final String inviteCodeStr) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getEmpByInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();

        bizMap.put("userid", mUserID);
        bizMap.put("token", mToken);
        bizMap.put("code", inviteCodeStr);

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.methodType     = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                              getString(R.string.net_exception), Gravity.CENTER);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                              getString(R.string.request_cancel), Gravity.CENTER);
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    JSONObject responseObj = new JSONObject(jsonResult);
                    Boolean isSuccess = responseObj.getBoolean("set");
                    if (isSuccess) {
                        mSalesID = responseObj.getString("salesid");
                        mShopID  = String.valueOf(responseObj.getInt("shopid"));
                        if (!TextUtils.isEmpty(mSalesID)) {
                            String avatarUrl = ProtocolUtil.getAvatarUrl(mSalesID);
                            mSalesName = responseObj.getString("sales_name");
                            mSalerPhone = responseObj.getString("sales_phone");

                            showSalerInfo(true, avatarUrl, mSalesName);
                            commitBtn.setText("确认");
                        }
                    } else {
                        commitBtn.setText("跳过");
                        if(responseObj.getInt("err") == 400) {
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                                  "token失效, 请重新登录！", Gravity.CENTER);
                        }
                        if(responseObj.getInt("err") == 404){
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                         "邀请码不存在, 请确认后重新输入！", Gravity.CENTER);
                        } else {
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                                   "查询失败，请稍后再试！", Gravity.CENTER);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * show the saler info when input the invite code correctly
     * @param show
     */
    private void showSalerInfo(final boolean show, String avatarUrl, String salerName) {

        AnimatorSet animation = null;
        if ((headLayout.getVisibility() == View.VISIBLE) && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(headLayout, "translationY", 0,
                                                      (5/4) * mEtInviteCode.getHeight());
            ObjectAnimator fade = ObjectAnimator.ofFloat(headLayout, "alpha", 1, 0);
            animation.playTogether(move, fade);
        } else if ((headLayout.getVisibility() != View.VISIBLE) && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(headLayout, "translationY",
                                                (5/4) * mEtInviteCode.getHeight(), 0);
            ObjectAnimator fade;
            if (mEtInviteCode.isFocused()) {
                fade = ObjectAnimator.ofFloat(headLayout, "alpha", 0, 1);
            } else {
                fade = ObjectAnimator.ofFloat(headLayout, "alpha", 0, 0.33f);
            }
            animation.playTogether(move, fade);
        }

        /** 设置头像显示和姓名显示 */
        if(show){
            SoftInputUtil.hideSoftInputMode(InviteCodeActivity.this, mEtInviteCode);

            if(!TextUtils.isEmpty(avatarUrl)){
                ImageLoader.getInstance().displayImage(avatarUrl, mCivSalerAvatar, mOptions);
            }
            if(!TextUtils.isEmpty(salerName)){
                mTvSalerName.setText("来自"+salerName+"的邀请");
            }
        }else{
            commitBtn.setText("跳过");
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    headLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    headLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    AnimatorProxy.wrap(headLayout).setAlpha(show ? 1 : 0);
                }
            });
            animation.start();
        }
    }

}
