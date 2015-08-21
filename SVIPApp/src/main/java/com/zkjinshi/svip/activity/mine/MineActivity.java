package com.zkjinshi.svip.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.http.HttpRequest;
import com.zkjinshi.svip.http.HttpRequestListener;
import com.zkjinshi.svip.http.HttpResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.Sex;
import com.zkjinshi.svip.vo.UserInfoVo;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 个人信息页面
 * 开发者：JimmyZhang
 * 日期：2015/7/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MineActivity extends Activity{

    private CircleImageView photoImageView;
    private TextView finishIv,titleTv,mobilePhoneTv;
    private EditText userNameEtv,positionEtv,companyEtv;
    private String userNameStr,positionStr,companyStr;
    private RadioGroup sexRp;
    private RadioButton bogyRBtn,girlRBtn;
    private int sexValue;
    private ImageButton backIBtn;
    private Map<String,String> stringMap;
    private Map<String,File> fileMap;
    private HttpRequest httpRequest;
    private StringRequest stringRequest;
    private UserInfoVo userInfoVo;
    private UserInfoResponse userInfoResponse;
    private String mobilePhoneStr;
    private String userPhotoUrl;
    private String userPhotoSuffix;
    private String picPath;
    private Sex sex;
    private ImageLoadingListener imageLoadingListener;
    private Bundle thirdBundledata;

    private void initView(){
        photoImageView = (CircleImageView)findViewById(R.id.mine_set_user_photo_iv);
        finishIv = (TextView)findViewById(R.id.header_bar_tv_finish);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        userNameEtv = (EditText)findViewById(R.id.mine_set_user_name_etv);
        positionEtv = (EditText)findViewById(R.id.mine_set_user_position_etv);
        companyEtv = (EditText)findViewById(R.id.mine_set_user_unit_etv);
        sexRp = (RadioGroup)findViewById(R.id.mine_set_user_sex);
        bogyRBtn = (RadioButton)findViewById(R.id.mine_set_user_male);
        girlRBtn = (RadioButton)findViewById(R.id.mine_set_user_female);
        mobilePhoneTv = (TextView)findViewById(R.id.mine_user_phone_num);
    }

    private void createImageLoadingListener(){
        imageLoadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                File photoFile = ImageLoader.getInstance().getDiskCache().get(thirdBundledata.getString("headimgurl"));
//                String path = photoFile.getAbsolutePath();
//                LogUtil.getInstance().info(LogLevel.INFO,"path:"+path);
//                CacheUtil.getInstance().savePicPath(path);
                String photoFileName = System.currentTimeMillis() + ".jpg";
                String path = FileUtil.getInstance().getImageTempPath() + photoFileName;
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

    private void initData(){
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        boolean isFromThird = getIntent().getBooleanExtra("from_third",false);
        LogUtil.getInstance().info(LogLevel.INFO, "isFromThird:" + isFromThird);
        thirdBundledata = getIntent().getExtras();

        if(isFromThird){
            //初始化名字
            userNameStr = thirdBundledata.getString("nickname");
            userNameEtv.setText(userNameStr);
            CacheUtil.getInstance().setUserName(userNameStr);
            //初始化头像
            String imgUrl = thirdBundledata.getString("headimgurl");
            LogUtil.getInstance().info(LogLevel.INFO, "headimgurl:" + imgUrl);
           // MineUiController.getInstance().setUserPhoto(thirdBundledata.getString("headimgurl"), photoImageView);
            DialogUtil.getInstance().showProgressDialog(this);
            createImageLoadingListener();
            ImageLoader.getInstance().displayImage(imgUrl, photoImageView, MineUiController.getInstance().getOptions(),
                    imageLoadingListener);
            //初始化性别
            String sex = thirdBundledata.getString("sex");
            if(null != sex && sex.equals("1")){
                bogyRBtn.setChecked(true);
            }else {
                girlRBtn.setChecked(true);
            }
        }

        titleTv.setText(getString(R.string.svip_mine));
        backIBtn.setVisibility(View.VISIBLE);
        finishIv.setVisibility(View.VISIBLE);

//        stringRequest = new StringRequest(Request.Method.GET,
//                ProtocolUtil.getUserInfoUrl(CacheUtil.getInstance().getToken(),
//                CacheUtil.getInstance().getUserId()),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        DialogUtil.getInstance().cancelProgressDialog();
//                        LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息响应结果:" + response);
//                        if(!TextUtils.isEmpty(response)){
//                            try {
//                                userInfoResponse =  new Gson().fromJson(response, UserInfoResponse.class);
//                                if(null != userInfoResponse){
//                                    userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
//                                    if(null != userInfoVo){
//                                       setViewData(userInfoVo);
//                                    }
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();;
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                DialogUtil.getInstance().cancelProgressDialog();
//                LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息错误信息:" +  error.getMessage());
//            }
//        });
//        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    private void initListeners(){

        //选择头像
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MineUiController.getInstance().showChoosePhotoDialog();
            }
        });

        //完成
        finishIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceUtils.init(MineActivity.this);
                userNameStr = userNameEtv.getText().toString();
                positionStr = positionEtv.getText().toString();
                companyStr = companyEtv.getText().toString();
                picPath =  CacheUtil.getInstance().getPicPath();
                sexValue = sexRp.getCheckedRadioButtonId() ==  R.id.mine_set_user_male ? 1 : 0;
                LogUtil.getInstance().info(LogLevel.INFO,"sexValue:"+sexValue);
                httpRequest = new HttpRequest();
                stringMap = new HashMap<String, String>();
                fileMap = new HashMap<String, File>();
                stringMap.put("userid",CacheUtil.getInstance().getUserId());
                stringMap.put("token", CacheUtil.getInstance().getToken());
                stringMap.put("phone_os",DeviceUtils.getOS()+" "+DeviceUtils.getSdk());
                stringMap.put("username",userNameStr);
                stringMap.put("remark",positionStr);
                stringMap.put("preference",companyStr);
                stringMap.put("sex",""+sexValue);
                if(!TextUtils.isEmpty(picPath)){
                    fileMap.put("UploadForm[file]", new File(picPath));
                    LogUtil.getInstance().info(LogLevel.INFO, "picPath:" + picPath);
                }
                httpRequest.setRequestUrl(ConfigUtil.getInst().getHttpDomain());
                httpRequest.setRequestMethod(Constants.MODIFY_USER_INFO_METHOD);
                httpRequest.setStringParamMap(stringMap);
                httpRequest.setFileParamMap(fileMap);
                MineNetController.getInstance().requestSetInfoTask(httpRequest,new HttpRequestListener<HttpResponse>(){
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

                        if(null != result && null != result.rawResult){
                            LogUtil.getInstance().info(LogLevel.INFO, "rawResult:" + result.rawResult);
                            BaseResponse baseResponse = new Gson().fromJson(result.rawResult,BaseResponse.class);
                            if(null != baseResponse && baseResponse.isSet()){
                                ImageLoader.getInstance().clearDiskCache();
                                ImageLoader.getInstance().clearMemoryCache();
                                Intent intent = new Intent(MineActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                DialogUtil.getInstance().showCustomToast(MineActivity.this,"长传头像失败!", Toast.LENGTH_LONG);
                            }
                        }else{
                            DialogUtil.getInstance().showCustomToast(MineActivity.this,"长传头像失败!", Toast.LENGTH_LONG);
                        }

                    }
                });
            }
        });

        //返回按钮
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MineUiController.getInstance().onActivityResult(requestCode, resultCode, data, photoImageView);
    }

    /**
     * 根据UserInfoVo设置页面数据
     * @param userInfoVo
     */
    private void setViewData(UserInfoVo userInfoVo){
        userNameStr = userInfoVo.getUsername();
        mobilePhoneStr = userInfoVo.getMobilePhoto();
        positionStr = userInfoVo.getPosition();
        companyStr = userInfoVo.getCompany();
        userPhotoSuffix = userInfoVo.getUserAvatar();
        sex = userInfoVo.getSex();
        if(!TextUtils.isEmpty(userNameStr)){
            userNameEtv.setText(userNameStr);
            CacheUtil.getInstance().setUserName(userNameStr);
        }
        if(!TextUtils.isEmpty(mobilePhoneStr)){
            mobilePhoneTv.setText(mobilePhoneStr);
            CacheUtil.getInstance().setUserPhone(mobilePhoneStr);
        }
        if(!TextUtils.isEmpty(positionStr)){
            positionEtv.setText(positionStr);
        }
        if(!TextUtils.isEmpty(companyStr)){
            companyEtv.setText(companyStr);
        }
        if(null != sex && sex == Sex.BOY){
            bogyRBtn.setChecked(true);
        }else {
            girlRBtn.setChecked(true);
        }
        if(!TextUtils.isEmpty(userPhotoSuffix)){
            userPhotoUrl = ConfigUtil.getInst().getHttpDomain()+userPhotoSuffix;
            CacheUtil.getInstance().saveUserPhotoUrl(userPhotoUrl);
            MineUiController.getInstance().setUserPhoto(userPhotoUrl,photoImageView);
        }
    }
}
