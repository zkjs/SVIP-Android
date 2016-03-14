package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseFornaxResponse;
import com.zkjinshi.svip.response.BasePavoResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.UpdateSiResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * 完善新用户资料
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CompleteInfoActivity extends BaseActivity {

    private CircleImageView mCivUserAvatar;
    private EditText        mEtNickName;
    private Button mIbtnQianJin;
    private CheckBox cbSex;
    private ImageView clearNickNameIv;
    private Drawable leftNickNameDrawable;

    private ImageLoadingListener imageLoadingListener;
    private Bundle               thirdBundledata;
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
        mCivUserAvatar = (CircleImageView) findViewById(R.id.civ_user_avatar);
        mEtNickName    = (EditText)    findViewById(R.id.et_nick_name);
        mIbtnQianJin   = (Button) findViewById(R.id.ibtn_qian_jin);
        cbSex = (CheckBox)findViewById(R.id.cb_sex);
        clearNickNameIv = (ImageView)findViewById(R.id.login_iv_clear_nick_name);
    }

    private void initData() {
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);

        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);

        boolean isFromThird = getIntent().getBooleanExtra("from_third",false);
        LogUtil.getInstance().info(LogLevel.INFO, "isFromThird:" + isFromThird);
        thirdBundledata = getIntent().getExtras();

        if(isFromThird){
            //初始化名字
            mNickName = thirdBundledata.getString("nickname");
            mEtNickName.setText(mNickName);
            CacheUtil.getInstance().setUserName(mNickName);

            //初始化头像
            String imgUrl = thirdBundledata.getString("headimgurl");
            LogUtil.getInstance().info(LogLevel.INFO, "headimgurl:" + imgUrl);
            DialogUtil.getInstance().showProgressDialog(this);
            createImageLoadingListener();
            ImageLoader.getInstance().displayImage(imgUrl, mCivUserAvatar,
                                                   MineUiController.getInstance().
                                                   getOptions(), imageLoadingListener);
            //初始化性别
            String sex = thirdBundledata.getString("sex");
            if(null != sex && sex.equals("1")){
                cbSex.setChecked(false);
            }else {
                cbSex.setChecked(true);
            }
        }

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
     * 图片载入监听器
     */
    private void createImageLoadingListener(){
        imageLoadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {}

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                String photoFileName = System.currentTimeMillis() + ".jpg";
                String path          = FileUtil.getInstance().getImageTempPath() + photoFileName;
                File f = new File(path);
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    loadedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    LogUtil.getInstance().info(LogLevel.INFO, "已经保存");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LogUtil.getInstance().info(LogLevel.INFO, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.getInstance().info(LogLevel.INFO,  e.getMessage());
                }
                finally {
                    LogUtil.getInstance().info(LogLevel.INFO, "path:" + path);
                    CacheUtil.getInstance().savePicPath(path);
                    DialogUtil.getInstance().cancelProgressDialog();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        };
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
                        UpdateSiResponse updateSiResponse = new Gson().fromJson(response, UpdateSiResponse.class);
                        if(updateSiResponse == null){
                            return;
                        }
                        if(updateSiResponse.getRes() == 0){//更新成功
//                            ImageLoader.getInstance().getDiskCache().remove(userPhotoUrl);
//                            ImageLoader.getInstance().getMemoryCache().remove(userPhotoUrl);
//                            ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                            Uri uri = Uri.parse(userPhotoUrl);
//                            imagePipeline.evictFromMemoryCache(uri);
//                            imagePipeline.evictFromDiskCache(uri);
                            CacheUtil.getInstance().setExtToken(updateSiResponse.getToken());
                            CacheUtil.getInstance().savePicPath("");
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
                //进入邀请码页面，并输入邀请码
                Intent goInviteCode = new Intent(CompleteInfoActivity.this, InviteCodeActivity.class);
                CompleteInfoActivity.this.startActivity(goInviteCode);
                finish();
            }
        });
    }

}
