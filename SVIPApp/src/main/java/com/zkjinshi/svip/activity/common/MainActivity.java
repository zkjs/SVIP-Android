package com.zkjinshi.svip.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.adapter.FooterFragmentPagerAdapter;
import com.zkjinshi.svip.base.BaseFragmentActivity;
import com.zkjinshi.svip.bean.LocPushBean;
import com.zkjinshi.svip.bluetooth.IBeaconController;
import com.zkjinshi.svip.bluetooth.IBeaconObserver;
import com.zkjinshi.svip.bluetooth.IBeaconSubject;
import com.zkjinshi.svip.bluetooth.IBeaconVo;
import com.zkjinshi.svip.emchat.observer.EMessageListener;
import com.zkjinshi.svip.emchat.observer.EMessageSubject;
import com.zkjinshi.svip.emchat.observer.IEMessageObserver;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.fragment.MessageFragment;
import com.zkjinshi.svip.fragment.SetFragment;
import com.zkjinshi.svip.fragment.ShopFragment;
;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.BadgeView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.yunba.android.manager.YunBaManager;

public class MainActivity extends BaseFragmentActivity implements IBeaconObserver,IEMessageObserver {

    public static final String TAG = MainActivity.class.getSimpleName();

    private final static int REQUEST_CONSUME_RECORD = 0x05;

    SVIPApplication svipApplication;
    public OrderLastResponse lastOrderInfo = null;
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private ArrayList<Fragment> fragmentList;
    private BadgeView bv;
    private int badgeNum;
    private UpdateBroadcastReceiver mUpdateReceiver;
    private MessageFragment messageFragment;
    private  HomeFragment homeFragment;

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
        addAllObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = new Intent(Constants.UPDATE_UNREAD_RECEIVER_ACTION);
        sendBroadcast(intent);
        if( null != homeFragment && getCurrentItem() == 0){
            homeFragment.loadHomeData();
        }
        int pageIndex = getIntent().getIntExtra("pageIndex",0);
        if(pageIndex == 2 ){
            viewPager.setCurrentItem(2,true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IBeaconSubject.getInstance().removeObserver(this);
        EMessageListener.getInstance().unregisterEventListener();
        removeAllObserver();
        if (null != mUpdateReceiver) {
            unregisterReceiver(mUpdateReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //请求订单接口返回 用于进入商家列表界面
        if(requestCode == REQUEST_CONSUME_RECORD){
            if(resultCode == RESULT_OK){
                this.setCurrentItem(1);
            }
        }
    }

    private void initView(){
        viewPager =(ViewPager)findViewById(R.id.viewPager);
        radioGroup =(RadioGroup)findViewById(R.id.footer_tab_rg);
        radioGroup.setOnCheckedChangeListener(new FooterCheckChangeListener());
        bv = new BadgeView(this, findViewById(R.id.footer_tab_btn_message_num));
    }

    private void initViewPager(){
        homeFragment = new HomeFragment();
        ShopFragment shopFragment = new ShopFragment();
        messageFragment = new MessageFragment();
        SetFragment setFragment = new SetFragment();
        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(homeFragment);
        fragmentList.add(shopFragment);
        fragmentList.add(messageFragment);
        fragmentList.add(setFragment);
        viewPager.setAdapter(new FooterFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new FooterOnPageChangeListener());


    }

    private class FooterCheckChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.footer_tab_rb_home:
                    viewPager.setCurrentItem(0,true);
                    break;
                case R.id.footer_tab_rb_shop:
                    viewPager.setCurrentItem(1,true);
                    break;
                case R.id.footer_tab_rb_message:
                    viewPager.setCurrentItem(2,true);
                    break;
                case R.id.footer_tab_rb_set:
                    viewPager.setCurrentItem(3,true);
                    break;
            }
        }
    }

    private class FooterOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    radioGroup.check(R.id.footer_tab_rb_home);
                    if(null != homeFragment){
                       homeFragment.loadHomeData();
                    }
                    break;
                case 1:
                    radioGroup.check(R.id.footer_tab_rb_shop);
                    break;
                case 2:
                    radioGroup.check(R.id.footer_tab_rb_message);
                    break;
                case 3:
                    radioGroup.check(R.id.footer_tab_rb_set);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void initData(){
        initViewPager();
        initDBName();
        initBadgeNum();
        MainController.getInstance().init(this);
        MainController.getInstance().initBigPic();
        MainController.getInstance().checkAppVersion();
    }

    private void initBadgeNum(){
        mUpdateReceiver = new UpdateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_UNREAD_RECEIVER_ACTION);
        registerReceiver(mUpdateReceiver, filter);
        Drawable right = getResources().getDrawable(
                R.mipmap.skin_list_newmessage);
        right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
        bv.setCompoundDrawables(null, null, right, null);
        bv.setBackgroundColor(getResources().getColor(R.color.transparent));
        bv.setPadding(0,0, DisplayUtil.dip2px(this,15),0);
        badgeNum = EMChatManager.getInstance().getUnreadMsgsCount();
        if (badgeNum > 0) {
            bv.show();
        } else {
            bv.hide();
        }
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
    public void intoRegion(IBeaconVo iBeaconVo) {
        LogUtil.getInstance().info(LogLevel.DEBUG,"--欢迎惠顾酒店-----");
        LogUtil.getInstance().info(LogLevel.DEBUG, "beacon info:" + iBeaconVo.toString());
        LogUtil.getInstance().info(LogLevel.DEBUG, "---------------------");

        reginAdPush(iBeaconVo);
        addRegionVo(iBeaconVo);
        notifyHomeFragment();
    }

    @Override
    public void outRegin(IBeaconVo iBeaconVo) {
        LogUtil.getInstance().info(LogLevel.DEBUG,"--欢迎下次光临-----");
        LogUtil.getInstance().info(LogLevel.DEBUG, "beacon info:" + iBeaconVo.toString());
        LogUtil.getInstance().info(LogLevel.DEBUG, "---------------------");

        removeRegionVo(iBeaconVo);
        notifyHomeFragment();
    }

    //添加蓝牙设备
    private void addRegionVo(IBeaconVo iBeaconVo){

        for(IBeaconVo item : svipApplication.mRegionList){
            if(item.getBeaconKey().equals(iBeaconVo.getBeaconKey())){
                svipApplication.mRegionList.remove(item);
                break;
            }
        }
        svipApplication.mRegionList.add(iBeaconVo);
    }

    //移除蓝牙设备
    private void removeRegionVo(IBeaconVo iBeaconVo){
        for(IBeaconVo item : svipApplication.mRegionList){
            if(item.getBeaconKey().equals(iBeaconVo.getBeaconKey())){
                svipApplication.mRegionList.remove(item);
                break;
            }
        }
    }

    /**
     * 区域广告通知
     * @param iBeaconVo
     */
    private void reginAdPush(IBeaconVo iBeaconVo){
        if(null != iBeaconVo){
            try {

                String locId = iBeaconVo.getLocid();
                String shopid = iBeaconVo.getShopid();
                String locdesc = iBeaconVo.getLocdesc();
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
        if(homeFragment != null){
            homeFragment.notifyIbeacon();
        }
    }

    public void setCurrentItem(int position){
        if(null != viewPager){
            viewPager.setCurrentItem(position);
        }
    }

    int waitTime = 2000;

    long touchTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if((currentTime-touchTime)>=waitTime) {
            touchTime = currentTime;
        }else {
            onExit();
        }
    }

    /**
     * 添加观察者
     */
    private void addAllObserver(){
        EMessageSubject.getInstance().addObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().removeObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    @Override
    public void receive(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            case EventOfflineMessage:
            case EventConversationListChanged:
                Intent intent = new Intent(Constants.UPDATE_UNREAD_RECEIVER_ACTION);
                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }

    private class UpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context ctx, Intent intent) {
            badgeNum = EMChatManager.getInstance().getUnreadMsgsCount();
            if (badgeNum > 0) {
                bv.show();
            } else {
                bv.hide();
            }
        }

    }

    public int getCurrentItem(){
        int currentItem = 0;
        if(null != viewPager){
            currentItem = viewPager.getCurrentItem();
        }
        return currentItem;
    }

}
