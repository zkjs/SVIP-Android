
package com.zkjinshi.svip.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.zkjinshi.svip.utils.Constants;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private final static String TAG = WXEntryActivity.class.getSimpleName();
    private IWXAPI api;
    public static BaseResp resp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i(TAG," public void onReq(BaseReq arg0)");
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        Log.i(TAG," public void onResp(BaseResp resp)");
        if (resp != null) {
            this.resp = resp;
        }
        switch(resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result ="发送成功";
               // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                finish();
                break;
        }

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }
}
