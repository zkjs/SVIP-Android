package com.zkjinshi.svip.activity.common;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.Uri;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;

import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.pyxis.bluetooth.IBeaconContext;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.activity.call.CallCenterActivity;
import com.zkjinshi.svip.activity.call.CallSelectShopActivity;
import com.zkjinshi.svip.activity.facepay.PayConfirmActivity;

import com.zkjinshi.svip.activity.facepay.PayRecordActivity;
import com.zkjinshi.svip.base.BaseFragmentActivity;
import com.zkjinshi.svip.blueTooth.BlueToothManager;

import com.zkjinshi.svip.fragment.ShopFragment;
import com.zkjinshi.svip.manager.BleLogManager;

import com.zkjinshi.svip.map.LocationManager;


import com.zkjinshi.svip.receiver.ScreenObserverReceiver;
import com.zkjinshi.svip.sqlite.BeaconMsgDBUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;

import com.zkjinshi.svip.utils.ProtocolUtil;


import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.blueware.agent.android.BlueWare;
import com.zkjinshi.svip.utils.qclCopy.BlurBehind;
import com.zkjinshi.svip.utils.qclCopy.OnBlurCompleteListener;

import com.zkjinshi.svip.view.MultiDirectionSlidingDrawer;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import java.util.ArrayList;

public class MainActivity extends BaseFragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static boolean showMsgAnimation = false;

    private SimpleDraweeView msgIv,avatarCiv,shopLogoSdv;
    private TextView activateTv;
    private MultiDirectionSlidingDrawer mDrawer;


    private RelativeLayout rootRlt;
    private UpdateLogoReceiver updateLogoReceiver;

    private ScreenObserverReceiver screenObserverReceiver;
    private Context mContext;
    public Animation fadeAnimation = null;
    private ShowMessageReceiver mShowMessageReceiver;

    public int clickCount = 0; //单击计数

    private ShopFragment shopFragment;
    public static int BEACON_MSG_DELAY_TIME = 500;
    public static final int CLEAR_CLICK_COUNT_ORDER = 1;
    public static final int SHOW_BEACON_MSG_ORDER = 2;
    public  Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == SHOW_BEACON_MSG_ORDER){
                final YunBaMsgVo yunBaMsgVo = BeaconMsgDBUtil.getInstance().popUnReadBeaconMsg();
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
                }else if(SplashActivity.beaconMsg != null){
                    BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
                        @Override
                        public void onBlurComplete() {
                            Intent bIntent = new Intent(mContext,BeaconMsgActivity.class);
                            bIntent.putExtra("data",SplashActivity.beaconMsg);
                            bIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(bIntent);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                        }
                    });
                }
            }else  if(msg.what == CLEAR_CLICK_COUNT_ORDER){
                clickCount = 0;
            }
        }
    };



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

    @Override
    public void onContentChanged()
    {
        super.onContentChanged();
        mDrawer = (MultiDirectionSlidingDrawer) findViewById( R.id.drawer );
    }

    protected void onResume() {
        super.onResume();
        clickCount = 0;
        String userPhotoUrl = CacheUtil.getInstance().getUserPhotoUrl();
        avatarCiv.setImageURI(Uri.parse(userPhotoUrl));


        if(showMsgAnimation){
            showPayMsgTips();
        }else{
            hidePayMsgTips();
        }
        BleLogManager.getInstance().uploadBleStatLog(this);


        myHandler.sendEmptyMessageDelayed(SHOW_BEACON_MSG_ORDER,BEACON_MSG_DELAY_TIME);

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
        BEACON_MSG_DELAY_TIME = 500;

    }

    public void onBackPressed(){
        if(shopFragment.isVisiable){
            shopFragment.hideAction();
        }else{
            super.onBackPressed();
        }
    }

    private void initView() {
        activateTv = (TextView)findViewById(R.id.activate_tips_tv);
        msgIv = (SimpleDraweeView)findViewById(R.id.msgIv);
        avatarCiv =  (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        shopLogoSdv = (SimpleDraweeView)findViewById(R.id.shop_logo);

        rootRlt = (RelativeLayout)findViewById(R.id.root_rlt);
        FragmentManager manager = getSupportFragmentManager();
        shopFragment = new ShopFragment();
        manager.beginTransaction().add(R.id.contentView, shopFragment).commit();

    }

    private void initData() {
        MainController.getInstance().init(this);
        MainController.getInstance().checkAppVersion();

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
        }
        LocationManager.getInstance().startLocation();



    }


    private void initListener() {

        //头像
        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mDrawer.animateClose();
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



        msgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayConfirmActivity.class);
                intent.putExtra("status","0");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                hidePayMsgTips();
                MainActivity.showMsgAnimation = false;               

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
                if(!shopFragment.isVisiable && !mDrawer.isOpened()){
                    shopFragment.show(rootRlt);
                }
            }
        });

        findViewById(R.id.wallet_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PayRecordActivity.class);
                intent.putExtra("status","2");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mDrawer.animateClose();
            }
        });

        findViewById(R.id.hujiao_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(null != bluetoothAdapter && bluetoothAdapter.isEnabled()){
                        //IBeaconVo iBeaconVo = IBeaconContext.getInstance().getLastIBeaconVo();
                        Intent intent = new Intent(mContext,CallCenterActivity.class);
                        intent.putExtra("locid","");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else{
                        showBlueToothMsg();
                    }
                }else{
                    gotoSelectShop();
                }


                mDrawer.animateClose();
            }
        });


    }

    private void gotoSelectShop(){
        Intent intent = new Intent(mContext,CallSelectShopActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public boolean onTouchEvent(MotionEvent event) {
       return false;
    }


    private void showBlueToothMsg(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setTitle("无法获取您所在的区域");
        customBuilder.setMessage("请点击设置，打开蓝牙以便我们更好的为您提供服务");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        customBuilder.setPositiveButton("好", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoSelectShop();
            }
        });
        customBuilder.create().show();
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
