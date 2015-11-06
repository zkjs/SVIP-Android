package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;

import com.zkjinshi.svip.R;

import com.zkjinshi.svip.factory.UserInfoFactory;

import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.vo.UserInfoVo;


/**
 * 开发者：dujiande
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginController {

    private final static String TAG = LoginController.class.getSimpleName();

    private LoginController(){}
    private static LoginController instance;
    private Context context;
    private Activity activity;

    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
    }




    /**
     * 获取用户详细信息
     * @param userid
     * @param token
     * @param isNewRegister 是否是新注册用户
     */
    public void getUserDetailInfo(final String userid, String token,final boolean isNewRegister,final Bundle thirdBundleData) {

        String url =  Constants.GET_USER_DETAIL_URL + "userid=" + userid + "&token=" + token;
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    UserInfoResponse userInfoResponse =  gson.fromJson(result.rawResult, UserInfoResponse.class);
                    if(null != userInfoResponse && userInfoResponse.isSet()){
                        //存数据库
                        UserDetailVo userDetailVo = gson.fromJson(result.rawResult, UserDetailVo.class);
                        UserDetailDBUtil.getInstance().addUserDetail(userDetailVo);
                        //存缓存
                        UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                        if(null != userInfoVo){
                            String userPhotoSuffix = userInfoVo.getUserAvatar();
                            if(!TextUtils.isEmpty(userPhotoSuffix)){
                                String userPhotoUrl = ConfigUtil.getInst().getHttpDomain()+userPhotoSuffix;
                                //保存头像到本地
                                CacheUtil.getInstance().saveUserPhotoUrl(userPhotoUrl);
                            }
                            CacheUtil.getInstance().saveTagsOpen(userInfoVo.isTagopen());
                            CacheUtil.getInstance().setUserPhone(userInfoVo.getMobilePhoto());
                            CacheUtil.getInstance().setUserName(userInfoVo.getUsername());
                        }

                       /* Intent intent = new Intent(activity, CompleteInfoActivity.class);
                        activity.startActivity(intent);
                        activity.finish();*/

                        if(isNewRegister){
                            //TODO:进行完善资料的填写
                            Intent intent = new Intent(activity, CompleteInfoActivity.class);
                            if(thirdBundleData != null){
                                intent.putExtra("from_third", true);
                                intent.putExtras(thirdBundleData);
                            }
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            goHome();
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

    private void goHome() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(mainIntent);
        activity.finish();
        activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }
}
