package com.zkjinshi.svip.activity.call;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.CallMoreAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.PayRecordVo;
import com.zkjinshi.svip.vo.ServiceTagDataSecondVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by dujiande on 2016/6/21.
 */
public class CallTaskActivity extends BaseActivity {

    private final static String TAG = CallTaskActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv;

    private EditText inputEt;
    private Button confirmBtn;

    private ServiceTagDataSecondVo serviceTagDataSecondVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_task);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        inputEt = (EditText)findViewById(R.id.input_et);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("呼叫中心");

        serviceTagDataSecondVo = (ServiceTagDataSecondVo) getIntent().getSerializableExtra("serviceTagDataSecondVo");
    }

    private void initListener() {

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTask();
            }
        });
    }

    public void onResume(){
        super.onResume();
    }

    /**
     * 创建呼叫服务
     */
    private void submitTask(){
        String remark = inputEt.getText().toString();
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            if(serviceTagDataSecondVo != null){
                jsonObject.put("svrid",serviceTagDataSecondVo.getSecondSrvTagId());
                jsonObject.put("desc", remark);
            }
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.serviceTaskCreate();
            client.get(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo baseResponseVo = new Gson().fromJson(response,BaseResponseVo.class);
                        if(baseResponseVo == null){
                            return;
                        }
                        if(baseResponseVo.getRes() == 0){
                            Toast.makeText(mContext, "发送成功",Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }else{
                            Toast.makeText(mContext, baseResponseVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
