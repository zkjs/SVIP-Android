package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.bean.LocPushBean;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.emchat.observer.EMessageListener;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.fragment.SetFragment;
import com.zkjinshi.svip.fragment.TabNavigationFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconEntity;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;

public class MainActivity extends FragmentActivity implements IBeaconObserver{

    public static final String TAG = MainActivity.class.getSimpleName();
    SVIPApplication svipApplication;
    public OrderLastResponse lastOrderInfo = null;

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
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IBeaconSubject.getInstance().removeObserver(this);
        EMessageListener.getInstance().unregisterEventListener();
//        if(listenerDialog != null){
//            listenerDialog.stopAnimation();
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SetFragment.KILL_MYSELT){
                logout();
                finish();
            }
        }

    }

    public void logout(){
        //环信接口退出
        EasemobIMHelper.getInstance().logout();
        //http接口推出
        String userID = CacheUtil.getInstance().getUserId();
        logoutHttp(userID);
        //熊推接口推出
        WebSocketManager.getInstance().logoutIM(VIPContext.getInstance().getContext());
        //修改登录状态
        CacheUtil.getInstance().setLogin(false);
        CacheUtil.getInstance().setActivate(false);
        CacheUtil.getInstance().setUserId("");
        CacheUtil.getInstance().setUserName("");
        CacheUtil.getInstance().setUserPhone("");
        CacheUtil.getInstance().savePicPath("");
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        Intent loginActiviy = new Intent(this, LoginActivity.class);
        startActivity(loginActiviy);
    }

    /**
     * 断开用户登录连接
     */
    private void logoutHttp(String userID) {
        String logoutUrl = ProtocolUtil.getLogoutUrl(userID);
        Log.i(TAG, logoutUrl);
        NetRequest netRequest = new NetRequest(logoutUrl);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出失败");
            }

            @Override
            public void onNetworkRequestCancelled() {
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出成功");
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    private void initView() {
        //listenerDialog = new ListenerDialog(this);

    }

    private void initData(){
        initDBName();
        MainController.getInstance().init(this);
        MainController.getInstance().initShop();
        MainController.getInstance().initBigPic();
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
        notifyHomeFragment();
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
        notifyHomeFragment();
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
        if(null != regionVo){
            try {
                IBeaconEntity iBeaconEntity = regionVo.getiBeacon();
                String locId = iBeaconEntity.getLocid();
                String shopid = iBeaconEntity.getShopid();
                String locdesc = iBeaconEntity.getLocdesc();
                String sexStr = CacheUtil.getInstance().getSex();
                LocPushBean locPushBean = new LocPushBean();
                int sex = Integer.parseInt(sexStr);
                locPushBean.setSex(sex);
                locPushBean.setUserid(CacheUtil.getInstance().getUserId());
                locPushBean.setUsername(CacheUtil.getInstance().getUserName());
                if(!TextUtils.isEmpty(locId)){
                    locPushBean.setLocid(locId);
                }
                if(!TextUtils.isEmpty(shopid)){
                    locPushBean.setShopid(shopid);
                }
                if(!TextUtils.isEmpty(locdesc)){
                    locPushBean.setLocdesc(locdesc);
                }
                String alert = "";
                String msg = new Gson().toJson(locPushBean);
                if(sex == 0){
                    alert = CacheUtil.getInstance().getUserName()+"女士到达"+locdesc;
                }else{
                    alert = CacheUtil.getInstance().getUserName()+"先生到达"+locdesc;
                }
                Log.i(TAG,"云巴推送订阅内容:"+msg);
                JSONObject opts = new JSONObject();
                JSONObject apn_json = new JSONObject();
                JSONObject aps = new JSONObject();
                aps.put("sound", "default");
                aps.put("badge", 1);
                aps.put("alert", alert);
                apn_json.put("aps", aps);
                opts.put("apn_json", apn_json);
                YunBaManager.publish2(getApplicationContext(), locId, msg, opts,
                        new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.i(TAG,"订阅云巴推送消息成功");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                if (exception instanceof MqttException) {
                                    MqttException ex = (MqttException)exception;
                                    String msg =  "publish failed with error code : " + ex.getReasonCode();
                                    Log.i(TAG,"订阅云巴推送消息失败:"+msg);
                                }
                            }
                        }
                );
                //区域位置变化通知
                MainController.getInstance().requestArriveNoticeTask(shopid,locId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void notifyHomeFragment(){
        if(getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.footer_tab_rb_home)) != null){
            HomeFragment homeFragment = (HomeFragment)getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.footer_tab_rb_home));
            homeFragment.notifyMainTextChange();
        }
    }

    public void changTag(int rbId){
        TabNavigationFragment.setCurrentNavigationChecked(rbId,this);
    }
}
