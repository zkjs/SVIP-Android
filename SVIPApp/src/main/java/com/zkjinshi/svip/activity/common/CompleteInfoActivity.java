package com.zkjinshi.svip.activity.common;

import android.content.Intent;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;


import com.zkjinshi.svip.base.BaseActivity;


import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.utils.CacheUtil;

import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.vo.BaseResponseVo;

import org.json.JSONObject;

import java.io.File;

import java.io.UnsupportedEncodingException;


import cz.msebera.android.httpclient.Header;

/**
 * 完善新用户资料
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CompleteInfoActivity extends BaseActivity {

    private SimpleDraweeView mCivUserAvatar;
    private EditText        mEtNickName;
    private Button mIbtnQianJin;
    private CheckBox cbSex;
    private ImageView clearNickNameIv;
    private Drawable leftNickNameDrawable;

    private String               mNickName;
    private String               picPath;
    private int                 sexValue;
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mCivUserAvatar = (SimpleDraweeView) findViewById(R.id.civ_user_avatar);
        mEtNickName    = (EditText)    findViewById(R.id.et_nick_name);
        mIbtnQianJin   = (Button) findViewById(R.id.ibtn_qian_jin);
        cbSex = (CheckBox)findViewById(R.id.cb_sex);
        clearNickNameIv = (ImageView)findViewById(R.id.login_iv_clear_nick_name);
    }

    private void initData() {
        MineUiController.getInstance().init(this);
    }

    private void initListener() {

        //清空姓名
        clearNickNameIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtNickName.setText("");
            }
        });

        //输入姓名
        mEtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nickNameStr = s.toString();
                if(TextUtils.isEmpty(nickNameStr)){
                    clearNickNameIv.setVisibility(View.GONE);
                    leftNickNameDrawable = getResources().getDrawable(
                            R.mipmap.ic_yonghu_nor);
                    leftNickNameDrawable.setBounds(0, 0, leftNickNameDrawable.getMinimumWidth(),
                            leftNickNameDrawable.getMinimumHeight());
                    mEtNickName.setCompoundDrawables(leftNickNameDrawable, null, null, null);
                }else{
                    clearNickNameIv.setVisibility(View.VISIBLE);
                    leftNickNameDrawable = getResources().getDrawable(
                            R.mipmap.ic_yonghu_pre);
                    leftNickNameDrawable.setBounds(0, 0, leftNickNameDrawable.getMinimumWidth(),
                            leftNickNameDrawable.getMinimumHeight());
                    mEtNickName.setCompoundDrawables(leftNickNameDrawable, null, null, null);
                }
            }
        });

        //头像选择操作
        mCivUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineUiController.getInstance().showChoosePhotoDialog();
            }
        });

        //前进邀请码输入页面
        mIbtnQianJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNickName = mEtNickName.getText().toString().trim();
                picPath   = CacheUtil.getInstance().getPicPath();
                if (TextUtils.isEmpty(mNickName)) {
                    DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this,
                            getString(R.string.please_input_username), Gravity.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(picPath)){
                    DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this,
                            getString(R.string.please_input_userphoto), Gravity.CENTER);
                    return;
                }
                sexValue  = cbSex.isChecked() ? 0 : 1;
                updateUserInfo(mNickName,sexValue+"",picPath);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获得图片选择后的操作
        MineUiController.getInstance().onActivityResult(requestCode, resultCode, data, mCivUserAvatar);
    }



    /**
     * 注册流程-更新si信息
     * @param username
     * @param sex
     * @param image
     */
    private void updateUserInfo(String username,String sex,String image){
        try{
            String url = ProtocolUtil.registerUpdateSi();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(5000);
            String token = CacheUtil.getInstance().getExtToken();
            client.addHeader("Token", token);
            RequestParams params = new RequestParams();
            params.put("sex",sex);
            params.put("realname",username);
            params.put("image",new File(image));
            client.post(url, params, new AsyncHttpResponseHandler() {

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(CompleteInfoActivity.this,"");
                }
                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo updateSiResponse = new Gson().fromJson(response, BaseResponseVo.class);
                        if(updateSiResponse == null){
                            return;
                        }
                        if(updateSiResponse.getRes() == 0){//更新成功
                            CacheUtil.getInstance().setExtToken(updateSiResponse.getToken());
                            getUserInfo();

                        }else if(updateSiResponse.getRes() == 30004){//更新失败-数据库更新出错
                            DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "数据库更新出错!", Toast.LENGTH_LONG);
                        }else if(updateSiResponse.getRes() == 30003){//更新失败-上传头像失败
                            DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "上传头像失败!", Toast.LENGTH_LONG);
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "上传资料失败!", Toast.LENGTH_LONG);
                    RequestUtil.onFailure(CompleteInfoActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getUserInfo(){
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                CacheUtil.getInstance().savePicPath("");
                //进入邀请码页面，并输入邀请码
                Intent goInviteCode = new Intent(CompleteInfoActivity.this, MainActivity.class);
                CompleteInfoActivity.this.startActivity(goInviteCode);
                finish();
            }
        });
    }

}
