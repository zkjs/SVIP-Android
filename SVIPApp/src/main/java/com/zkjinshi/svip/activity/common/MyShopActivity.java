package com.zkjinshi.svip.activity.common;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.adapter.MyShopAdapter;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

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
    private ListView mRefreshListView;
    private MyShopAdapter mMyShopAdapter = null;



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
        mRefreshListView = (ListView)findViewById(R.id.refresh_list_view);
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
    }




    private void loadRecord(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.shopBelong();
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
                        GetMyShopVo getMyShopVo = new Gson().fromJson(response,GetMyShopVo.class);
                        if(getMyShopVo == null){
                            return;
                        }
                        if(getMyShopVo.getRes() == 0){
                            ArrayList<MyShopVo> dataList = getMyShopVo.getData();
                            if(dataList.isEmpty()){
                                emptyTv.setVisibility(View.VISIBLE);
                            }else{
                                emptyTv.setVisibility(View.GONE);
                                mMyShopAdapter.refresh(dataList);
                            }
                        }else{
                            Toast.makeText(mContext, getMyShopVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
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
