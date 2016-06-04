package com.zkjinshi.ritz.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.base.BaseActivity;
import com.zkjinshi.ritz.manager.SSOManager;
import com.zkjinshi.ritz.manager.YunBaSubscribeManager;
import com.zkjinshi.ritz.utils.AsyncHttpClientUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.utils.ProtocolUtil;
import com.zkjinshi.ritz.vo.BaseResponseVo;
import com.zkjinshi.ritz.vo.PayloadVo;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dujiande on 2016/3/8.
 */
public class UploadAvatarActivity extends BaseActivity {

    private Context mContext;
    private SimpleDraweeView avatarSdv;
    private String               picPath;
    private TextView uploadTv;
    private View maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);
        mContext = this;
        MineUiController.getInstance().init(this);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获得图片选择后的操作
        MineUiController.getInstance().onActivityResult(requestCode, resultCode, data, avatarSdv,uploadTv);
    }

    private void initView() {
        avatarSdv = (SimpleDraweeView)findViewById(R.id.civ_user_icon);
        uploadTv = (TextView)findViewById(R.id.upload_tv);
        maskView = findViewById(R.id.mask_view);
    }

    private void initData() {
        CacheUtil.getInstance().savePicPath("");
    }

    private void initListener() {
        avatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MineUiController.getInstance().showChoosePhotoDialog();
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picPath   = CacheUtil.getInstance().getPicPath();
                if(TextUtils.isEmpty(picPath)){
                    Toast.makeText(mContext,"请上传身份识别头像",Toast.LENGTH_SHORT).show();
                }
                updateUserInfo(CacheUtil.getInstance().getUserName(),"-1",picPath);
            }
        });

        findViewById(R.id.info_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(view);
            }
        });
    }

    private void showPopupWindow(View view) {
        maskView.setVisibility(View.VISIBLE);
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate( R.layout.pop_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_tishikuang));
        ColorDrawable color = new ColorDrawable(Color.parseColor("#8f101010"));
        popupWindow.setBackgroundDrawable(color);
        // 设置好参数之后再show
        int offsetX = DisplayUtil.dip2px(this,200);
        int offsetY = DisplayUtil.dip2px(this,45);
        popupWindow.showAsDropDown(view,-offsetX,-offsetY);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                maskView.setVisibility(View.GONE);
            }
        });
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
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
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
                            DialogUtil.getInstance().showCustomToast(mContext, "数据库更新出错!", Toast.LENGTH_LONG);
                        }else if(updateSiResponse.getRes() == 30003){//更新失败-上传头像失败
                            DialogUtil.getInstance().showCustomToast(mContext, "上传头像失败!", Toast.LENGTH_LONG);
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    DialogUtil.getInstance().showCustomToast(mContext, "上传资料失败!", Toast.LENGTH_LONG);
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
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
                Intent goInviteCode = new Intent(mContext, MainActivity.class);
                startActivity(goInviteCode);
                finish();
            }
        });
    }
}
