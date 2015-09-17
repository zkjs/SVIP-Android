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
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.activity.order.GoodListActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;
import com.zkjinshi.svip.activity.order.ShopListActivity;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocA2M;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.fragment.MenuRightFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.listener.MessageListener;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.request.pushad.MsgPushLocA2MReqTool;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.sqlite.MessageDBUtil;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.BadgeView;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.kenburnsview.KenBurnsView;
import com.zkjinshi.svip.view.kenburnsview.Transition;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.OrderInfoVo;
import com.zkjinshi.svip.vo.ShopDetailVo;

import com.zkjinshi.svip.vo.UserInfoVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainActivity extends FragmentActivity implements IBeaconObserver {

    public static final String TAG = "MainActivity";

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public static final int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;

    private KenBurnsView kbv;
    private CircleImageView photoCtv;
    private ImageButton menuIbtn,msgListIBtn,logoIBtn;
    private int notifyCount;
    private SlidingMenu slidingMenu;
    private Fragment leftMenuFragment;
    private MessageListener messageListener;

    private LinearLayout orderLlt;
    private LinearLayout mianqianLlt;
    private LinearLayout addLlt;
    private TextView locationTv;
    private TextView orderInfoTv;
    private TextView orderStatusTv1;
    private TextView msgNumTv;


    private Response.Listener<String> loadOrderListener;
    private Response.ErrorListener    loadOrderErrorListener;

    private String lastShopId = "";
    private OrderLastResponse lastOrderInfo = null;
   // private ShopDetailVo lastShopInfo  = null;
   // private ArrayList<RegionVo> mRegionList = new ArrayList<RegionVo>();
    SVIPApplication svipApplication;

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

    private void initView(){
        initMenu();
        kbv  = new KenBurnsView(this);
        photoCtv = (CircleImageView)findViewById(R.id.main_user_photo_civ);
        menuIbtn = (ImageButton)findViewById(R.id.main_menu_ibtn);
        msgListIBtn = (ImageButton)findViewById(R.id.main_msg_list_ibtn);
        logoIBtn = (ImageButton)findViewById(R.id.main_logo_ibtn);

        orderLlt = (LinearLayout)findViewById(R.id.llt_order);
        mianqianLlt = (LinearLayout)findViewById(R.id.llt_mianqiantai);
        addLlt     = (LinearLayout) findViewById(R.id.llt_add);
        locationTv = (TextView) findViewById(R.id.tv_location);
        orderInfoTv = (TextView)findViewById(R.id.tv_book_info);
        orderStatusTv1 = (TextView)findViewById(R.id.tv_order_status);
        msgNumTv = (TextView)findViewById(R.id.main_msg_num_tv);
    }

    private void initData(){
        EventBus.getDefault().register(this);
        MainUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        initDBName();
        initShop();
        initAvatar();
        LocationManager.getInstance().registerLocation(this);
        messageListener = new MessageListener();
        initService(messageListener);
        //设置消息未读个数
        setBadgeNum();
    }

    /**
     * 初始化商家列表信息
     */
    private void initShop() {
        ShopListManager.getInstance().init(this);
    }

    private void setBadgeNum(){
        notifyCount = MessageDBUtil.getInstance().queryNotifyCount();
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

    private void initAvatar() {
        if(CacheUtil.getInstance().getToken() == null){
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                ProtocolUtil.getUserInfoUrl(CacheUtil.getInstance().getToken(),
                        CacheUtil.getInstance().getUserId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.ERROR, "获取用户信息响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                UserInfoResponse userInfoResponse =  new Gson().fromJson(response, UserInfoResponse.class);
                                if(null != userInfoResponse && userInfoResponse.getUserid() != null){

                                    UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                                    if(null != userInfoVo){
                                        String userPhotoSuffix = userInfoVo.getUserAvatar();
                                        if(!TextUtils.isEmpty(userPhotoSuffix)){
                                            String userPhotoUrl = ConfigUtil.getInst().getHttpDomain()+userPhotoSuffix;
                                            //保存头像到本地
                                            CacheUtil.getInstance().saveUserPhotoUrl(userPhotoUrl);
                                            MineUiController.getInstance().setUserPhoto(userPhotoUrl, photoCtv);
                                            MenuLeftFragment leftmenu = (MenuLeftFragment)leftMenuFragment;
                                            leftmenu.setAvatar();
                                        }
                                        CacheUtil.getInstance().saveTagsOpen(userInfoVo.isTagopen());
                                        CacheUtil.getInstance().setUserPhone(userInfoVo.getMobilePhoto());
                                        CacheUtil.getInstance().setUserName(userInfoVo.getUsername());
                                    }
                                }else if(!userInfoResponse.isSet()){
                                    CacheUtil.getInstance().setLogin(false);
                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.slide_out_left);
                                    finish();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息错误信息:" +  error.getMessage());
            }
        });
        LogUtil.getInstance().info(LogLevel.ERROR, "获取用户信息:" + stringRequest.toString());
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    private void initMenu() {
        slidingMenu = new SlidingMenu(this);
        leftMenuFragment = new MenuLeftFragment();
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

    public void toggleMenu(){
        slidingMenu.toggle();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
    }

    protected  void onResume(){
        super.onResume();
        MineUiController.getInstance().setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), photoCtv);
        setBadgeNum();
        loadLastOrderInfo();
    }

    //加载最近的一条订单信息
    private void loadLastOrderInfo(){
        createLoadLastOrderListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.getLastOrder(), loadOrderListener, loadOrderErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                LogUtil.getInstance().info(LogLevel.ERROR, map.toString());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        DialogUtil.getInstance().showProgressDialog(this);
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void createLoadLastOrderListener() {
        loadOrderListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogUtil.getInstance().cancelProgressDialog();
                if(!TextUtils.isEmpty(response)){
                    try {
                        LogUtil.getInstance().info(LogLevel.ERROR, "public void onResponse:\n"+response);

                        lastOrderInfo = new Gson().fromJson(response, OrderLastResponse.class);
                        if(lastOrderInfo != null && !lastOrderInfo.isSet()){
                            CacheUtil.getInstance().setLogin(false);
                            DialogUtil.getInstance().showToast(MainActivity.this,"token 过期，请重新登录");
                            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(mainIntent);
                            MainActivity.this.finish();
                            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                            return;
                        }
                        if(lastOrderInfo != null && lastOrderInfo.getUserid() == null){
                            lastOrderInfo = null;
                            return;
                        }
                        if(lastOrderInfo != null && lastOrderInfo.getReservation_no().equals("0")){
                            lastOrderInfo = null;
                            return;
                        }
                        // lastOrderInfo.setShopid("120");
                        String roomType = lastOrderInfo.getRoom_type();
                        String roomRate =  lastOrderInfo.getRoom_rate();
                        String arriveDate = lastOrderInfo.getArrival_date();
                        String orderStatusStr = lastOrderInfo.getStatus();
                        String shopId = lastOrderInfo.getShopid();
                        if (!TextUtils.isEmpty(orderStatusStr)) {
                            CacheUtil.getInstance().setOrderStatus(shopId, Integer.parseInt(orderStatusStr));
                        }
                        String arriveDateStr = "";
                        SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");
                        try {
                            Date date = mSimpleFormat.parse(arriveDate);
                            arriveDateStr = mChineseFormat.format(date);
                        }catch (Exception e){
                        }
                        orderInfoTv.setVisibility(View.VISIBLE);
                        orderInfoTv.setText("" + roomType + "  |  " + arriveDateStr + "  |  ￥" + roomRate);
                        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //register error listener
        loadOrderErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                DialogUtil.getInstance().cancelProgressDialog();
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.INFO, "获取最近订单失败。" + volleyError.toString());
                orderInfoTv.setVisibility(View.GONE);
                lastOrderInfo = null;
            }
        };
    }

    private void initListeners() {

        //头像
        photoCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //动态图片背景
        kbv.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });

        //左菜单
        menuIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showMenu();
            }
        });

        //右菜单消息列表
        msgListIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showSecondaryMenu();
            }
        });

        logoIBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // LogUtil.getInstance().info(LogLevel.DEBUG," public boolean onLongClick(View view) " + lastShopInfo.getPhone());
                // && StringUtil.isPhoneNumber(lastShopInfo.getPhone())
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
        logoIBtn.setOnClickListener(new View.OnClickListener() {
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

        //中间消息提示布局
        orderLlt.setOnClickListener(new View.OnClickListener() {
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
                            //intent = new Intent(MainActivity.this, GoodListActivity.class);
                            // intent.putExtra("shopid", svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1).getiBeacon().getShopid());

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

        //添加
        addLlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //免前台
        mianqianLlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("webview_url","http://iwxy.cc/mqt/");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
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
            locationTv.setText("不在酒店");
            lastShopId = "";

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
        switch (getMainTextStatus()){
            //其他状态
            case DEFAULT_NULL:
                orderStatusTv1.setText("如有疑问，请和客服联系!");
                break;
            //没订单，不在酒店
            case NO_ORDER_NOT_IN:
                orderStatusTv1.setText("您没有任何预订信息 立即预订");
                setDrawableLeft(orderStatusTv1,R.drawable.sl_wu);
                break;
            //没订单，在酒店
            case NO_ORDER_IN:
                int index = svipApplication.mRegionList.size()-1;
                String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                String fullname = ShopListManager.getInstance().getShopName(shopid);
                orderStatusTv1.setText(fullname+"欢迎您，点击马上预订酒店");
                setDrawableLeft(orderStatusTv1, R.drawable.sl_dengdai);
                break;
            //有预定状态订单，不在酒店
            case BOOKING_NOT_IN:
                orderStatusTv1.setText("订单已提交，请等待酒店确定");
                setDrawableLeft(orderStatusTv1, R.drawable.sl_tijiao);
                break;
            //有预定状态订单，在酒店
            case BOOKING_IN:
                orderStatusTv1.setText("订单已提交，请等待酒店确定");
                setDrawableLeft(orderStatusTv1, R.drawable.sl_tijiao);
                break;
            //有确认状态的订单，在酒店，有免前台特权
            case SURE_IN_HAVE_NOLOGIN:
                orderStatusTv1.setText("到达酒店，将为您办理入住手续");
                setDrawableLeft(orderStatusTv1, R.drawable.ic_zhuanshuqiantai);
                break;
            //有确认状态的订单，在酒店，没有免前台特权
            case SURE_IN_NOT_NOLOGIN:
                orderStatusTv1.setText("到达酒店，请办理入住手续");
                setDrawableLeft(orderStatusTv1, R.drawable.ic_zhuanshuqiantai);
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
                setDrawableLeft(orderStatusTv1, R.drawable.sl_zhuyi);
                break;
            //有已经入住订单，在酒店
            case CHECKIN_IN:
                String locdesc = svipApplication.mRegionList.get(svipApplication.mRegionList.size()-1).getiBeacon().getLocdesc();
                orderStatusTv1.setText("您到达" + locdesc + "," + lastOrderInfo.getFullname() + "随时为您服务！");
                setDrawableLeft(orderStatusTv1, R.drawable.sl_yuding);
                Calendar todayC2 = Calendar.getInstance();
                Date today2 = todayC2.getTime();
                SimpleDateFormat mSimpleFormat2  = new SimpleDateFormat("yyyy-MM-dd");
                Date departureDay = mSimpleFormat2.parse(lastOrderInfo.getDeparture_date());
                int offsetDay2 = TimeUtil.daysBetween(today2,departureDay);
                if(offsetDay2 == 0){
                    orderStatusTv1.setText("您今天需要退房，旅途愉快!");
                    setDrawableLeft(orderStatusTv1, R.drawable.sl_tuifang);
                }
                break;
            //有已经入住订单，不在酒店
            case CHECKIN_NOT_IN:
                orderStatusTv1.setText(lastOrderInfo.getFullname() + "随时为您服务！");
                setDrawableLeft(orderStatusTv1, R.drawable.sl_likai);
                break;
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
     * 设定textView 的 DrawableLeft 属性
     */
    private void setDrawableLeft(TextView myTextview,int resoundId){

        Drawable drawable= getResources().getDrawable(resoundId);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        myTextview.setCompoundDrawables(drawable,null,null,null);

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
