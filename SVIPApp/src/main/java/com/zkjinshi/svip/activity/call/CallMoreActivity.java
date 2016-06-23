package com.zkjinshi.svip.activity.call;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.PayRecordVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class CallMoreActivity extends BaseActivity {

    private final static String TAG = CallMoreActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv;

    private RefreshListView mRefreshListView;
    private CallMoreAdapter callMoreAdapter = null;
    private int    mCurrentPage;//记录当前查询页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_more);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mRefreshListView = (RefreshListView)findViewById(R.id.slv_call_more);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("呼叫中心");

        ArrayList<PayRecordDataVo> dataList = new ArrayList<PayRecordDataVo>();
        callMoreAdapter = new CallMoreAdapter(dataList, CallMoreActivity.this);
        mRefreshListView.setAdapter(callMoreAdapter);
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
                //int realPostion = position - 1;
            }
        });

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void onResume(){
        super.onResume();
        mCurrentPage = 0;
        loadRecord(mCurrentPage);
    }

    private void loadRecord(int page){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getPayList("0",page);
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
                                callMoreAdapter.refresh(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                }
                            } else {
                                callMoreAdapter.loadMore(payRecordDataList);
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
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
