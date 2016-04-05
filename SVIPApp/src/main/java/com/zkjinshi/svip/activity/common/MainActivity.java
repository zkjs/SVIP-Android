package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.facepay.PayConfirmActivity;
import com.zkjinshi.svip.activity.facepay.PayRecordActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.blueTooth.BlueToothManager;

import com.zkjinshi.svip.map.LocationManager;

import com.zkjinshi.svip.receiver.ScreenObserverReceiver;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PayUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;


import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.blueware.agent.android.BlueWare;
import com.zkjinshi.svip.view.BeaconMsgDialog;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import java.util.ArrayList;

public class MainActivity extends BaseActivity{

    public static final String TAG = MainActivity.class.getSimpleName();

    public static boolean showMsgAnimation = false;

    private SimpleDraweeView msgIv,avatarCiv,shopLogoSdv,walletSdv;
    private TextView accountTv,usernameTv;
    private RelativeLayout paopaoRlt;
    private CheckBox switchCbx;

    private ScreenObserverReceiver screenObserverReceiver;
    private Context mContext;
    public Animation fadeAnimation = null;
    private ShowMessageReceiver mShowMessageReceiver;

    public int clickCount = 0; //单击计数



    private class ShowMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            LogUtil.getInstance().info(LogLevel.DEBUG,TAG+"收到显示信息广播");
            showPayMsgTips();
        }
    }

    private ShowIBeaconPushMsgReceiver mShowIBeaconPushMsgReceiver;
    private class ShowIBeaconPushMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            try {
                YunBaMsgVo yunBaMsgVo = (YunBaMsgVo) intent.getSerializableExtra("data");
//                if(yunBaMsgVo != null){
//                    BeaconMsgDialog beaconMsgDialog = new BeaconMsgDialog(MainActivity.this);
//                    beaconMsgDialog.show();
//                    beaconMsgDialog.setContentText(yunBaMsgVo.getTitle(),yunBaMsgVo.getContent());
//                }
                Intent bIntent = new Intent(mContext,BeaconMsgActivity.class);
                bIntent.putExtra("data",yunBaMsgVo);
                startActivity(bIntent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //one apm初始化操作(事件统计)
        BlueWare.withApplicationToken("48253A521296A801A544BD1E163B1F6D55").start(this.getApplication());
        mContext = this;

        initView();
        initData();
        initListener();
    }

    protected void onResume() {
        super.onResume();
        clickCount = 0;
        String userPhotoUrl = CacheUtil.getInstance().getUserPhotoUrl();
        avatarCiv.setImageURI(Uri.parse(userPhotoUrl));
        usernameTv.setText(CacheUtil.getInstance().getUserName());

        paopaoRlt.setVisibility(View.GONE);

        if(showMsgAnimation){
            showPayMsgTips();
        }else{
            hidePayMsgTips();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(null != screenObserverReceiver){
            unregisterReceiver(screenObserverReceiver);
        }
        if(null != mShowMessageReceiver){
            unregisterReceiver(mShowMessageReceiver);
        }
        if(null != mShowIBeaconPushMsgReceiver){
            unregisterReceiver(mShowIBeaconPushMsgReceiver);
        }
    }

    private void initView() {
        msgIv = (SimpleDraweeView)findViewById(R.id.msgIv);
        avatarCiv =  (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        accountTv = (TextView)findViewById(R.id.account_tv);
        usernameTv = (TextView)findViewById(R.id.username_tv);
        shopLogoSdv = (SimpleDraweeView)findViewById(R.id.shop_logo);
        walletSdv = (SimpleDraweeView)findViewById(R.id.wallet_sdv);
        paopaoRlt = (RelativeLayout)findViewById(R.id.paopao_rlt);
        switchCbx = (CheckBox)findViewById(R.id.switch_cbx);
    }

    private void initData() {

        BlueToothManager.getInstance().openBluetooth();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenObserverReceiver = new ScreenObserverReceiver();
        registerReceiver(screenObserverReceiver,intentFilter);

        accountTv.setText("0.00");
        usernameTv.setText("");
        //walletSdv.setVisibility(View.GONE);
        //msgIv.setVisibility(View.GONE);

        mShowMessageReceiver = new ShowMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SHOW_CONTACT_RECEIVER_ACTION);
        registerReceiver(mShowMessageReceiver, filter);

        mShowIBeaconPushMsgReceiver = new ShowIBeaconPushMsgReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(com.zkjinshi.svip.utils.Constants.SHOW_IBEACON_PUSH_MSG_RECEIVER_ACTION);
        registerReceiver(mShowIBeaconPushMsgReceiver, filter2);

        switchCbx.setChecked(true);
        BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
        LocationManager.getInstance().startLocation();
    }


    private void initListener() {
        paopaoRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayRecordActivity.class);
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
                hidePayMsgTips();
                MainActivity.showMsgAnimation = false;
            }
        });

        walletSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paopaoRlt.getVisibility() == View.GONE){
                    getAccount();
                    paopaoRlt.setVisibility(View.VISIBLE);
                }else{
                    paopaoRlt.setVisibility(View.GONE);
                }
            }
        });

        switchCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOpen) {
                if(isOpen){
                    BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
                }else{
                    BlueToothManager.getInstance().stopIBeaconService();
                }
            }
        });

        findViewById(R.id.finish_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCount++;
                if(clickCount == 6){
                    CacheUtil.getInstance().setLogin(false);
                    BlueToothManager.getInstance().stopIBeaconService();
                    LocationManager.getInstance().stopLocation();
                    BaseApplication.getInst().clear();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    clickCount = 0;
                }else{
                     handler.removeMessages(1);
                     handler.sendEmptyMessageDelayed(1,1000);
                }

            }
        });


    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1){
                clickCount = 0;
            }
        }
    };

    //呼吸灯闪
    public void showPayMsgTips(){
        fadeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade);
        msgIv.startAnimation(fadeAnimation);

    }

    //呼吸灯不闪
    public void hidePayMsgTips(){
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
                    //DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                   // DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            double balance = response.getDouble("balance");
                            accountTv.setText(PayUtil.changeMoney(balance));
                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    //Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(MainActivity.this,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
