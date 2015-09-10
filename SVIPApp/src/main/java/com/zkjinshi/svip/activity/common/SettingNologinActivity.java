package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
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

import java.util.HashMap;

/**
 * Created by dujiande on 2015/9/10.
 */
public class SettingNologinActivity extends Activity {

    private ItemTitleView mTitle;
    private TextView mFlagTv;
    private ToggleButton mToggleBtn;
    private boolean mIsNologin;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_nologin);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mFlagTv           = (TextView) findViewById(R.id.tv_flag);
        mToggleBtn        = (ToggleButton)findViewById(R.id.toggleButton);
        mWebView          = (WebView)findViewById(R.id.webView);
    }

    private void initData() {
        mTitle.setTextTitle("免前台");
        mTitle.getmRight().setVisibility(View.GONE);
        //设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        mWebView.loadUrl("http://iwxy.cc/mqt/");

    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsNologin = isChecked;
                if (isChecked) {
                    mFlagTv.setText("开启");
                    //submitInfo("nologin","1");
                } else {
                    mFlagTv.setText("关闭");
                    //submitInfo("nologin","0");
                }
            }
        });
    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){
        HttpRequest httpRequest = new HttpRequest();
        HashMap<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("userid", CacheUtil.getInstance().getUserId());
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

                    } else {
                        //DialogUtil.getInstance().showCustomToast(SettingNologinActivity.this, "修改失败!", Toast.LENGTH_LONG);
                    }
                } else {
                    DialogUtil.getInstance().showCustomToast(SettingNologinActivity.this, "修改失败!", Toast.LENGTH_LONG);
                }

            }
        });
    }

}
