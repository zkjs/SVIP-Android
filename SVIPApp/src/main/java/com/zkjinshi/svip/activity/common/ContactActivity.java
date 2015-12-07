package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.bean.SalerInfoBean;
import com.zkjinshi.svip.fragment.contacts.Contact;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.ShopListResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.TicketVo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * 联系人主页显示（销售员主页）
 * 开发者：WinkyQin
 * 日期：2015/12/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactActivity extends Activity {

    private final String TAG = ContactActivity.class.getSimpleName();

    private RelativeLayout mRlBack;//返回
    private ImageView      mIvAvatar;//用户头像
    private TextView       mTvLatestOnline;//最近在线时间
    private TextView       mTvShopName;//所属商家
    private TextView       mTvContactName;//联想人姓名
    private TextView       mTvPhoneNumber;//手机号
    private ImageButton    mIbtnDial;//聊天按键
    private TextView       mTvEvaluateScore;//评分
    private CheckBox       mCbOrderEvaluatePoor;//太差
    private CheckBox       mCbOrderEvaluateCommon;//不及格
    private CheckBox       mCbOrderEvaluateGratify;//及格
    private CheckBox       mCbOrderEvaluateGreatGratify;//良好
    private CheckBox       mCbOrderEvaluateHighlyRecommend;//优秀
    private TextView       mTvEvaluationsCount;//评价数量
    private Button         mBtnConsultImmediately;//评价数量

    private String  mUserID;
    private String  mToken;
    private String  mContactID;
    private SalerInfoBean mSalerInfo;

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack   = (RelativeLayout) findViewById(R.id.rl_back);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvLatestOnline  = (TextView) findViewById(R.id.tv_latest_online);
        mTvShopName      = (TextView) findViewById(R.id.tv_shop_name);
        mTvContactName   = (TextView) findViewById(R.id.tv_contact_name);
        mTvPhoneNumber   = (TextView) findViewById(R.id.tv_phone_number);
        mIbtnDial        = (ImageButton) findViewById(R.id.ibtn_dial);
        mTvEvaluateScore = (TextView) findViewById(R.id.tv_evaluate_score);
        mCbOrderEvaluatePoor            = (CheckBox) findViewById(R.id.cb_order_evaluate_poor);
        mCbOrderEvaluateCommon          = (CheckBox) findViewById(R.id.cb_order_evaluate_common);
        mCbOrderEvaluateGratify         = (CheckBox) findViewById(R.id.cb_order_evaluate_gratify);
        mCbOrderEvaluateGreatGratify    = (CheckBox) findViewById(R.id.cb_order_evaluate_great_gratify);
        mCbOrderEvaluateHighlyRecommend = (CheckBox) findViewById(R.id.cb_order_evaluate_highly_recommend);
        mTvEvaluationsCount    = (TextView) findViewById(R.id.tv_evaluation_count);
        mBtnConsultImmediately = (Button) findViewById(R.id.btn_consult_immediately);
    }

    private void initData() {

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();

        mContactID = getIntent().getStringExtra("contact_id");
        if(!TextUtils.isEmpty(mContactID)){
            getContactInfoByUserID(mUserID, mToken, mContactID);
        }

        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_hotel_anli03)
                .showImageForEmptyUri(R.mipmap.img_hotel_anli03)
                .showImageOnFail(R.mipmap.img_hotel_anli03)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private void initListener() {

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactActivity.this.finish();
            }
        });

        mIbtnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String phoneNumber = mTvPhoneNumber.getText().toString();
               if(TextUtils.isEmpty(phoneNumber)){
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_CALL);
                   intent.setData(Uri.parse("tel:" + phoneNumber));
                   ContactActivity.this.startActivity(intent);
               }
            }
        });

        mBtnConsultImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:进入聊天界面
                Intent goChat = new Intent(ContactActivity.this, ChatActivity.class);
                if(null != mSalerInfo){
                    String fromName   = CacheUtil.getInstance().getUserName();
                    String salerID    = mSalerInfo.getUserid();
                    String salerName  = mSalerInfo.getUsername();
                    String shopName   = mSalerInfo.getShop_name();
                    String shopId     = mSalerInfo.getShopid();
                    if (!TextUtils.isEmpty(salerID)) {
                        goChat.putExtra(Constants.EXTRA_FROM_NAME, fromName);
                    }
                    if (!TextUtils.isEmpty(salerID)) {
                        goChat.putExtra(Constants.EXTRA_USER_ID, salerID);
                    }
                    if (!TextUtils.isEmpty(salerName)) {
                        goChat.putExtra(Constants.EXTRA_TO_NAME, salerName);
                    }
                    if (!TextUtils.isEmpty(shopId)) {
                        goChat.putExtra(Constants.EXTRA_SHOP_ID, shopId);
                    }
                    if (!TextUtils.isEmpty(shopName)) {
                        goChat.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                    }
                    ContactActivity.this.startActivity(goChat);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    ContactActivity.this.finish();
                }
            }
        });
    }

    /**
     * 根据销售服务人员ID获取销售信息
     * @param userID
     */
    private void getContactInfoByUserID(String userID, String token, String salesID) {
        String url = ProtocolUtil.getSalesUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid",userID);
        bizMap.put("token", token);
        bizMap.put("sid", salesID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
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
                    Gson gson = new Gson();
                    mSalerInfo = gson.fromJson(result.rawResult, SalerInfoBean.class);
                    if (null!=mSalerInfo && mSalerInfo.isSet()) {
                        showSalerInfo(mSalerInfo);
                    } else {
                        DialogUtil.getInstance().showToast(ContactActivity.this, "获取销售详情失败。");
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
     * 显示销售员信息
     * @param salerInfo
     */
    private void showSalerInfo(SalerInfoBean salerInfo) {

        String userID      = salerInfo.getUserid();
        String userName    = salerInfo.getUsername();
        String phoneNumber = salerInfo.getPhone();
        String shopName    = salerInfo.getShop_name();
        String city        = salerInfo.getCity();
        String shopId      = salerInfo.getShopid();
        int    sex         = salerInfo.getSex();
        String realName    = salerInfo.getReal_name();

        if(!TextUtils.isEmpty(userID)){
            String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
            ImageLoader.getInstance().displayImage(avatarUrl, mIvAvatar, mOptions);
        }

        if(!TextUtils.isEmpty(userName)){
            mTvContactName.setText(userName);
        }

        if(!TextUtils.isEmpty(phoneNumber)){
            mTvPhoneNumber.setText(phoneNumber);
        }

        if(!TextUtils.isEmpty(shopName)){
            mTvShopName.setText(shopName);
        }
    }
}
