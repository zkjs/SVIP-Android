package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineActivity;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.vo.UserInfoVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;

/**
 * 开发者：dujiande
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginController {

    private LoginController(){}
    private static LoginController instance;
    private Context context;
    private Activity activity;
    private RequestQueue requestQueue;
    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
        this.requestQueue = Volley.newRequestQueue(context);
    }


    public void addToRequestQueue(Request<?> request) {
        requestQueue.add(request);
    }

    public void cancelAllQueue(){
        requestQueue.cancelAll(context);
    }

    /**
     * 获取用户详细信息
     * @param userid
     * @param token
     * @param isNewRegister 是否是新注册用户
     */
    public void getUserDetailInfo(final String userid, String token,final boolean isNewRegister,final Bundle thirdBundleData) {
        cancelAllQueue();
        DataRequestVolley request = new DataRequestVolley(HttpMethod.GET,
                Constants.GET_USER_DETAIL_URL + "userid=" + userid + "&token=" + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.getInstance().info(LogLevel.INFO, "获取用户详细信息="+response);
                        if(!TextUtils.isEmpty(response)){
                            Gson gson = new Gson();
                            UserInfoResponse userInfoResponse =  gson.fromJson(response, UserInfoResponse.class);
                            if(null != userInfoResponse && userInfoResponse.isSet()){
                                //存数据库
                                UserDetailVo userDetailVo = gson.fromJson(response, UserDetailVo.class);
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
                            }else if(null != userInfoResponse && !userInfoResponse.isSet()){
                                LogUtil.getInstance().info(LogLevel.ERROR,"token 过期");
                                DialogUtil.getInstance().showToast(activity,"token 过期");
                            }
                        }

                        Intent intent = new Intent(activity, CompleteInfoActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

//                        if(isNewRegister){
//                            //TODO:进行完善资料的填写
//                            Intent intent = new Intent(activity, CompleteInfoActivity.class);
//                            if(thirdBundleData != null){
//                                intent.putExtra("from_third", true);
//                                intent.putExtras(thirdBundleData);
//                            }
//                            activity.startActivity(intent);
//                            activity.finish();
//                        } else {
//                            goHome();
//                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.getInstance().info(LogLevel.ERROR, "getUserDetailInfo=>获取用户详细信息失败"+error.toString());
                    }
                });
        request.setRetryPolicy(ProtocolUtil.getDefaultRetryPolicy());
        LogUtil.getInstance().info(LogLevel.INFO, request.toString());
        addToRequestQueue(request);
    }

    private void goHome() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(mainIntent);
        activity.finish();
        activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }
}
