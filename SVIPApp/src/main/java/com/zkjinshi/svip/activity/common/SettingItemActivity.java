package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.http.HttpRequest;
import com.zkjinshi.svip.http.HttpRequestListener;
import com.zkjinshi.svip.http.HttpResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.ItemTitleView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;

/**
 * 开发者：杜健德
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SettingItemActivity extends Activity implements View.OnClickListener  {

    private ItemTitleView mTitle;//返回
    private TextView mTipsTv;
    private EditText mInputEt;
    private Button   mSaveBtn;
    private String fieldKey;
    private int modifyType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_item);

        initView();//初始化view
        initData();//初始化data加入

    }

    private void initView() {

        mInputEt = (EditText)findViewById(R.id.et_setting_input);
        mTipsTv = (TextView)findViewById(R.id.tv_setting_tips);
        mSaveBtn = (Button)findViewById(R.id.btn_confirm);
        mSaveBtn.setOnClickListener(this);

        mTitle = (ItemTitleView)findViewById(R.id.itv_setting_title);

    }

    private void initData() {
        mTitle.getmRight().setVisibility(View.INVISIBLE);
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modifyType = getIntent().getIntExtra("modify_type",Constants.FLAG_MODIFY_REAL_NAME);
        fieldKey = getIntent().getStringExtra("field_key");
        String titleStr = getIntent().getStringExtra("title");
        mTitle.setTextTitle(titleStr);

        mInputEt.setHint(getIntent().getStringExtra("hint"));
        mTipsTv.setText(getIntent().getStringExtra("tips"));

        String fieldValue = getIntent().getStringExtra("field_value");

        if(!TextUtils.isEmpty(fieldValue)){
            mInputEt.setText(fieldValue);
        }



    }

    @Override
    public void onClick(View view) {
        String fieldValue = mInputEt.getText().toString();
        if(TextUtils.isEmpty(fieldValue)){
            DialogUtil.getInstance().showToast(this,"不能为空！");
            return;
        }
        if(fieldKey.equals("email") )
        {
            if(fieldValue.matches("^\\w+@\\w+\\.(com|cn)")){
                mTipsTv.setText("");
            }else{
                mTipsTv.setText("格式错误！");
                return;
            }
        }
        submitInfo(fieldKey,fieldValue);
    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){
        HttpRequest httpRequest = new HttpRequest();
        HashMap<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("userid",CacheUtil.getInstance().getUserId());
        stringMap.put("token", CacheUtil.getInstance().getToken());
        stringMap.put(fieldKey,fieldValue);
        httpRequest.setRequestUrl(ConfigUtil.getInst().getHttpDomain());
        httpRequest.setRequestMethod(Constants.MODIFY_USER_INFO_METHOD);
        httpRequest.setStringParamMap(stringMap);
        MineNetController.getInstance().init(this);
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
                       Intent data = new Intent();
                        data.putExtra("new_value",fieldValue);
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        DialogUtil.getInstance().showCustomToast(SettingItemActivity.this, "修改失败!", Toast.LENGTH_LONG);
                    }
                } else {
                    DialogUtil.getInstance().showCustomToast(SettingItemActivity.this, "修改失败!", Toast.LENGTH_LONG);
                }

            }
        });
    }
}
