package com.zkjinshi.svip.activity.call;

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
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.CallSelectAreaAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.response.GetZoneListResponse;
import com.zkjinshi.svip.utils.AssetUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.PayRecordVo;
import com.zkjinshi.svip.vo.ServiceTagVo;
import com.zkjinshi.svip.vo.ZoneVo;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class CallSelectAreaActivity extends BaseActivity {

    private final static String TAG = CallSelectAreaActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv;

    private RefreshListView mRefreshListView;
    private CallSelectAreaAdapter callSelectAreaAdapter = null;
    private int    mCurrentPage;//记录当前查询页
    private String shopid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_select_area);
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
        titleTv.setText("选择区域");

        shopid = getIntent().getStringExtra("shopid");

        ArrayList<ZoneVo> dataList = new ArrayList<ZoneVo>();
        callSelectAreaAdapter = new CallSelectAreaAdapter(dataList,this);
        mRefreshListView.setAdapter(callSelectAreaAdapter);
    }

    private void initListener() {
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 0;
                getZoneList();
            }

            @Override
            public void onLoadingMore() {
                getZoneList();
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realPostion = position - 1;
                ZoneVo zoneVo = (ZoneVo) callSelectAreaAdapter.getItem(realPostion);
                Intent intent = new Intent(mContext,CallCenterActivity.class);
                intent.putExtra("locid",zoneVo.getLocid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        getZoneList();
    }

    public void test(){
        try {
            String response = AssetUtil.getContent(this,"getzonelist.txt");
            GetZoneListResponse getZoneListResponse = new Gson().fromJson(response,GetZoneListResponse.class);
            if (getZoneListResponse == null){
                return;
            }
            if(getZoneListResponse.getRes() == 0){

                ArrayList<ZoneVo> zoneVoArrayList = getZoneListResponse.getData();
                if (mCurrentPage == 0) {
                    callSelectAreaAdapter.refresh(zoneVoArrayList);
                    if(!zoneVoArrayList.isEmpty()){
                        mCurrentPage++;
                    }
                } else {
                    callSelectAreaAdapter.loadMore(zoneVoArrayList);
                    if(!zoneVoArrayList.isEmpty()){
                        mCurrentPage++;
                    }

                }

            }else{
                Toast.makeText(mContext,getZoneListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getZoneList(){
//        if(true){
//            test();
//            return;
//        }

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getZoneList(shopid,mCurrentPage);
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
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
                        GetZoneListResponse getZoneListResponse = new Gson().fromJson(response,GetZoneListResponse.class);
                        if (getZoneListResponse == null){
                            return;
                        }
                        if(getZoneListResponse.getRes() == 0){

                            ArrayList<ZoneVo> zoneVoArrayList = getZoneListResponse.getData();
                            if (mCurrentPage == 0) {
                                callSelectAreaAdapter.refresh(zoneVoArrayList);
                                if(!zoneVoArrayList.isEmpty()){
                                    mCurrentPage++;
                                }
                            } else {
                                callSelectAreaAdapter.loadMore(zoneVoArrayList);
                                if(!zoneVoArrayList.isEmpty()){
                                    mCurrentPage++;
                                }

                            }

                        }else{
                            Toast.makeText(mContext,getZoneListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
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
            e.printStackTrace();
        }
    }

}