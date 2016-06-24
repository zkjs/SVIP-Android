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

import com.zkjinshi.svip.adapter.CallOrderAdapter;

import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.utils.AssetUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.ServiceTaskDataVo;
import com.zkjinshi.svip.vo.ServiceTaskVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class CallOrderActivity extends BaseActivity {

    private final static String TAG = CallOrderActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv;

    private RefreshListView mRefreshListView;
    private CallOrderAdapter callOrderAdapter = null;
    private int    mCurrentPage;//记录当前查询页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_order);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mRefreshListView = (RefreshListView)findViewById(R.id.slv_call_order);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("呼叫列表");

        ArrayList<ServiceTaskDataVo> dataList = new ArrayList<ServiceTaskDataVo>();
        callOrderAdapter = new CallOrderAdapter(dataList, CallOrderActivity.this);
        mRefreshListView.setAdapter(callOrderAdapter);
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

    public void test(){
        try {
            String response = AssetUtil.getContent(this,"calltask.txt");
            ServiceTaskVo serviceTaskVo = new Gson().fromJson(response,ServiceTaskVo.class);
            if(serviceTaskVo == null){
                return;
            }
            if(serviceTaskVo.getRes() == 0){
                ArrayList<ServiceTaskDataVo> dataList = serviceTaskVo.getData();
                if (mCurrentPage == 0) {
                    callOrderAdapter.refresh(dataList);
                    if(!dataList.isEmpty()){
                        mCurrentPage++;
                    }
                } else {
                    callOrderAdapter.loadMore(dataList);
                    if(!dataList.isEmpty()){
                        mCurrentPage++;
                    }

                }

            }else{
                Toast.makeText(mContext, serviceTaskVo.getResDesc(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecord(int page){
//        if(true){
//            test();
//            return;
//        }
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.serviceTaskList(page);
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
                        ServiceTaskVo serviceTaskVo = new Gson().fromJson(response,ServiceTaskVo.class);
                        if(serviceTaskVo == null){
                            return;
                        }
                        if(serviceTaskVo.getRes() == 0){
                            ArrayList<ServiceTaskDataVo> dataList = serviceTaskVo.getData();
                            if (mCurrentPage == 0) {
                                callOrderAdapter.refresh(dataList);
                                if(!dataList.isEmpty()){
                                    mCurrentPage++;
                                }
                            } else {
                                callOrderAdapter.loadMore(dataList);
                                if(!dataList.isEmpty()){
                                    mCurrentPage++;
                                }

                            }

                        }else{
                            Toast.makeText(mContext, serviceTaskVo.getResDesc(),Toast.LENGTH_SHORT).show();
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
