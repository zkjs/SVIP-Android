package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.http.post.HttpRequest;
import com.zkjinshi.svip.http.post.HttpRequestListener;
import com.zkjinshi.svip.http.post.HttpResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 完善新用户资料
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CompleteInfoActivity extends Activity {

    private ItemTitleView   mTitle;
    private CircleImageView mCivUserAvatar;
    private EditText        mEtNickName;
    private RadioGroup      mRgSex;
    private RadioButton     mRbMale;
    private RadioButton     mRbFemale;
    private ImageButton     mIbtnQianJin;

    private ImageLoadingListener imageLoadingListener;
    private Bundle               thirdBundledata;
    private HttpRequest          httpRequest;
    private String               mNickName;
    private String               picPath;
    private int                 sexValue;

    private Map<String,String>  stringMap;
    private Map<String,File>    fileMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle         = (ItemTitleView)   findViewById(R.id.itv_title);
        mCivUserAvatar = (CircleImageView) findViewById(R.id.civ_user_avatar);
        mEtNickName    = (EditText)    findViewById(R.id.et_nick_name);
        mRgSex         = (RadioGroup)  findViewById(R.id.rg_sex);
        mRbMale        = (RadioButton) findViewById(R.id.rb_male);
        mRbFemale      = (RadioButton) findViewById(R.id.rb_female);
        mIbtnQianJin   = (ImageButton) findViewById(R.id.ibtn_qian_jin);
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
                mRbMale.setChecked(true);
            }else {
                mRbFemale.setChecked(true);
            }
        }

        mTitle.setTextTitle(getString(R.string.complete_info));
    }

    private void initListener() {

        //左侧后退操作
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteInfoActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //头像选择操作
        mCivUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineUiController.getInstance().showChoosePhotoDialog();
            }
        });

        //性别切换按钮
        mRgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //选择男性
                if (checkedId == R.id.rb_male) {
                }

                //选择女性
                if (checkedId == R.id.rb_female) {
                }
            }
        });

        //前进邀请码输入页面
        mIbtnQianJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //验证昵称
                String nickName = mEtNickName.getText().toString().trim();
                if (TextUtils.isEmpty(nickName)) {
                    DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "请输入昵称", Gravity.CENTER);
                    return;
                }

                //性别选择
                if (!mRbMale.isChecked() && !mRbFemale.isChecked()) {
                    DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "请选择性别", Gravity.CENTER);
                    return;
                }

                DeviceUtils.init(CompleteInfoActivity.this);

                mNickName = mEtNickName.getText().toString();
                picPath   = CacheUtil.getInstance().getPicPath();
                sexValue  = mRgSex.getCheckedRadioButtonId() == R.id.rb_male ? 1 : 0;
                LogUtil.getInstance().info(LogLevel.INFO, "sexValue:" + sexValue);

                httpRequest = new HttpRequest();
                stringMap   = new HashMap<String, String>();
                fileMap     = new HashMap<String, File>();

                stringMap.put("userid", CacheUtil.getInstance().getUserId());
                stringMap.put("token", CacheUtil.getInstance().getToken());
                stringMap.put("phone_os", DeviceUtils.getOS() + " " + DeviceUtils.getSdk());
                stringMap.put("username", nickName);
                stringMap.put("remark", "");
                stringMap.put("preference", "");
                stringMap.put("sex", "" + sexValue);
                if (!TextUtils.isEmpty(picPath)) {
                    fileMap.put("UploadForm[file]", new File(picPath));
                    LogUtil.getInstance().info(LogLevel.INFO, "picPath:" + picPath);
                }
                httpRequest.setRequestUrl(ConfigUtil.getInst().getHttpDomain());
                httpRequest.setRequestMethod(Constants.MODIFY_USER_INFO_METHOD);
                httpRequest.setStringParamMap(stringMap);
                httpRequest.setFileParamMap(fileMap);
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
                                ImageLoader.getInstance().clearDiskCache();
                                ImageLoader.getInstance().clearMemoryCache();
                                DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, CompleteInfoActivity.this.getString(
                                                                         R.string.update_user_info_success), Gravity.CENTER);
                                //进入邀请码页面，并输入邀请码
                                Intent goInviteCode = new Intent(CompleteInfoActivity.this, InviteCodeActivity.class);
                                CompleteInfoActivity.this.startActivity(goInviteCode);
                                finish();
                            } else {
                                DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "上传头像失败!", Toast.LENGTH_LONG);
                            }
                        } else {
                            DialogUtil.getInstance().showCustomToast(CompleteInfoActivity.this, "上传头像失败!", Toast.LENGTH_LONG);
                        }

                    }
                });
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

}
