package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.ClipboardUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.SalerInfoBean;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;

import java.util.HashMap;

/**
 * Created by dujiande on 2016/1/25.
 */
public class AddFriendDetailActivity extends BaseActivity {

    private final String TAG = AddFriendDetailActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private CircleImageView mIvAvatar;//用户头像
    private TextView       mTvContactName;//姓名
    private TextView       mTvPhoneNumber;//手机号
    private TextView       mTvShopName;//所属商家
    private Button addBtn;//添加到通讯录


    private DisplayImageOptions mOptions;

    private String mContactID;
    private SalerInfoBean mSalerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_detail);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);

        mIvAvatar = (CircleImageView) findViewById(R.id.civ_avatar);
        mTvShopName      = (TextView) findViewById(R.id.shopName_tv);
        mTvContactName   = (TextView) findViewById(R.id.name_tv);
        mTvPhoneNumber   = (TextView) findViewById(R.id.phone_tv);
        addBtn = (Button) findViewById(R.id.btn_add);

    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("添加");
        mContactID = getIntent().getStringExtra("contact_id");

        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();

        if(!TextUtils.isEmpty(mContactID)){
            getContactInfoByUserID(CacheUtil.getInstance().getUserId(), CacheUtil.getInstance().getToken(), mContactID);
        }


    }

    private void initListener() {

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(AddFriendDetailActivity.this,LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                addContact();
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
                LogUtil.getInstance().info(LogLevel.INFO, TAG + "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    mSalerInfo = gson.fromJson(result.rawResult, SalerInfoBean.class);
                    if (null!=mSalerInfo && mSalerInfo.isSet()) {
                        showSalerInfo(mSalerInfo);
                    } else {
                        LogUtil.getInstance().info(LogLevel.INFO, TAG + "获取销售详情失败。");
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

        if(!TextUtils.isEmpty(userID)){
            String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
            ImageLoader.getInstance().displayImage(avatarUrl, mIvAvatar, mOptions);
        }

        if(!TextUtils.isEmpty(userName)){
            mTvContactName.setText(userName);
        }

        if(!TextUtils.isEmpty(phoneNumber)){
            mTvPhoneNumber.setText(phoneNumber);
        } else {
            mTvPhoneNumber.setText(getString(R.string.current_none));
        }
        if(!TextUtils.isEmpty(shopName)){
            mTvShopName.setText(shopName);
        }
    }



    //添加好友
    public void addContact(){
        if(TextUtils.isEmpty(mSalerInfo.getShopid())){
            return;
        }
        String url = ProtocolUtil.addfuser();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("fuid", mContactID);
        bizMap.put("userid",CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid",mSalerInfo.getShopid());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(AddFriendDetailActivity.this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(AddFriendDetailActivity.this) {
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
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if(baseBean != null){
                        if(baseBean.isSet()){
                            //发送透传信息
                            EMConversationHelper.getInstance().sendSaleCmdMessage(
                                    CacheUtil.getInstance().getUserId(),
                                    CacheUtil.getInstance().getUserName(),
                                    mContactID,
                                    new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            LogUtil.getInstance().info(LogLevel.INFO, TAG + "透传信息发送成功");
                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            LogUtil.getInstance().info(LogLevel.INFO, TAG + "透传信息发送失败");
                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    }
                            );
                            //发送文本信息
                            EMConversationHelper.getInstance().sendTxtMessage(
                                    "我已添加你为联系人",
                                    mContactID,
                                    mSalerInfo.getUsername(),
                                    CacheUtil.getInstance().getUserName(),
                                    mSalerInfo.getShopid(),
                                    mSalerInfo.getShop_name(),
                                    new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    LogUtil.getInstance().info(LogLevel.INFO, TAG + "文本信息发送成功");
                                }

                                @Override
                                public void onError(int i, String s) {
                                    LogUtil.getInstance().info(LogLevel.INFO, TAG + "文本信息发送失败");
                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                            DialogUtil.getInstance().showToast(AddFriendDetailActivity.this,"添加联系人成功!");
                            BaseApplication.getInst().clearLeaveTop();
                        }else{
                            if(baseBean.getErr().equals("304")){
                                DialogUtil.getInstance().showToast(AddFriendDetailActivity.this,"同一商家只能添加一名联系人!");
                            }else{
                                DialogUtil.getInstance().showToast(AddFriendDetailActivity.this,"添加联系人失败!");
                            }
                        }
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