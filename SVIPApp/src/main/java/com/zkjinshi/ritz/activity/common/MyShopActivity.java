package com.zkjinshi.ritz.activity.common;


import android.content.Context;
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
import com.zkjinshi.ritz.R;

import com.zkjinshi.ritz.adapter.MyShopAdapter;
import com.zkjinshi.ritz.base.BaseActivity;

import com.zkjinshi.ritz.listener.OnRefreshListener;
import com.zkjinshi.ritz.utils.AsyncHttpClientUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.utils.Constants;
import com.zkjinshi.ritz.utils.ProtocolUtil;

import com.zkjinshi.ritz.view.RefreshListView;
import com.zkjinshi.ritz.vo.GetMyShopVo;
import com.zkjinshi.ritz.vo.MyShopVo;


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
//        for(int i=0;i<10;i++){
//            MyShopVo myShopVo = new MyShopVo();
//            myShopVo.setShopid(""+i);
//            myShopVo.setShoplogo("test");
//            myShopVo.setShopname("温德姆至尊豪廷大酒店");
//            dataList.add(myShopVo);
//        }
        mMyShopAdapter = new MyShopAdapter(dataList, MyShopActivity.this);
        mRefreshListView.setAdapter(mMyShopAdapter);
        mRefreshListView.setEmptyView(emptyTv);
        titleTv.setText("我的商家");
        emptyTv.setVisibility(View.GONE);
    }

    public void onResume(){
        super.onResume();
        loadRecord();
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
                loadRecord();
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {

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
