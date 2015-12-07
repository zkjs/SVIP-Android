package com.zkjinshi.svip.activity.common;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocA2M;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.listener.MessageListener;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.request.pushad.MsgPushLocA2MReqTool;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.view.GooeyMenu;
import com.zkjinshi.svip.view.ListenerDialog;


public class MainActivity extends FragmentActivity implements IBeaconObserver, LocationManager.LocationChangeListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public static final int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;

    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;


    public static double geoLat = 100;
    public static double geoLng;

    private ListenerDialog listenerDialog;

    public enum MainTextStatus {
        DEFAULT_NULL,
        NO_ORDER_NOT_IN,
        NO_ORDER_IN,
        BOOKING_NOT_IN,
        BOOKING_IN,
        SURE_IN_HAVE_NOLOGIN,
        SURE_IN_NOT_NOLOGIN,
        SURE_NOT_IN,
        CHECKIN_IN,
        CHECKIN_NOT_IN,
        ORDER_FINISH
    }

    private MainTextStatus mainTextStatus = MainTextStatus.DEFAULT_NULL;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOTIFY_UPDATE_VIEW:
                   // setBadgeNum();
                    break;
                case NOTIFY_UPDATE_MAIN_TEXT:
                   // changeMainText();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svipApplication = (SVIPApplication)getApplication();
        initView();
        initData();
        initListeners();
        initIBeaconList();
        IBeaconSubject.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
    }

    @Override
    protected void onResume(){
        super.onResume();
        LocationManager.getInstance().registerLocation(this);
        LocationManager.getInstance().setLocationChangeListener(this);
    }

    private void initView() {
        listenerDialog = new ListenerDialog(this);
    }

    private void initData(){
        initDBName();
        MainController.getInstance().init(this);
        MainController.getInstance().initShop();
        MainController.getInstance().checktActivate();
        MessageListener  messageListener = new MessageListener();
        initService(messageListener);
    }

    private void initListeners() {

    }

    /**
     * 初始化数据库名称
     */
    private void initDBName(){
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        }
    }

    /**
     * 初始化socket
     */
    private void initService(MessageListener messageListener) {
        WebSocketManager.getInstance().initService(this).setMessageListener(messageListener);
    }

    /**
     * 初始化beacon列表
     */
    private void initIBeaconList(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 大于等于android 4.4
            IBeaconController.getInstance().init(this);
        }
    }


    @Override
    public void intoRegion(RegionVo regionVo) {
        LogUtil.getInstance().info(LogLevel.DEBUG,"--欢迎惠顾酒店-----");
        LogUtil.getInstance().info(LogLevel.DEBUG,"inTime:"+regionVo.getInTime());
        LogUtil.getInstance().info(LogLevel.DEBUG, "beacon info:" + regionVo.getiBeacon().toString());
        LogUtil.getInstance().info(LogLevel.DEBUG, "---------------------");

        reginAdPush(regionVo);
        addRegionVo(regionVo);
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
    }

    @Override
    public void outRegin(RegionVo regionVo) {
        LogUtil.getInstance().info(LogLevel.DEBUG,"--欢迎下次光临-----");
        LogUtil.getInstance().info(LogLevel.DEBUG,"inTime:"+regionVo.getInTime());
        LogUtil.getInstance().info(LogLevel.DEBUG,"outTime:"+regionVo.getOutTime());
        LogUtil.getInstance().info(LogLevel.DEBUG, "standTime:"+regionVo.getStandTime());
        LogUtil.getInstance().info(LogLevel.DEBUG, "beacon info:" + regionVo.getiBeacon().toString());
        LogUtil.getInstance().info(LogLevel.DEBUG, "---------------------");

        removeRegionVo(regionVo);
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
    }

    //添加蓝牙设备
    private void addRegionVo(RegionVo regionVo){

        for(RegionVo item : svipApplication.mRegionList){
            if(item.getiBeacon().getBeaconKey().equals(regionVo.getiBeacon().getBeaconKey())){
                svipApplication.mRegionList.remove(item);
                break;
            }
        }
        svipApplication.mRegionList.add(regionVo);
    }

    //移除蓝牙设备
    private void removeRegionVo(RegionVo regionVo){
        for(RegionVo item : svipApplication.mRegionList){
            if(item.getiBeacon().getBeaconKey().equals(regionVo.getiBeacon().getBeaconKey())){
                svipApplication.mRegionList.remove(item);
                break;
            }
        }
    }

    /**
     * 区域广告通知
     * @param regionVo
     */
    private void reginAdPush(RegionVo regionVo){
        MsgPushLocA2M msgPushLocA2M = MsgPushLocA2MReqTool.buildMsgPushLocA2M(regionVo);
        String msgPushStr = new Gson().toJson(msgPushLocA2M,MsgPushLocA2M.class);
        WebSocketManager.getInstance().sendMessage(msgPushStr);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取位置信息
        geoLat = aMapLocation.getLatitude();//纬度
        geoLng = aMapLocation.getLongitude();//经度
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图返回位置信息：("+geoLat+","+geoLng+")");
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);

    }
}
