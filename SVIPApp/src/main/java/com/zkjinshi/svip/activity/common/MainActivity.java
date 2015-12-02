package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;
import com.zkjinshi.svip.activity.order.OrderEvaluateActivity;
import com.zkjinshi.svip.activity.order.ShopActivity;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocA2M;
import com.zkjinshi.svip.emchat.observer.EMessageListener;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.listener.MessageListener;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.request.pushad.MsgPushLocA2MReqTool;
import com.zkjinshi.svip.response.AdPushResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.MapUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.BookingDialog;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.GooeyMenu;
import com.zkjinshi.svip.view.ListenerDialog;
import com.zkjinshi.svip.view.zoomview.CityDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends FragmentActivity implements IBeaconObserver,
        GooeyMenu.GooeyMenuInterface, LocationManager.LocationChangeListener {


    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public static final int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;

    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;
    private SlidingMenu slidingMenu;
    private GooeyMenu mGooeyMenu;
    private WebView webView;

    private TextView shopNameTv;
    private RatingBar ratingBar;
    private TextView majorTv,minorTv;
    private CircleImageView shopIcon;
    private LinearLayout containerLlt;
    public TextView haveOrderTv,distanceTv;
    public TextView mTvActivate;

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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取位置信息
        geoLat = aMapLocation.getLatitude();//纬度
        geoLng = aMapLocation.getLongitude();//经度
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图返回位置信息：("+geoLat+","+geoLng+")");
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);

    }

    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {

    }

    @Override
    public void menuItemClicked(int menuNumber) {
        if(mGooeyMenu.isMenuVisible){
            mGooeyMenu.hide();
        }
        //Toast.makeText(MainActivity.this,"click"+menuNumber,Toast.LENGTH_SHORT).show();
       loadCleverServer(menuNumber);
    }

    public void showBookingDailog(int menuNumber,String shopId,CustomerServiceBean customerServiceBean){
        BookingDialog bookingDialog = new BookingDialog(this);
        bookingDialog.pageIndex = menuNumber -1;
        bookingDialog.shopId = shopId;
        bookingDialog.customerService = customerServiceBean;
        bookingDialog.shopName = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId);
        if(listenerDialog != null && !TextUtils.isEmpty(listenerDialog.getMediaPath())){
            bookingDialog.recordFileName = listenerDialog.getMediaPath();
            bookingDialog.recordSecond = listenerDialog.getRecordSecond();
            listenerDialog.setRecordFileName(null);
        }
        bookingDialog.show();
    }

    @Override
    public void menuLongClicked() {
        if(mGooeyMenu.isMenuVisible){
            mGooeyMenu.hide();
        }
       if(!listenerDialog.isShowing()){
           listenerDialog.show();
           listenerDialog.startRecord();
       }

    }

    public void menuLongClickedUp(){
        if(listenerDialog.isShowing()){
            listenerDialog.stopRecord();
            listenerDialog.cancel();
        }
        loadCleverServer(1);
    }

    //智能选择酒店
    public String getCleverShopId(){
        if(lastOrderInfo != null){
            return lastOrderInfo.getShopid();
        }else if(svipApplication.mRegionList.size() > 0){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            return  region.getiBeacon().getShopid();
        }
        return "120";
    }

    //智能选择客服
    public void loadCleverServer(final int menuNumber){
        final String shopid = getCleverShopId();
        CustomerServicesManager.getInstance().requestServiceListTask(this,shopid , new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result:" + result.rawResult);
                CustomerServiceListResponse customerServiceListResponse = new Gson().fromJson(result.rawResult, CustomerServiceListResponse.class);
                if (null != customerServiceListResponse) {
                    HeadBean head = customerServiceListResponse.getHead();
                    if (null != head) {
                        boolean isSet = head.isSet();
                        if (isSet) {
                            ArrayList<CustomerServiceBean> customerServiceList = customerServiceListResponse.getData();
                            String salesId = head.getExclusive_salesid();
                            CustomerServiceBean customerService = null;
                            if (null != customerServiceList && !customerServiceList.isEmpty()) {
                                if (!TextUtils.isEmpty(salesId)) {//有专属客服
                                    customerService = CustomerServicesManager.getInstance().getExclusiveCustomerServic(customerServiceList, salesId);
                                } else {//无专属客服
                                    customerService = CustomerServicesManager.getInstance().getRandomCustomerServic(customerServiceList);
                                }
                                showBookingDailog(menuNumber,shopid,customerService);
                            }



                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
    }




    private void initView(){
        listenerDialog = new ListenerDialog(this);
        initMenu();
        TextView nameTv = (TextView)findViewById(R.id.name_tv);
        nameTv.setText(CacheUtil.getInstance().getUserName());
        TextView appLevelTv = (TextView)findViewById(R.id.level_tv);
        appLevelTv.setText("VIP "+CacheUtil.getInstance().getUserApplevel());

        mGooeyMenu = (GooeyMenu) findViewById(R.id.gooey_menu);
        mGooeyMenu.setOnMenuListener(this);

        shopIcon = (CircleImageView)findViewById(R.id.civ_shop_icon);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        majorTv = (TextView)findViewById(R.id.tv_order_status);
        minorTv = (TextView)findViewById(R.id.tv_book_info);
        shopNameTv = (TextView)findViewById(R.id.shop_name_tv);
        haveOrderTv = (TextView)findViewById(R.id.have_order_tv);
        distanceTv = (TextView)findViewById(R.id.distance_tv);
        mTvActivate = (TextView)findViewById(R.id.tv_activate_immediately);

        ratingBar.setVisibility(View.INVISIBLE);
        majorTv.setText("");
        minorTv.setText("");
        shopNameTv.setText("");
        haveOrderTv.setText("没订单");
        distanceTv.setText("距离未知");


        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // 是否支持javascript
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); // 隐藏滚动条
        webView.getSettings().setDomStorageEnabled(true);// 设置可以使用localStorage
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);// 默认使用缓存
        webView.getSettings().setAppCacheMaxSize(8 * 1024 * 1024);// 缓存最多可以有8M
        webView.getSettings().setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAppCacheEnabled(false);// 应用可以有缓存
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSaveFormData(true);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
//        webView.setWebViewClient(new WebViewClient() {
//
//
//            /**
//             * 载入页面完成的事件 |同样道理，我们知道一个页面载入完成，于是我们可以关闭loading条，切换程序动作
//             */
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//                findViewById(R.id.progressBar).setVisibility(View.GONE);
//                super.onPageFinished(view, url);
//            }
//
//        });

    }



    private void initData(){
        initDBName();
        MainController.getInstance().init(this);
        MainController.getInstance().initShop();
        MainController.getInstance().initServerPersonal();
        MessageListener  messageListener = new MessageListener();
        initService(messageListener);
        setBadgeNum();
    }

    private void setBadgeNum(){
        int notifyCount = MessageDBUtil.getInstance().queryNotifyCount();
        TextView msgNumTv = (TextView)findViewById(R.id.main_msg_num_tv);
        if (notifyCount > 99) {
            msgNumTv.setText(99 + "+");
            msgNumTv.setVisibility(View.VISIBLE);
        } else if (notifyCount > 0) {
            msgNumTv.setText(notifyCount + "");
            msgNumTv.setVisibility(View.VISIBLE);
        } else {
            msgNumTv.setVisibility(View.GONE);
        }
    }

    private void initMenu() {
        slidingMenu = new SlidingMenu(this);
        Fragment leftMenuFragment = new MenuLeftFragment();
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(getLayoutInflater().inflate(R.layout.left_menu_frame, null));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        // 设置触摸屏幕的模式f
        /** 修改触摸屏幕模式为左侧单独划出 add by winkyqin 2015/11/11 */
        slidingMenu.setMode(SlidingMenu.LEFT);
//        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        /** 去除侧滑阴影效果 add by winkyqin */
//        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        slidingMenu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);

        /** 取消右侧菜单滑出显示 add by winkyqin 2015/11/11 */
        //设置右边（二级）侧滑菜单
//        slidingMenu.setSecondaryMenu(R.layout.right_menu_frame);
//        Fragment rightMenuFragment = new MenuRightFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.id_right_menu_frame, rightMenuFragment).commit();
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

        CircleImageView photoCtv = (CircleImageView)findViewById(R.id.main_user_photo_civ);
        String userId = CacheUtil.getInstance().getUserId();
        String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
        if(!TextUtils.isEmpty(userPhotoUrl)){
            MainController.getInstance().setPhoto(userPhotoUrl, photoCtv);
        }

        TextView nameTv = (TextView)findViewById(R.id.name_tv);
        nameTv.setText(CacheUtil.getInstance().getUserName());
        TextView appLevelTv = (TextView)findViewById(R.id.level_tv);
        appLevelTv.setText("VIP "+CacheUtil.getInstance().getUserApplevel());

        setBadgeNum();
        loadLastOrderInfo();
        loadAdPush();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_ACTIVATE_INVITE_CODE){
                onResume();
            }
        }
    }

    private void initWebView(String webViewURL){
        webView.loadUrl(webViewURL);
    }

    /**
     * 固定链接获取推送广告
     */
    private void loadAdPush() {
        String url = ProtocolUtil.getAdList();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {

                    AdPushResponse adPushResponse = new Gson().fromJson(result.rawResult, AdPushResponse.class);
                    if (adPushResponse != null && !TextUtils.isEmpty(adPushResponse.getUrl())) {
                        initWebView(adPushResponse.getUrl());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    protected void onStop(){
        super.onStop();
    }

    //加载最近的一条订单信息
    private void loadLastOrderInfo(){
        String url = ProtocolUtil.getLastOrder();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {

                    lastOrderInfo = new Gson().fromJson(result.rawResult, OrderLastResponse.class);
                    if (lastOrderInfo != null && !lastOrderInfo.isSet()) {
                        CacheUtil.getInstance().setLogin(false);
                        DialogUtil.getInstance().showToast(MainActivity.this, "token 过期，请重新登录");
                        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                        return;
                    }
                    if (lastOrderInfo != null && lastOrderInfo.getUserid() == null) {
                        lastOrderInfo = null;
                        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                        return;
                    }
                    if (lastOrderInfo != null && lastOrderInfo.getReservation_no().equals("0")) {
                        lastOrderInfo = null;
                        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                        return;
                    }
                    String roomType = lastOrderInfo.getRoom_type();
                    String roomRate = lastOrderInfo.getRoom_rate();
                    String arriveDate = lastOrderInfo.getArrival_date();
                    String departureDate = lastOrderInfo.getDeparture_date();
                    String orderStatusStr = lastOrderInfo.getStatus();
                    String shopId = lastOrderInfo.getShopid();
                    if (!TextUtils.isEmpty(orderStatusStr)) {
                        CacheUtil.getInstance().setOrderStatus(shopId, Integer.parseInt(orderStatusStr));
                    }
                    MainController.getInstance().setPhoto(ProtocolUtil.getShopIconUrl(shopId), shopIcon);
                    shopNameTv.setText(lastOrderInfo.getFullname());

                    String arriveDateStr = "";
                    int dayNum = 0;
                    SimpleDateFormat mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");

                    Date date = mSimpleFormat.parse(arriveDate);
                    arriveDateStr = mChineseFormat.format(date);
                    dayNum = TimeUtil.daysBetween(arriveDate, departureDate);

                    minorTv.setVisibility(View.VISIBLE);
                    minorTv.setText("" + roomType + "  |  " + arriveDateStr + "入住  |  " + dayNum + "晚" + "  |  ￥" + roomRate);
                    ratingBar.setRating(lastOrderInfo.getStar());
                    haveOrderTv.setText("有订单");
                    handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();

    }

    private void initListeners() {

        //头像
        findViewById(R.id.main_user_photo_civ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //左菜单
        findViewById(R.id.menu_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showMenu();
            }
        });

        //右菜单消息列表
        findViewById(R.id.main_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // slidingMenu.showSecondaryMenu();

                // TODO: 进入右侧服务中心界面
                Intent goServiceCenter = new Intent(MainActivity.this, ServiceCenterActivity.class);
                MainActivity.this.startActivity(goServiceCenter);
                //左出又进
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //城市列表
        findViewById(R.id.city_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent goServiceCenter = new Intent(MainActivity.this, CityActivity.class);
//                MainActivity.this.startActivity(goServiceCenter);
//                //左出又进
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                new CityDialog(MainActivity.this).show();
            }
        });

        findViewById(R.id.order_llt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (mainTextStatus) {
                    case DEFAULT_NULL:
                        intent = new Intent(MainActivity.this, ShopActivity.class);
                        break;
                    case NO_ORDER_NOT_IN:
                        intent = new Intent(MainActivity.this, ShopActivity.class);
                        break;
                    case NO_ORDER_IN:
                        if (svipApplication.mRegionList.size() > 0) {

                            intent = new Intent(MainActivity.this, OrderBookingActivity.class);
                            intent.putExtra("shopid", svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1).getiBeacon().getShopid());
                        }

                        break;
                    case ORDER_FINISH:
                        intent = new Intent(MainActivity.this, OrderEvaluateActivity.class);
                        OrderConsumeResponse bookOrder = new OrderConsumeResponse();
                        bookOrder.setReservation_no(lastOrderInfo.getReservation_no());
                        bookOrder.setShopid(lastOrderInfo.getShopid());
                        bookOrder.setRoom_type(lastOrderInfo.getRoom_type());
                        bookOrder.setRooms(lastOrderInfo.getRooms());
                        bookOrder.setRoom_rate(lastOrderInfo.getRoom_rate());
                        bookOrder.setArrival_date(lastOrderInfo.getArrival_date());
                        bookOrder.setDeparture_date(lastOrderInfo.getDeparture_date());
                        intent.putExtra("bookOrder", bookOrder);
                        break;
                    default:
                        if (lastOrderInfo != null && lastOrderInfo.getReservation_no() != null) {
                            intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                            intent.putExtra("reservation_no", lastOrderInfo.getReservation_no());
                            intent.putExtra("shopid", lastOrderInfo.getShopid());
                        }

                }
                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
            }
        });

        findViewById(R.id.main_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        mTvActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goInvitesCode = new Intent(MainActivity.this, InviteCodeActivity.class);
                startActivityForResult(goInvitesCode, REQUEST_ACTIVATE_INVITE_CODE);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    protected void onDestroy() {
        super.onDestroy();
        IBeaconSubject.getInstance().removeObserver(this);
        EMessageListener.getInstance().unregisterEventListener();
        if(listenerDialog != null){
            listenerDialog.stopAnimation();
        }

    }

    /**
     * 初始化数据库名称
     */
    private void initDBName(){
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
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



    /**
     * 改变位置提示信息。
     */
    private void changLocationTips(){
//        geoLat = 22.596328;
//        geoLng = 113.990493;
        if(lastOrderInfo == null && svipApplication.mRegionList.size() > 0){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            haveOrderTv.setText("没订单");
            distanceTv.setText("在"+region.getiBeacon().getLocdesc());
        }else if(checkIfInOrderShop()){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            haveOrderTv.setText("有订单");
            distanceTv.setText("在"+region.getiBeacon().getLocdesc());
        }else if(lastOrderInfo != null && geoLat != 100){
            double shopLat;
            double shopLng;
            shopLat = lastOrderInfo.getMap_latitude();
            shopLng = lastOrderInfo.getMap_longitude();
            LogUtil.getInstance().info(LogLevel.DEBUG,"酒店位置信息("+shopLat+","+shopLng+")");
            double distancedouble =  MapUtil.GetDistance(geoLat,geoLng,shopLat,shopLng);
            distanceTv.setText("距离"+distancedouble+"KM");
        }else{
            distanceTv.setText("距离未知");
        }
    }

    /**
     * 改变中间块提示信息。
     */
    private void changMiddleBlockTips() throws  Exception{
        switch (getMainTextStatus()){
            //其他状态
            case DEFAULT_NULL:
                shopNameTv.setVisibility(View.INVISIBLE);
                ratingBar.setVisibility(View.INVISIBLE);
                majorTv.setText("如有疑问，请和客服联系!");
                minorTv.setText("长按或点击智键开始快速预定");
                break;
            //没订单，不在酒店
            case NO_ORDER_NOT_IN:
                shopNameTv.setVisibility(View.INVISIBLE);
                ratingBar.setVisibility(View.INVISIBLE);
                majorTv.setText("您没有任何预订信息 立即预订");
                minorTv.setText("长按或点击智键开始快速预定");
                shopIcon.setImageResource(R.mipmap.img_avatar_hotel);
                break;
            //没订单，在酒店
            case NO_ORDER_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.INVISIBLE);
                int index = svipApplication.mRegionList.size()-1;
                String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                String fullname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
                shopNameTv.setText(fullname);
                majorTv.setText("欢迎光临");
                minorTv.setText("长按或点击智键开始快速预定");
                MainController.getInstance().setPhoto(ProtocolUtil.getShopIconUrl(shopid), shopIcon);
                break;
            //有预定状态订单，不在酒店
            case BOOKING_NOT_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                majorTv.setText("订单已生成，请尽快确定");
                break;
            //有预定状态订单，在酒店
            case BOOKING_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                majorTv.setText("订单已生成，请确定");
                break;
            //有确认状态的订单，在酒店，有免前台特权
            case SURE_IN_HAVE_NOLOGIN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                majorTv.setText("到达酒店，将为您办理入住手续");
                break;
            //有确认状态的订单，在酒店，没有免前台特权
            case SURE_IN_NOT_NOLOGIN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                majorTv.setText("到达酒店，请办理入住手续");
                break;
            //有确认状态订单，不在酒店
            case SURE_NOT_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                Calendar todayC = Calendar.getInstance();
                Date today = todayC.getTime();
                SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivelDay = mSimpleFormat.parse(lastOrderInfo.getArrival_date());
                int offsetDay = TimeUtil.daysBetween(today,arrivelDay);
                if(offsetDay != 0){
                    majorTv.setText(offsetDay+"天后入住酒店");
                    if(offsetDay < 0){
                        majorTv.setText("订单已经过期。");
                    }
                }else{
                    majorTv.setText("今天入住酒店");
                }

                break;
            //有已经入住订单，在酒店
            case CHECKIN_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                String locdesc = svipApplication.mRegionList.get(svipApplication.mRegionList.size()-1).getiBeacon().getLocdesc();
                majorTv.setText("您到达" + locdesc + ","  + "随时为您服务！");

                Calendar todayC2 = Calendar.getInstance();
                Date today2 = todayC2.getTime();
                SimpleDateFormat mSimpleFormat2  = new SimpleDateFormat("yyyy-MM-dd");
                Date departureDay = mSimpleFormat2.parse(lastOrderInfo.getDeparture_date());
                int offsetDay2 = TimeUtil.daysBetween(today2,departureDay);
                if(offsetDay2 == 0){
                    majorTv.setText("您今天需要退房，旅途愉快!");

                }
                break;
            //有已经入住订单，不在酒店
            case CHECKIN_NOT_IN:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                majorTv.setText("随时为您服务！");
                break;
            //订单结束
            case ORDER_FINISH:
                shopNameTv.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                if(lastOrderInfo.getScore() == 0){
                    majorTv.setText("行程结束，请评价");
                }else{
                    majorTv.setText("行程结束，已评价");
                }

                break;
        }
    }

    /**
     * 改变主语句
     */
    public synchronized void changeMainText(){

        try {
            changLocationTips();
            changMiddleBlockTips();
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, e.getMessage());
        }
    }

    /**
     * 获取主语句状态
     */
    public MainTextStatus getMainTextStatus(){
        if(lastOrderInfo == null && svipApplication.mRegionList.size() <= 0){
            mainTextStatus = MainTextStatus.NO_ORDER_NOT_IN;
        }
        else if( lastOrderInfo == null &&  svipApplication.mRegionList.size() > 0){
            mainTextStatus = MainTextStatus.NO_ORDER_IN;
        }
        else if(lastOrderInfo != null && lastOrderInfo.getStatus().equals("0") && !checkIfInOrderShop()){
            mainTextStatus = MainTextStatus.BOOKING_NOT_IN;
        }
        else if(lastOrderInfo != null && lastOrderInfo.getStatus().equals("0") && checkIfInOrderShop()){
            mainTextStatus = MainTextStatus.BOOKING_IN;
        }
        else if(checkIfInOrderShop() && lastOrderInfo.getStatus().equals("2") && lastOrderInfo.getNologin().equals("0")){
            mainTextStatus = MainTextStatus.SURE_IN_NOT_NOLOGIN;
        }
        else if(checkIfInOrderShop() && lastOrderInfo.getStatus().equals("2") && lastOrderInfo.getNologin().equals("1")){
            mainTextStatus = MainTextStatus.SURE_IN_HAVE_NOLOGIN;
        }
        else if(!checkIfInOrderShop() && lastOrderInfo != null && lastOrderInfo.getStatus().equals("2")){
            mainTextStatus = MainTextStatus.SURE_NOT_IN;
        }
        else if(checkIfInOrderShop() && lastOrderInfo != null && lastOrderInfo.getStatus().equals("4")){
            mainTextStatus = MainTextStatus.CHECKIN_IN;
        }
        else if(!checkIfInOrderShop() && lastOrderInfo != null && lastOrderInfo.getStatus().equals("4")){
            mainTextStatus = MainTextStatus.CHECKIN_NOT_IN;
        }
        else if( lastOrderInfo != null && lastOrderInfo.getStatus().equals("3")){
            mainTextStatus = MainTextStatus.ORDER_FINISH;
        }
        else{
            mainTextStatus = MainTextStatus.DEFAULT_NULL;
        }
        return mainTextStatus;
    }

    /**
     * 判断用户是否在最近订单所在的酒店中 如果在则返回true 否则 返回false。
     */
    private boolean checkIfInOrderShop(){
        if(svipApplication.mRegionList.size() <= 0){
            return false;
        }
        RegionVo regionVo = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
        if(lastOrderInfo!= null && regionVo.getiBeacon().getShopid().equals(lastOrderInfo.getShopid())) {
            return true;
        }
        return false;
    }

    /**
     * 初始化beacon列表
     */
    private void initIBeaconList(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 大于等于android 4.4
            IBeaconController.getInstance().init(this);
        }
    }

    /**
     * 初始化socket
     */
    private void initService(MessageListener messageListener) {
        WebSocketManager.getInstance().initService(this).setMessageListener(messageListener);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOTIFY_UPDATE_VIEW:
                    setBadgeNum();
                    break;
                case NOTIFY_UPDATE_MAIN_TEXT:
                    changeMainText();
                    break;
            }
        }
    };

}
