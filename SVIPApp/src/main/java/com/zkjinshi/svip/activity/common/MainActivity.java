package com.zkjinshi.svip.activity.common;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.bean.LocPushBean;
import com.zkjinshi.svip.emchat.observer.EMessageListener;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.fragment.TabNavigationFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconEntity;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
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

    private void initView() {
        //listenerDialog = new ListenerDialog(this);

    }

    private void initData(){
        initDBName();
        MainController.getInstance().init(this);
        MainController.getInstance().initShop();
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
            IBeaconEntity iBeaconEntity = regionVo.getiBeacon();
            String locId = iBeaconEntity.getLocid();
            String shopid = iBeaconEntity.getShopid();
            String locdesc = iBeaconEntity.getLocdesc();
            LocPushBean locPushBean = new LocPushBean();
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
            //新增云巴测试
            YunBaManager.publish(getApplicationContext(), locId, new Gson().toJson(locPushBean),
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            String msgLog = "Publish succeed : " + asyncActionToken.getTopics().toString();
                            Log.i(TAG,"onSuccess:"+msgLog);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            if (exception instanceof MqttException) {
                                MqttException ex = (MqttException)exception;
                                String msg =  "publish failed with error code : " + ex.getReasonCode();
                                Log.i(TAG,"onFailure:"+msg);
                            }
                        }
                    }
            );
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
