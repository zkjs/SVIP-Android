package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;

import java.util.HashMap;

/**
 * Created by dujiande on 2015/9/10.
 */
public class SettingNologinActivity extends BaseActivity {

    private final static String TAG = SettingNologinActivity.class.getSimpleName();

    private ItemTitleView mTitle;
    private TextView mFlagTv;
    private ToggleButton mToggleBtn;
    private boolean mIsNologin;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void submitInfo(final String fieldKey, final String fieldValue){

        String url = ProtocolUtil.getUserUploadUrl();
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",  CacheUtil.getInstance().getToken());
        bizMap.put(fieldKey, fieldValue);

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

                if (null != result && null != result.rawResult) {
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult, BaseResponse.class);
                    if (null != baseResponse && baseResponse.isSet()) {

                    } else {
                        //DialogUtil.getInstance().showCustomToast(SettingNologinActivity.this, "修改失败!", Toast.LENGTH_LONG);
                    }
                } else {
                    DialogUtil.getInstance().showCustomToast(SettingNologinActivity.this, "修改失败!", Toast.LENGTH_LONG);
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
