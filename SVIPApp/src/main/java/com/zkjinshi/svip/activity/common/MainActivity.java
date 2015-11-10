package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;
import com.zkjinshi.svip.activity.order.ShopListActivity;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocA2M;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.fragment.MenuRightFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.listener.MessageListener;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.request.pushad.MsgPushLocA2MReqTool;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.MessageVo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import com.zkjinshi.svip.view.GooeyMenu;

import org.apache.log4j.chainsaw.Main;


public class MainActivity extends FragmentActivity implements IBeaconObserver, GooeyMenu.GooeyMenuInterface {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public static final int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;

    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;
    private SlidingMenu slidingMenu;
    private GooeyMenu mGooeyMenu;

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
    }

    private MainTextStatus mainTextStatus = MainTextStatus.DEFAULT_NULL;

    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {

    }

    @Override
    public void menuItemClicked(int menuNumber) {
        Toast.makeText(MainActivity.this,"click"+menuNumber,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void menuLongClicked() {
        Toast.makeText(MainActivity.this,"long click",Toast.LENGTH_SHORT).show();
    }


    private void initView(){
        initMenu();
        TextView nameTv = (TextView)findViewById(R.id.name_tv);
        nameTv.setText(CacheUtil.getInstance().getUserName());

        mGooeyMenu = (GooeyMenu) findViewById(R.id.gooey_menu);
        mGooeyMenu.setOnMenuListener(this);

    }



    private void initData(){
        initDBName();
        EventBus.getDefault().register(this);
        MainController.getInstance().init(this);
        MainController.getInstance().initShop();
        MainController.getInstance().initServerPersonal();
        LocationManager.getInstance().registerLocation(this);
        MessageListener  messageListener = new MessageListener();
        initService(messageListener);
        //设置消息未读个数
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
        // 设置触摸屏幕的模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        /** 去除侧滑阴影效果 add by winkyqin */
//        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        slidingMenu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
        //设置右边（二级）侧滑菜单
        slidingMenu.setSecondaryMenu(R.layout.right_menu_frame);
        Fragment rightMenuFragment = new MenuRightFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_right_menu_frame, rightMenuFragment).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
    }

    protected  void onResume(){
        super.onResume();
        CircleImageView photoCtv = (CircleImageView)findViewById(R.id.main_user_photo_civ);
        String userId = CacheUtil.getInstance().getUserId();
        String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
        if(!TextUtils.isEmpty(userPhotoUrl)){
            MainController.getInstance().setPhoto(userPhotoUrl, photoCtv);
        }
        setBadgeNum();
        loadLastOrderInfo();
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
                        return;
                    }
                    if (lastOrderInfo != null && lastOrderInfo.getReservation_no().equals("0")) {
                        lastOrderInfo = null;
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
                    CircleImageView shopLogo = (CircleImageView) findViewById(R.id.civ_shop_icon);
                    MainController.getInstance().setPhoto(ProtocolUtil.getShopIconUrl(shopId), shopLogo);

                    TextView shopNameTv = (TextView) findViewById(R.id.shop_name_tv);
                    shopNameTv.setText(lastOrderInfo.getFullname());

                    String arriveDateStr = "";
                    int dayNum = 0;
                    SimpleDateFormat mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");
                    try {
                        Date date = mSimpleFormat.parse(arriveDate);
                        arriveDateStr = mChineseFormat.format(date);

                        dayNum = TimeUtil.daysBetween(arriveDate, departureDate);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    TextView orderInfoTv = (TextView) findViewById(R.id.tv_book_info);
                    orderInfoTv.setVisibility(View.VISIBLE);
                    orderInfoTv.setText("" + roomType + "  |  " + arriveDateStr + "入住  |  " + dayNum + "晚" + "  |  ￥" + roomRate);
                    handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
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
                slidingMenu.showSecondaryMenu();
            }
        });

        findViewById(R.id.order_llt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (mainTextStatus) {
                    case DEFAULT_NULL:
                        intent = new Intent(MainActivity.this, ShopListActivity.class);
                        break;
                    case NO_ORDER_NOT_IN:
                        intent = new Intent(MainActivity.this, ShopListActivity.class);
                        break;
                    case NO_ORDER_IN:
                        if (svipApplication.mRegionList.size() > 0) {

                            intent = new Intent(MainActivity.this, OrderBookingActivity.class);
                            intent.putExtra("shopid", svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1).getiBeacon().getShopid());
                        }

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
                Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        findViewById(R.id.main_logo_ibtn).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (lastOrderInfo != null && lastOrderInfo.getPhone() != null) {
                    IntentUtil.callPhone(MainActivity.this, lastOrderInfo.getPhone());
                    return true;
                } else if (svipApplication.mRegionList.size() > 0) {
                    int index = svipApplication.mRegionList.size() - 1;
                    String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                    String phone = ShopListManager.getInstance().getShopPhone(shopid);
                    IntentUtil.callPhone(MainActivity.this, phone);
                    return true;
                }
                return false;
            }
        });



        //智能键
        findViewById(R.id.main_logo_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (mainTextStatus) {
                    case NO_ORDER_NOT_IN:
                        intent = new Intent(MainActivity.this, ShopListActivity.class);
                        intent.putExtra("click_to_talk", true);
                        break;
                    default:
                        if (lastOrderInfo != null && lastOrderInfo.getShopid() != null) {
                            intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("shop_id", lastOrderInfo.getShopid());
                            intent.putExtra("shop_name", lastOrderInfo.getFullname());
                        } else if (svipApplication.mRegionList.size() > 0) {
                            int index = svipApplication.mRegionList.size() - 1;
                            String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                            String fullname = ShopListManager.getInstance().getShopName(shopid);

                            intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("shop_id", shopid);
                            intent.putExtra("shop_name", fullname);
                        } else {
                            intent = new Intent(MainActivity.this, ShopListActivity.class);
                            intent.putExtra("click_to_talk", true);
                        }

                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }


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
        EventBus.getDefault().unregister(this);
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
        LogUtil.getInstance().info(LogLevel.ERROR,"--欢迎惠顾酒店-----");
        LogUtil.getInstance().info(LogLevel.ERROR,"inTime:"+regionVo.getInTime());
        LogUtil.getInstance().info(LogLevel.ERROR, "beacon info:" + regionVo.getiBeacon().toString());
        LogUtil.getInstance().info(LogLevel.ERROR, "---------------------");

        reginAdPush(regionVo);
        addRegionVo(regionVo);
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
    }

    @Override
    public void outRegin(RegionVo regionVo) {
        LogUtil.getInstance().info(LogLevel.ERROR,"--欢迎下次光临-----");
        LogUtil.getInstance().info(LogLevel.ERROR,"inTime:"+regionVo.getInTime());
        LogUtil.getInstance().info(LogLevel.ERROR,"outTime:"+regionVo.getOutTime());
        LogUtil.getInstance().info(LogLevel.ERROR, "standTime:"+regionVo.getStandTime());
        LogUtil.getInstance().info(LogLevel.ERROR, "beacon info:" + regionVo.getiBeacon().toString());
        LogUtil.getInstance().info(LogLevel.ERROR, "---------------------");

        removeRegionVo(regionVo);
        if(svipApplication.mRegionList.size() <= 0){

        }
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
        TextView locationTv = (TextView)findViewById(R.id.distance_tv);
        if(lastOrderInfo == null && svipApplication.mRegionList.size() > 0){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            locationTv.setText(region.getiBeacon().getLocdesc());
        }else if(checkIfInOrderShop()){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            locationTv.setText(region.getiBeacon().getLocdesc());
        }else{
            locationTv.setText("不在酒店");
        }
    }

    /**
     * 改变中间块提示信息。
     */
    private void changMiddleBlockTips() throws  Exception{
        TextView orderStatusTv1 = (TextView)findViewById(R.id.tv_order_status);
        switch (getMainTextStatus()){
            //其他状态
            case DEFAULT_NULL:
                orderStatusTv1.setText("如有疑问，请和客服联系!");
                break;
            //没订单，不在酒店
            case NO_ORDER_NOT_IN:
                orderStatusTv1.setText("您没有任何预订信息 立即预订");
                break;
            //没订单，在酒店
            case NO_ORDER_IN:
                int index = svipApplication.mRegionList.size()-1;
                String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                String fullname = ShopListManager.getInstance().getShopName(shopid);
                orderStatusTv1.setText(fullname + "欢迎您，点击马上预订酒店");
                break;
            //有预定状态订单，不在酒店
            case BOOKING_NOT_IN:
                orderStatusTv1.setText("订单已提交，请等待酒店确定");
                break;
            //有预定状态订单，在酒店
            case BOOKING_IN:
                orderStatusTv1.setText("订单已提交，请等待酒店确定");
                break;
            //有确认状态的订单，在酒店，有免前台特权
            case SURE_IN_HAVE_NOLOGIN:
                orderStatusTv1.setText("到达酒店，将为您办理入住手续");
                break;
            //有确认状态的订单，在酒店，没有免前台特权
            case SURE_IN_NOT_NOLOGIN:
                orderStatusTv1.setText("到达酒店，请办理入住手续");
                break;
            //有确认状态订单，不在酒店
            case SURE_NOT_IN:
                Calendar todayC = Calendar.getInstance();
                Date today = todayC.getTime();
                SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivelDay = mSimpleFormat.parse(lastOrderInfo.getArrival_date());
                int offsetDay = TimeUtil.daysBetween(today,arrivelDay);
                if(offsetDay != 0){
                    orderStatusTv1.setText(offsetDay+"天后入住"+lastOrderInfo.getFullname()+"酒店");
                }else{
                    orderStatusTv1.setText("今天入住"+lastOrderInfo.getFullname()+"酒店");
                }

                break;
            //有已经入住订单，在酒店
            case CHECKIN_IN:
                String locdesc = svipApplication.mRegionList.get(svipApplication.mRegionList.size()-1).getiBeacon().getLocdesc();
                orderStatusTv1.setText("您到达" + locdesc + "," + lastOrderInfo.getFullname() + "随时为您服务！");

                Calendar todayC2 = Calendar.getInstance();
                Date today2 = todayC2.getTime();
                SimpleDateFormat mSimpleFormat2  = new SimpleDateFormat("yyyy-MM-dd");
                Date departureDay = mSimpleFormat2.parse(lastOrderInfo.getDeparture_date());
                int offsetDay2 = TimeUtil.daysBetween(today2,departureDay);
                if(offsetDay2 == 0){
                    orderStatusTv1.setText("您今天需要退房，旅途愉快!");

                }
                break;
            //有已经入住订单，不在酒店
            case CHECKIN_NOT_IN:
                orderStatusTv1.setText(lastOrderInfo.getFullname() + "随时为您服务！");

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
            IBeaconController.getInstance().init();
        }
    }

    /**
     * 初始化socket
     */
    private void initService(MessageListener messageListener) {
        WebSocketManager.getInstance().initService(this).setMessageListener(messageListener);
    }

    @Subscribe
    public void onEvent(MessageVo messageVo){
        Message message = new Message();
        message.what = NOTIFY_UPDATE_VIEW;
        handler.sendMessage(message);
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
