package com.zkjinshi.svip.activity.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.jsonbean.InviteCodeEntity;
import com.zkjinshi.svip.bean.jsonbean.MsgUserDefine;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.PushOfflineMsg;

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
public class InviteCodeActivity extends Activity {

    private Context       mContext;
    private String        mUserID;
    private String        mPhoneNum;
    private String        mToken;
    private String        mUserName;

    private ItemTitleView mTitle;
    private EditText      mEtInviteCode;
    private TextView      mTvInviteCode;
    private ImageButton   mIbtnQianJin;

    private LinearLayout  mLlSalerInfo;
    private ImageView     mCivSalerAvatar;
    private TextView      mTvSalerName;

    private DisplayImageOptions mOptions;
    private String              mSalesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle        = (ItemTitleView) findViewById(R.id.itv_title);
        mEtInviteCode = (EditText)      findViewById(R.id.et_invite_code);
        mTvInviteCode = (TextView)      findViewById(R.id.tv_what_is_invite_code);
        mIbtnQianJin  = (ImageButton)   findViewById(R.id.ibtn_qian_jin);

        mLlSalerInfo    = (LinearLayout)  findViewById(R.id.ll_saler_info);
        mCivSalerAvatar = (ImageView)     findViewById(R.id.civ_saler_avatar);
        mTvSalerName    = (TextView)      findViewById(R.id.tv_saler_name);
    }

    private void initData() {
        mContext  = InviteCodeActivity.this;
        mUserID   = CacheUtil.getInstance().getUserId();
        mPhoneNum = CacheUtil.getInstance().getUserPhone();
        mToken    = CacheUtil.getInstance().getToken();
        mUserName = CacheUtil.getInstance().getUserName();

        mTitle.setTextTitle("");
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodeActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

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
                if (inviteCodeStr.length() >= 6) {
                    String upperCode = inviteCodeStr.toUpperCase();
                    findSalerByInviteCode(upperCode);
                }

                if (TextUtils.isEmpty(inviteCodeStr)) {
                    showSalerInfo(false, null, null);
                }

            }
        });

        mTvInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(InviteCodeActivity.this, "TODO");
            }
        });

        mIbtnQianJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteCode = mEtInviteCode.getText().toString().trim();
                if (!TextUtils.isEmpty(inviteCode) && (mLlSalerInfo.getVisibility() == View.VISIBLE)) {
                    //邀请码验证成功 开始确认并绑定邀请码
                    bindTheSalerWithCode(inviteCode);

                } else {
                    Intent goHome = new Intent(InviteCodeActivity.this, MainActivity.class);
                    startActivity(goHome);
                    InviteCodeActivity.this.finish();
                }
            }
        });
    }

    /**
     * 根据邀请码绑定客服
     */
    private void bindTheSalerWithCode(String inviteCode) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getUserBindInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();
        bizMap.put("userid", mUserID);
        bizMap.put("token", mToken);
        bizMap.put("code", inviteCode);

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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    JSONObject responseObj = new JSONObject(jsonResult);
                    Boolean    isSuccess   = responseObj.getBoolean("set");
                    if(isSuccess) {

                        //发送自定义协议通知被绑定的服务员
                        Gson gson = new Gson();
                        InviteCodeEntity codeBean = new InviteCodeEntity();

                        codeBean.setPhone_number(mPhoneNum);
                        codeBean.setDate(System.currentTimeMillis());
                        codeBean.setUserid(mUserID);
                        codeBean.setUsername(mUserName);
                        String codeBeanJson = gson.toJson(codeBean, InviteCodeEntity.class);

                        if(!TextUtils.isEmpty(mSalesID)){
                            sendMsgBindInviteCodeSuccess(codeBeanJson, mSalesID);
                        }

                        Intent mainIntent = new Intent(InviteCodeActivity.this, MainActivity.class);
                        InviteCodeActivity.this.startActivity(mainIntent);
                        InviteCodeActivity.this.finish();
                        InviteCodeActivity.this.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

                    } else {
                        int errCode = responseObj.getInt("err");
                        if(400 == errCode){
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                    "邀请码已经被使用，请勿重复使用。", Gravity.CENTER);
                        }else {
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
        bizMap.put("salesid", mUserID);
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    JSONObject responseObj = new JSONObject(jsonResult);
                    Boolean isSuccess = responseObj.getBoolean("set");
                    if (isSuccess) {
                        mSalesID = responseObj.getString("salesid");
                        if (!TextUtils.isEmpty(mSalesID)) {
                            String avatarUrl = ProtocolUtil.getAvatarUrl(mSalesID);
                            String salerName = responseObj.getString("username");
                            showSalerInfo(true, avatarUrl, salerName);
                        }
                    } else {
                        DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                "邀请码输入错误，请重新输入！", Gravity.CENTER);
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
     * 绑定邀请码
     */
    private void sendMsgBindInviteCodeSuccess(String content, String toID) {

        //确认绑定邀请码的通知是由何种协议
        //1.MsgUserDefine
        MsgUserDefine msgUserDefine = new MsgUserDefine();
        msgUserDefine.setType(ProtocolMSG.MSG_UserDefine);
        msgUserDefine.setTimestamp(System.currentTimeMillis());

        msgUserDefine.setChildtype(ProtocolMSG.MSG_ChildType_BindInviteCode);
        msgUserDefine.setContent(content);
        msgUserDefine.setFromid(mUserID);
        //        msgUserDefine.setProtover();
        msgUserDefine.setPushalert("绑定邀请码");
        msgUserDefine.setPushofflinemsg(PushOfflineMsg.PUSH_MSG.getValue());
        msgUserDefine.setToid(toID);

        Gson gson = new Gson();
        String msgJson = gson.toJson(msgUserDefine, MsgUserDefine.class);
        WebSocketManager.getInstance().sendMessage(msgJson);
    }

    /**
     * show the saler info when input the invite code correctly
     * @param show
     */
    private void showSalerInfo(final boolean show, String avatarUrl, String salerName) {
        AnimatorSet animation = null;

        if ((mLlSalerInfo.getVisibility() == View.VISIBLE) && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLlSalerInfo, "translationY", 0,
                                                      (5/4) * mEtInviteCode.getHeight());
            ObjectAnimator fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 1, 0);
            animation.playTogether(move, fade);
        } else if ((mLlSalerInfo.getVisibility() != View.VISIBLE) && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLlSalerInfo, "translationY",
                                                (5/4) * mEtInviteCode.getHeight(), 0);
            ObjectAnimator fade;
            if (mEtInviteCode.isFocused()) {
                fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 0, 1);
            } else {
                fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 0, 0.33f);
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
                mTvSalerName.setText(salerName);
            }
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mLlSalerInfo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mLlSalerInfo.setVisibility(show ? View.VISIBLE : View.GONE);
                    AnimatorProxy.wrap(mLlSalerInfo).setAlpha(show ? 1 : 0);
                }
            });
            animation.start();
        }
    }

}
