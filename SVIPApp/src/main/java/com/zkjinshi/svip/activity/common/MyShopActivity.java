package com.zkjinshi.svip.activity.common;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.activity.facepay.PayRecordActivity;
import com.zkjinshi.svip.adapter.MyShopAdapter;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.GetMyShopVo;
import com.zkjinshi.svip.vo.MyShopVo;


import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/3/8.
 */
public class MyShopActivity extends BaseActivity {

    private Context mContext;
    private ImageButton backBtn;
    private TextView titleTv,emptyTv;
    private RefreshListView mRefreshListView;
    private MyShopAdapter mMyShopAdapter = null;
    private int    mCurrentPage = 0;//记录当前查询页
    private int mPageSize = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mRefreshListView = (RefreshListView)findViewById(R.id.refresh_list_view);
        emptyTv = (TextView)findViewById(R.id.empty_tv);
    }

    private void initData() {
        ArrayList<MyShopVo> dataList = new ArrayList<MyShopVo>();
        mMyShopAdapter = new MyShopAdapter(dataList, MyShopActivity.this);
        mRefreshListView.setmPageSize(mPageSize);
        mRefreshListView.setAdapter(mMyShopAdapter);
        mRefreshListView.setEmptyView(emptyTv);
        titleTv.setText("我的会员卡");
        emptyTv.setVisibility(View.GONE);
        loadRecord();
    }

    public void onResume(){
        super.onResume();

    }

    private void initListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 0;
                loadRecord();
            }

            @Override
            public void onLoadingMore() {
                mCurrentPage = 0;
                loadRecord();
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realindex = position -1;
                MyShopVo itemData = (MyShopVo)mMyShopAdapter.getItem(realindex);
                Intent intent = new Intent(mContext,PayRecordActivity.class);
                intent.putExtra("status","2");
                intent.putExtra("shopid",itemData.getShopid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }




    private void loadRecord(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.shopBelong(mCurrentPage,mPageSize);
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
                        GetMyShopVo getMyShopVo = new Gson().fromJson(response,GetMyShopVo.class);
                        if(getMyShopVo == null){
                            emptyTv.setVisibility(View.VISIBLE);
                            return;
                        }
                        if(getMyShopVo.getRes() == 0){
                            ArrayList<MyShopVo> dataList = getMyShopVo.getData();
                            if (mCurrentPage == 0) {
                                mMyShopAdapter.refresh(dataList);
                                if(!dataList.isEmpty()){
                                    mCurrentPage++;
                                }else{
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mMyShopAdapter.loadMore(dataList);
                                if(!dataList.isEmpty()){
                                    mCurrentPage++;
                                }
                            }
                        }else{
                            Toast.makeText(mContext, getMyShopVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        emptyTv.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(MyShopActivity.this,statusCode);
                    emptyTv.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
