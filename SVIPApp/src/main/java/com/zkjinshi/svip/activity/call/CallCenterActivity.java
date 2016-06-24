package com.zkjinshi.svip.activity.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ServiceTagAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.AssetUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.ServiceTagDataFirstVo;
import com.zkjinshi.svip.vo.ServiceTagDataSecondVo;
import com.zkjinshi.svip.vo.ServiceTagTopVo;
import com.zkjinshi.svip.vo.ServiceTagVo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class CallCenterActivity extends BaseActivity {

    private final static String TAG = CallCenterActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn,menuIBtn;
    private TextView titleTv;

    private ListView listview;
    private ServiceTagAdapter serviceTagAdapter;
    private ArrayList<ServiceTagTopVo> mList;

    private String locid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_center);
        mContext = this;

        initView();
        initData();
        initListener();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.FLAG_SELECT_AREA:
                    int index = data.getIntExtra("index",0);
                    Collections.swap(mList,0,index);
                    serviceTagAdapter.refresh(mList);
                    break;
            }
        }
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);

        menuIBtn = (ImageButton)findViewById(R.id.btn_menu);
        listview = (ListView) findViewById(R.id.listview);
    }

    private void initData() {
        locid = getIntent().getStringExtra("locid");

        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("呼叫中心");

        menuIBtn.setImageResource(R.drawable.btn_menu_selector);
        menuIBtn.setVisibility(View.VISIBLE);
   
        serviceTagAdapter = new ServiceTagAdapter(mContext,new ArrayList<ServiceTagTopVo>());
        listview.setAdapter(serviceTagAdapter);

        getServiceTag();
    }



    private void initListener() {
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        menuIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CallOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    public void test(){
        try {
            String response = AssetUtil.getContent(this,"servicetag.txt");
            ServiceTagVo serviceTagVo = new Gson().fromJson(response,ServiceTagVo.class);
            if (serviceTagVo == null){
                return;
            }
            if(serviceTagVo.getRes() == 0){
                if(!serviceTagVo.getData().isEmpty()){
                    mList = serviceTagVo.getData();
                    serviceTagAdapter.refresh(mList);
                }else{
                    Toast.makeText(mContext,"暂无数据",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext,serviceTagVo.getResDesc(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getServiceTag(){
//        if(true){
//            test();
//            return;
//        }
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String url = ProtocolUtil.servicetag(locid);
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        ServiceTagVo serviceTagVo = new Gson().fromJson(response,ServiceTagVo.class);
                        if (serviceTagVo == null){
                            return;
                        }
                        if(serviceTagVo.getRes() == 0){
                            if(!serviceTagVo.getData().isEmpty()){
                                mList = serviceTagVo.getData();
                                serviceTagAdapter.refresh(mList);
                            }else{
                                Toast.makeText(mContext,"暂无数据",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(mContext,serviceTagVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
