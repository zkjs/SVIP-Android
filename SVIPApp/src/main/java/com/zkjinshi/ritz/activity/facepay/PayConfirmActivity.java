package com.zkjinshi.ritz.activity.facepay;

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
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.activity.common.BeaconMsgActivity;
import com.zkjinshi.ritz.adapter.PayConfirmAdapter;
import com.zkjinshi.ritz.base.BaseActivity;
import com.zkjinshi.ritz.listener.OnRefreshListener;
import com.zkjinshi.ritz.sqlite.BeaconMsgDBUtil;
import com.zkjinshi.ritz.utils.AsyncHttpClientUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.utils.Constants;
import com.zkjinshi.ritz.utils.ProtocolUtil;

import com.zkjinshi.ritz.utils.qclCopy.BlurBehind;
import com.zkjinshi.ritz.utils.qclCopy.OnBlurCompleteListener;
import com.zkjinshi.ritz.view.RefreshListView;
import com.zkjinshi.ritz.vo.PayRecordDataVo;
import com.zkjinshi.ritz.vo.PayRecordVo;
import com.zkjinshi.ritz.vo.YunBaMsgVo;

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
    private PayConfirmAdapter mPayRecordAdapter = null;
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
        mPayRecordAdapter = new PayConfirmAdapter(payRecordDataList, PayConfirmActivity.this);
        mRefreshListView.setAdapter(mPayRecordAdapter);
        titleTv.setText("消息");
        mPayRecordAdapter.status = status;
    }

    public void onResume(){
        super.onResume();
        mCurrentPage = 0;
        loadRecord(mCurrentPage);
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
                int realPostion = position - 1;
                final PayRecordDataVo payRecordDataVo = (PayRecordDataVo)mPayRecordAdapter.getItem(realPostion);
                if(payRecordDataVo.getYunBaMsgVo() == null){
                    Intent intent = new Intent(mContext, PayActivity.class);
                    intent.putExtra("amountStatusVo",payRecordDataVo);
                    startActivity(intent);
                }else{

                    BlurBehind.getInstance().execute(PayConfirmActivity.this, new OnBlurCompleteListener() {
                        @Override
                        public void onBlurComplete() {
                            Intent bIntent = new Intent(mContext,BeaconMsgActivity.class);
                            bIntent.putExtra("data",payRecordDataVo.getYunBaMsgVo());
                            bIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(bIntent);
                            //overridePendingTransition(R.anim.anim_small_big, R.anim.anim_big_small);
                        }
                    });
                }

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
                    if(mCurrentPage == 0){
                        DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                    }
                }

                public void onFinish(){
                    super.onFinish();
                    if(mCurrentPage == 0 || mCurrentPage == 1){
                        DialogUtil.getInstance().cancelProgressDialog();
                    }
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
                                payRecordDataList = checkAppendBeaconMsg(payRecordDataList);
                                mPayRecordAdapter.refresh(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                }
                            } else {
                                payRecordDataList = checkAppendBeaconMsg(payRecordDataList);
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
                    if (mCurrentPage == 0) {
                        ArrayList<PayRecordDataVo> payRecordDataList = new ArrayList<PayRecordDataVo>();
                        payRecordDataList = checkAppendBeaconMsg(payRecordDataList);
                        mPayRecordAdapter.refresh(payRecordDataList);
                    }
                    AsyncHttpClientUtil.onFailure(PayConfirmActivity.this,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<PayRecordDataVo> checkAppendBeaconMsg(ArrayList<PayRecordDataVo> payRecordDataList){
        if(payRecordDataList.size() < 10 ){
            if(mCurrentPage != 0 && mPayRecordAdapter.isAppendBeaconMsg()){
                return payRecordDataList;
            }
            ArrayList<YunBaMsgVo> msgs = BeaconMsgDBUtil.getInstance().queryBeaconMsg();
           if(msgs != null && msgs.size() > 0){
               for (YunBaMsgVo msg : msgs){
                   PayRecordDataVo itemVo = new PayRecordDataVo();
                   itemVo.setYunBaMsgVo(msg);
                   payRecordDataList.add(itemVo);
               }
           }
        }
        return payRecordDataList;
    }
}
