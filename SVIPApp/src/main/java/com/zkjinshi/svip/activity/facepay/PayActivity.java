package com.zkjinshi.svip.activity.facepay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.PayRecordDataVo;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/3/9.
 */
public class PayActivity extends Activity {


    private Context mContext;
    private ProgressDialog mProgressDialog;

    public ImageButton backBtn;
    public TextView hotelNameTv,priceTv,orderNoTv,problemTv;
    public Button confirmBtn;

    public PayRecordDataVo amountStatusVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mContext = this;

        amountStatusVo = (PayRecordDataVo)getIntent().getSerializableExtra("amountStatusVo");

        initView();
        initData();
        initListener();
    }

    private void initView() {
        hotelNameTv = (TextView)findViewById(R.id.shopname_tv);
        priceTv = (TextView)findViewById(R.id.price_tv);
        problemTv = (TextView)findViewById(R.id.problem_tv);
        orderNoTv = (TextView)findViewById(R.id.orderno_tv);

        backBtn = (ImageButton)findViewById(R.id.btn_cancel);
        confirmBtn = (Button)findViewById(R.id.confirm_btn);
    }

    private void initData() {
        if(amountStatusVo != null){
            hotelNameTv.setText(amountStatusVo.getShopname());
            priceTv.setText("¥ "+amountStatusVo.getAmount());
            orderNoTv.setText("交易单号："+amountStatusVo.getOrderno());
        }
    }

    private void initListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment(amountStatusVo.getOrderno(),1);
            }
        });

        problemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment(amountStatusVo.getOrderno(),-1);
            }
        });


    }

    private void payment(final String orderno, final int action) {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderno", orderno);
            jsonObject.put("action", action);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.payment();
            client.put(mContext, url, stringEntity, "application/json", new JsonHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    mProgressDialog = ProgressDialog.show( mContext, "" , "", true,true);
                }

                public void onFinish(){
                    super.onFinish();
                    mProgressDialog.cancel();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            if(action == 1){
                                Toast.makeText(mContext,"确认支付成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,"拒绝付款成功",Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
