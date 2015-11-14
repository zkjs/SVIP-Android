package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.TicketVo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by djd on 2015/8/24.
 */
public class SettingTicketsItemActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();
    private boolean isNew = true;
    private TicketVo tickeData = null;
    private int position;

    private ItemTitleView mTitle;
    private EditText  mInputEt;
    private CheckBox mDefaultRbtn;
    private Button mSaveBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ticket_item);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mInputEt          = (EditText)findViewById(R.id.et_setting_input);
        mDefaultRbtn      = (CheckBox)findViewById(R.id.ticket_checkbox);
        mSaveBtn          = (Button)findViewById(R.id.btn_confirm);

    }

    private void initData() {
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            isNew = true;
            mTitle.setTextTitle("新增发票");
        }else{
            Object data = bundle.getSerializable("TicketVo");
            position = bundle.getInt("position");
            isNew = false;
            mTitle.setTextTitle("修改发票"+(position+1));
            tickeData = (TicketVo)data;
            mInputEt.setText(tickeData.getInvoice_title());
            mDefaultRbtn.setChecked(tickeData.getIs_default().equals("1"));
        }


    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNew) {
                    addTicket();
                } else {
                    updateTicket();
                }
            }
        });

    }

    //添加发票
    private void addTicket(){
        if(StringUtil.isEmpty(mInputEt.getText().toString())){
            DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"发票抬头不能为空。");
            return;
        }
        String url = ProtocolUtil.addTicketUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("invoice_title",mInputEt.getText().toString());
        bizMap.put("is_default",mDefaultRbtn.isChecked() ? "1":"0");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    BaseResponse baseResponse = gson.fromJson( result.rawResult,BaseResponse.class);
                    if(baseResponse.isSet()){
                        DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"保存发票成功。");
                        back();
                    }else{
                        DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this, "保存发票失败。");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }



    //修改发票
    private void updateTicket(){
        if(StringUtil.isEmpty(mInputEt.getText().toString())){
            DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"发票抬头不能为空。");
            return;
        }
        String url = ProtocolUtil.updateTicketUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("id",tickeData.getId()+"");
        bizMap.put("set","2");
        bizMap.put("invoice_title",mInputEt.getText().toString());
        bizMap.put("is_default",mDefaultRbtn.isChecked() ? "1":"0");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    BaseResponse baseResponse = gson.fromJson(result.rawResult,BaseResponse.class);
                    if(baseResponse.isSet()){
                        DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"更新发票成功。");
                        back();
                    }else{
                        DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this, "更新发票失败。");
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }



    //回退
    private void back(){
        startActivity(new Intent(SettingTicketsItemActivity.this,SettingTicketsActivity.class));
        finish();
    }
}
