package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.facepay.PayConfirmActivity;
import com.zkjinshi.svip.blueTooth.BlueToothManager;

import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.blueware.agent.android.BlueWare;

public class MainActivity extends Activity {

    private SimpleDraweeView msgIv,avatarCiv;
    private TextView accountTv,usernameTv;


    private Context mContext;
    private ProgressDialog mProgressDialog;

    public Animation fadeAnimation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BlueWare.withApplicationToken("48253A521296A801A544BD1E163B1F6D55").start(this.getApplication());
        mContext = this;

        initView();
        initData();
        initListener();
    }

    protected void onResume() {
        super.onResume();
        if(CacheUtil.getInstance().isLogin()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !BlueToothManager.getInstance().isIBeaconServiceRunning()) {
                BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
            }
            if(!LocationManager.getInstance().isRuning()){
                LocationManager.getInstance().startLocation();
            }
        }
        getAccount();
        String userPhotoUrl = CacheUtil.getInstance().getUserPhotoUrl();
        avatarCiv.setImageURI(Uri.parse(userPhotoUrl));
        usernameTv.setText(CacheUtil.getInstance().getUserName());
    }

    private void initView() {
        msgIv = (SimpleDraweeView)findViewById(R.id.msgIv);
        avatarCiv =  (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        accountTv = (TextView)findViewById(R.id.account_tv);
        usernameTv = (TextView)findViewById(R.id.username_tv);

    }

    private void initData() {
        accountTv.setText("¥ --");
        usernameTv.setText("");
    }


    private void initListener() {
        accountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayConfirmActivity.class);
                intent.putExtra("status","2");
                startActivity(intent);
            }
        });

        msgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayConfirmActivity.class);
                intent.putExtra("status","0");
                startActivity(intent);
            }
        });
    }

    //呼吸灯闪
    public void showPayMsgTips(){
        fadeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade);
        msgIv.startAnimation(fadeAnimation);

    }

    //呼吸灯不闪
    public void hidePrivilegeTips(){
        if(fadeAnimation != null){
            fadeAnimation.cancel();
            fadeAnimation = null;
        }
        msgIv.clearAnimation();
    }

    private void getAccount(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getAccount();
            client.get(mContext, url, stringEntity, "application/json", new JsonHttpResponseHandler(){

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
                            double balance = response.getDouble("balance");
                            accountTv.setText("¥ "+balance);
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
