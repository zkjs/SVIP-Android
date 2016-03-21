package com.zkjinshi.svip.activity.facepay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.PayRecordAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.utils.AESUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.PayRecordVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/3/8.
 */
public class PayConfirmActivity extends BaseActivity {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    private ImageButton backBtn;
    private TextView titleTv;
    private RefreshListView mRefreshListView;
    private PayRecordAdapter mPayRecordAdapter = null;
    private int    mCurrentPage;//记录当前查询页
    private String status = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);
        mContext = this;

        if(!TextUtils.isEmpty(getIntent().getStringExtra("status"))){
            status = getIntent().getStringExtra("status");
        }

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mRefreshListView = (RefreshListView)findViewById(R.id.slv_history_order);
    }

    private void initData() {
        ArrayList<PayRecordDataVo> payRecordDataList = new ArrayList<PayRecordDataVo>();
        mPayRecordAdapter = new PayRecordAdapter(payRecordDataList, PayConfirmActivity.this);
        mRefreshListView.setAdapter(mPayRecordAdapter);
        if(status.equals("0")){
            titleTv.setText("支付确认");
        }else{
            titleTv.setText("支付记录");
        }
        mPayRecordAdapter.status = status;
    }

    public void onResume(){
        super.onResume();
        mCurrentPage = 0;
        loadRecord(mCurrentPage);
        //test();
    }

    public void test(){
        ArrayList<PayRecordDataVo> payRecordDataList = new ArrayList<PayRecordDataVo>();
        for(int i=0;i<10;i++){
            PayRecordDataVo dataVo = new PayRecordDataVo();
            dataVo.setAmount(i+1);
            dataVo.setOrderno(i+1+"15456485485");
            dataVo.setShopname("豪庭至尊大酒店");
            dataVo.setCreatetime("2016-01-06 09:2"+i);
            payRecordDataList.add(dataVo);
        }
        mPayRecordAdapter = new PayRecordAdapter(payRecordDataList, PayConfirmActivity.this);
        mRefreshListView.setAdapter(mPayRecordAdapter);
    }

    private void initListener() {
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 0;
                loadRecord(mCurrentPage);
            }

            @Override
            public void onLoadingMore() {
                loadRecord(mCurrentPage);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
//                int realPostion = position - 1;
//                PayRecordDataVo payRecordDataVo = (PayRecordDataVo)mPayRecordAdapter.getItem(realPostion);
//                if(status.equals("0")){
//                    Intent intent = new Intent(mContext, PayActivity.class);
//                    intent.putExtra("amountStatusVo",payRecordDataVo);
//                    startActivity(intent);
//                }else{
//                    Intent intent = new Intent(mContext,PayDetailActivity.class);
//                    intent.putExtra("payRecordDataVo",payRecordDataVo);
//                    startActivity(intent);
//                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }




    private void loadRecord(int page){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getPayList(status,page);
            client.get(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    //mProgressDialog = ProgressDialog.show( mContext, "" , "", true,true);
                }

                public void onFinish(){
                    super.onFinish();
                   // mProgressDialog.cancel();
                    mRefreshListView.refreshFinish();//结束刷新状态
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        PayRecordVo payRecordVo = new Gson().fromJson(response,PayRecordVo.class);
                        if(payRecordVo == null){
                            return;
                        }
                        if(payRecordVo.getRes() == 0){
                            ArrayList<PayRecordDataVo> payRecordDataList = payRecordVo.getData();
                            if (mCurrentPage == 0) {
                                mPayRecordAdapter.refresh(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                }
                            } else {
                                mPayRecordAdapter.loadMore(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                    //mRefreshListView.setSelection(mPayRecordAdapter.datalist.size() - 1);
                                }

                            }

                        }else{
                            Toast.makeText(mContext, payRecordVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    RequestUtil.onFailure(PayConfirmActivity.this,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
