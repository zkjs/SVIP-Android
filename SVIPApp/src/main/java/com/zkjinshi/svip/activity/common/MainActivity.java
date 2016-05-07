package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.pyxis.bluetooth.IBeaconObserver;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.facepay.PayConfirmActivity;
import com.zkjinshi.svip.activity.facepay.PayRecordActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.base.BaseFragmentActivity;
import com.zkjinshi.svip.blueTooth.BlueToothManager;

import com.zkjinshi.svip.fragment.ShopFragment;
import com.zkjinshi.svip.manager.BleLogManager;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.map.LocationManager;

import com.zkjinshi.svip.net.SvipHttpClient;
import com.zkjinshi.svip.receiver.ScreenObserverReceiver;
import com.zkjinshi.svip.sqlite.BeaconMsgDBUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PayUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;


import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.blueware.agent.android.BlueWare;
import com.zkjinshi.svip.utils.qclCopy.BlurBehind;
import com.zkjinshi.svip.utils.qclCopy.OnBlurCompleteListener;
import com.zkjinshi.svip.view.BeaconMsgDialog;
import com.zkjinshi.svip.view.FlingCallback;
import com.zkjinshi.svip.view.Gesture;
import com.zkjinshi.svip.vo.AreaVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import java.util.ArrayList;

public class MainActivity extends BaseFragmentActivity implements IBeaconObserver {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static boolean showMsgAnimation = false;

    private SimpleDraweeView msgIv,avatarCiv,shopLogoSdv,walletSdv;
    private TextView accountTv,usernameTv,activateTv,areaTv;
    private RelativeLayout paopaoRlt;

    private RelativeLayout rootRlt;
    private UpdateLogoReceiver updateLogoReceiver;

    private ScreenObserverReceiver screenObserverReceiver;
    private Context mContext;
    public Animation fadeAnimation = null;
    private ShowMessageReceiver mShowMessageReceiver;

    public int clickCount = 0; //单击计数

    private ShopFragment shopFragment;
    public static final int CLEAR_CLICK_COUNT_ORDER = 1;
    public static final int SHOW_BEACON_MSG_ORDER = 2;
    public static final int SHOW_AREA_DESC = 3;
    public  Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == SHOW_BEACON_MSG_ORDER){
                SVIPApplication svipApp = (SVIPApplication)getApplication();
                final YunBaMsgVo yunBaMsgVo = svipApp.popBeaconMsg();
                if(yunBaMsgVo != null){
                    BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                        @Override
                        public void onBlurComplete() {
                            Intent bIntent = new Intent(mContext,BeaconMsgActivity.class);
                            bIntent.putExtra("data",yunBaMsgVo);
                            bIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(bIntent);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                        }
                    });
                }
            }else  if(msg.what == CLEAR_CLICK_COUNT_ORDER){
                clickCount = 0;
            }else  if(msg.what == SHOW_AREA_DESC){
                AreaVo areaVo = BlueToothManager.getInstance().getNearestArea(MainActivity.this);
                if(areaVo != null){
                    areaTv.setText(areaVo.getLocdesc());
                }else{
                    areaTv.setText("");
                }

            }
        }
    };

    @Override
    public void intoRegion(IBeaconVo iBeaconVo) {
        myHandler.sendEmptyMessage(SHOW_AREA_DESC);
    }

    @Override
    public void outRegin(IBeaconVo iBeaconVo) {
        myHandler.sendEmptyMessage(SHOW_AREA_DESC);
    }

    @Override
    public void sacnBeacon(IBeaconVo iBeaconVo) {

    }

    @Override
    public void exitRegion(Region region) {

    }

    @Override
    public void postCollectBeacons() {

    }


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
                final YunBaMsgVo yunBaMsgVo = (YunBaMsgVo) intent.getSerializableExtra("data");
                SVIPApplication svipApp = (SVIPApplication)getApplication();
                svipApp.pushBeaconMsg(yunBaMsgVo);
                myHandler.removeMessages(SHOW_BEACON_MSG_ORDER);
                myHandler.sendEmptyMessageDelayed(SHOW_BEACON_MSG_ORDER,500);

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
        BleLogManager.getInstance().uploadBleStatLog(this);

        SVIPApplication svipApp = (SVIPApplication)getApplication();
        final YunBaMsgVo yunBaMsgVo = svipApp.popBeaconMsg();
        if(yunBaMsgVo != null){
            BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                @Override
                public void onBlurComplete() {
                    Intent bIntent = new Intent(MainActivity.this,BeaconMsgActivity.class);
                    bIntent.putExtra("data",yunBaMsgVo);
                    bIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(bIntent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
            });
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(null != updateLogoReceiver){
            unregisterReceiver(updateLogoReceiver);
        }
        if(null != screenObserverReceiver){
            unregisterReceiver(screenObserverReceiver);
        }
        if(null != mShowMessageReceiver){
            unregisterReceiver(mShowMessageReceiver);
        }
        if(null != mShowIBeaconPushMsgReceiver){
            unregisterReceiver(mShowIBeaconPushMsgReceiver);
        }
        SVIPApplication svipApplication = (SVIPApplication)getApplication();
        svipApplication.clearBeaconMsg();
        BlueToothManager.getInstance().removeObserver(this);
    }

    public void onBackPressed(){
        if(shopFragment.isVisiable){
            shopFragment.hideAction();
        }else{
            super.onBackPressed();
        }
    }

    private void initView() {
        areaTv  = (TextView)findViewById(R.id.area_tv);
        activateTv = (TextView)findViewById(R.id.activate_tips_tv);
        msgIv = (SimpleDraweeView)findViewById(R.id.msgIv);
        avatarCiv =  (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        accountTv = (TextView)findViewById(R.id.account_tv);
        usernameTv = (TextView)findViewById(R.id.username_tv);
        shopLogoSdv = (SimpleDraweeView)findViewById(R.id.shop_logo);
        walletSdv = (SimpleDraweeView)findViewById(R.id.wallet_sdv);
        paopaoRlt = (RelativeLayout)findViewById(R.id.paopao_rlt);

        rootRlt = (RelativeLayout)findViewById(R.id.root_rlt);

        FragmentManager manager = getSupportFragmentManager();
        shopFragment = new ShopFragment();
        manager.beginTransaction().add(R.id.contentView, shopFragment).commit();

    }

    private void initData() {
        areaTv.setText("");
        //注册更新logo广播
        updateLogoReceiver = new UpdateLogoReceiver();
        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(Constants.UPDATE_LOGO_RECEIVER_ACTION);
        registerReceiver(updateLogoReceiver,updateIntentFilter);
        //设置默认商家logo
        if(CacheUtil.getInstance().isUpdateLogo()){
            Uri uri =  Uri.parse("res://com.zkjinshi.svip/"+R.mipmap.ic_shop_logo);
            shopLogoSdv.setImageURI(uri);
        }
        //打开蓝牙请求
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

        if(CacheUtil.getInstance().isServiceSwitch()){
            BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
            BlueToothManager.getInstance().addObserver(this);
        }
        LocationManager.getInstance().startLocation();

        //AreaVo areaVo = ((SVIPApplication)getApplication()).getAreaVo("931ddf8e-10e4-11e5-9493-1697f925ec7b851");

    }


    private void initListener() {

        //头像
        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //立即激活
        activateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo jimmyzhang
                DialogUtil.getInstance().showCustomToast(v.getContext(),"开始激活", Gravity.CENTER);
            }
        });

        paopaoRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayRecordActivity.class);
                intent.putExtra("status","2");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        msgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PayConfirmActivity.class);
//                intent.putExtra("status","0");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                hidePayMsgTips();
//                MainActivity.showMsgAnimation = false;
                showPopupWindow(view);

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



        findViewById(R.id.finish_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCount++;
                if(clickCount == 6){
                    AsyncHttpClientUtil.forceExit(mContext);
                    clickCount = 0;
                }else{
                     myHandler.removeMessages(CLEAR_CLICK_COUNT_ORDER);
                    myHandler.sendEmptyMessageDelayed(1,1000);
                }

            }
        });
        shopLogoSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!shopFragment.isVisiable){
                    shopFragment.show(rootRlt);
                }
            }
        });


    }

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate( R.layout.menu_pop_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_tishikuang));

        ColorDrawable color = new ColorDrawable(Color.parseColor("#8f101010"));
        popupWindow.setBackgroundDrawable(color);
        // 设置好参数之后再show
        int offsetX = DisplayUtil.dip2px(this,104);
        popupWindow.showAsDropDown(view,-offsetX,5);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        //消息
        contentView.findViewById(R.id.pay_confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayConfirmActivity.class);
                intent.putExtra("status","0");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                hidePayMsgTips();
                MainActivity.showMsgAnimation = false;
                popupWindow.dismiss();
            }
        });
        //视频直播
        contentView.findViewById(R.id.video_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PayConfirmActivity.class);
//                intent.putExtra("status","0");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                popupWindow.dismiss();
            }
        });

        //室内导航
        contentView.findViewById(R.id.map_direct_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PayConfirmActivity.class);
//                intent.putExtra("status","0");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                popupWindow.dismiss();
            }
        });

        //门锁开关
        contentView.findViewById(R.id.lock_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PayConfirmActivity.class);
//                intent.putExtra("status","0");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                popupWindow.dismiss();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
       return false;
    }





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

    /**
     * 动态更新商家logo
     */
    private void updateShopLogo(){
        String shopLogoUrl = CacheUtil.getInstance().getShopLogo();
        if(!TextUtils.isEmpty(shopLogoUrl)){
            Uri uri =  Uri.parse(shopLogoUrl);
            shopLogoSdv.setImageURI(uri);
        }
    }

    class UpdateLogoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent){
                String action =  intent.getAction();
                if(!TextUtils.isEmpty(action) && action.equals(Constants.UPDATE_LOGO_RECEIVER_ACTION)){
                    //更新商家logo
                    updateShopLogo();
                    //更新商家详情页面
                    if(null != shopFragment){
                        shopFragment.initData();
                    }
                }
            }
        }
    }


}
