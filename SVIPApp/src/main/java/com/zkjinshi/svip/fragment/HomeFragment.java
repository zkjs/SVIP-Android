package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.city.citylist.CityListActivity;
import com.zkjinshi.svip.activity.common.ContactActivity;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.MainController;
import com.zkjinshi.svip.activity.order.HistoryOrderActivtiy;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;
import com.zkjinshi.svip.activity.order.OrderEvaluateActivity;
import com.zkjinshi.svip.adapter.HomeAdapter;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BigPicResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.response.HomeMsgResponse;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.response.OrderRoomResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.MapUtil;
import com.zkjinshi.svip.utils.OrderUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RecyclerWrapAdapter;
import com.zkjinshi.svip.view.ServerDialog;
import com.zkjinshi.svip.view.WrapRecyclerView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment implements LocationManager.LocationChangeListener{

    public static final String TAG = HomeFragment.class.getSimpleName();

    private final static int NOTIFY_UPDATE_VIEW = 0x0001;
    private final static int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;

    private View view = null;
    private View headerView = null;
    private ImageView homePicIv;
    private TextView hiTextTv;
    private TextView nameTv;
    private TextView activeCodeTv;
    private TextView simpleTextTv;
    private ImageView logoIv;

    private Activity mActivity;
    public WrapRecyclerView wrapRecyclerView;
    public RecyclerWrapAdapter recyclerWrapAdapter;
    public HomeAdapter homeAdapter = null;


    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;
    public static double geoLat = 100;
    public static double geoLng;
    private String bestHotelId="120";
    private String bestServerid="555711167a31a";
    private ArrayList<HomeMsgResponse> homeMsgList;
    private String city = "";



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
                    if(mActivity instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity)mActivity;
                        mainActivity.lastOrderInfo = lastOrderInfo;
                    }
                    changeMainText();
                    break;
            }
        }
    };


    private void initView(LayoutInflater inflater, ViewGroup container){
        svipApplication = (SVIPApplication)getActivity().getApplication();
        view = inflater.inflate(R.layout.fragment_home,container,false);

        wrapRecyclerView = (WrapRecyclerView) view.findViewById(R.id.wrapRecyclerView);
        wrapRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        wrapRecyclerView.setLayoutManager(mLayoutManager);

        if(headerView == null){
            headerView = inflater.inflate(R.layout.item_home_header,null,false);
            wrapRecyclerView.addHeaderView(headerView);

            homePicIv = (ImageView)headerView.findViewById(R.id.home_pic_iv);
            hiTextTv = (TextView)headerView.findViewById(R.id.hi_text_tv);
            nameTv = (TextView)headerView.findViewById(R.id.name_tv);
            simpleTextTv = (TextView)headerView.findViewById(R.id.simple_text_tv);
            logoIv = (ImageView)headerView.findViewById(R.id.logo_iv);
            activeCodeTv = (TextView)headerView.findViewById(R.id.active_code_tv);
        }

        homeMsgList = new ArrayList<HomeMsgResponse>();
        homeAdapter = new HomeAdapter(homeMsgList,getActivity());
        wrapRecyclerView.setAdapter(homeAdapter);

    }

    private void initData(){
        homeMsgList.clear();
        HomeMsgResponse welcomeMsg= new HomeMsgResponse();
        welcomeMsg.setMsgType(HomeMsgResponse.HomeMsgType.HOME_MSG_DEFAULT);
        welcomeMsg.setClickAble(false);
        welcomeMsg.setMajorText("欢迎使用超级服务");
        welcomeMsg.setMinorText("超级身份精选了很多优质服务，您可以直接向商家等服务员沟通.");
        homeMsgList.add(welcomeMsg);

        welcomeMsg= new HomeMsgResponse();
        welcomeMsg.setMsgType(HomeMsgResponse.HomeMsgType.HOME_MSG_DEFAULT);
        welcomeMsg.setClickAble(true);
        welcomeMsg.setMajorText("我们为您精心推荐了本地服务");
        welcomeMsg.setMinorText("限量KTV到店服务");
        homeMsgList.add(welcomeMsg);

        homeAdapter.notifyItemInserted(0);

    }

    private void initListeners(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == mActivity.RESULT_OK){
            if(requestCode == ShopFragment.REQUEST_CHOOSE_CITY){
                if(null != data){
                    String city = data.getStringExtra("city");
                }
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        LocationManager.getInstance().registerLocation(mActivity);
        LocationManager.getInstance().setLocationChangeListener(this);
        MainController.getInstance().init(mActivity);
        if (view == null)
        {
            initView(inflater, container);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initListeners();

    }

    @Override
    public void onResume() {
        super.onResume();
        //首页大图区
        setBigPicZone();
        loadLastOrderInfo();
        checktActivate();
        setBigPicAnimation();
    }

    public void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
        MainController.getInstance().pauseBigPicAnimation();
    }

    public void setBigPicAnimation(){
        MainController.getInstance().setBigPicAnimation(homePicIv);
    }





    /**
     * 设置首页大图区信息
     */
    public void setBigPicZone(){
        //设置时间问候语
        String welcomeText = "";
        int hour =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour >= 0 && hour < 9){
            welcomeText = "早上好";
        }else if(hour >= 9 && hour < 11){
            welcomeText = "上午好";
        }else if(hour >= 11 && hour < 13){
            welcomeText = "中午好";
        }else if(hour >= 13 && hour < 18){
            welcomeText = "下午好";
        }else if(hour >= 18&& hour < 24){
            welcomeText = "晚上好";
        }

        hiTextTv.setText(welcomeText);

        //动态提示语

        //用户未登陆
        if(!CacheUtil.getInstance().isLogin()){

            nameTv.setText("立即登陆");
            nameTv.setTextColor(Color.parseColor("#ffc56e"));
            nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goLogin();
                    mActivity.finish();
                }
            });
            simpleTextTv.setText("使用超级身份，享受超凡个性服务");
            activeCodeTv.setVisibility(View.GONE);

        }else{//用户已经登录
            String nameStr = "";
            if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
                nameStr = CacheUtil.getInstance().getUserName();
            }
            if(CacheUtil.getInstance().getSex().equals("0")){
                nameStr = nameStr + "  女士";
            }else{
                nameStr = nameStr + "  先生";
            }
            nameTv.setText(nameStr);
            nameTv.setTextColor(Color.parseColor("#ffffff"));
            nameTv.setOnClickListener(null);

                //用户已经激活
            if(CacheUtil.getInstance().isActivate()){
                activeCodeTv.setVisibility(View.GONE);
                simpleTextTv.setText("使用超级身份，享受超凡个性服务");
                //到店
                if(svipApplication.mRegionList.size() > 0){
                    int index = svipApplication.mRegionList.size()-1;
                    final String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                    String fullname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
                    simpleTextTv.setText("欢迎光临"+fullname);
                }
            }else{
                //用户未激活
                activeCodeTv.setVisibility(View.VISIBLE);
                activeCodeTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goInvitesCode = new Intent(mActivity, InviteCodeActivity.class);
                        mActivity.startActivity(goInvitesCode);
                        mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                simpleTextTv.setText("输入邀请码激活身份，享受超凡个性服务");

            }
        }


    }

    /**
     * 跳转到登录页面
     */
    private void goLogin() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取位置信息
        geoLat = aMapLocation.getLatitude();//纬度
        geoLng = aMapLocation.getLongitude();//经度
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图返回位置信息：("+geoLat+","+geoLng+")");

        city = aMapLocation.getCity();
        city = city.replace("市","");
        notifyMainTextChange();

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
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
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
                    if (lastOrderInfo != null && lastOrderInfo.getReservation_no().equals("0")) {
                        lastOrderInfo = null;
                        //lastOrderLayout.setVisibility(View.GONE);
                        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                        return;
                    }
                    //lastOrderLayout.setVisibility(View.VISIBLE);
                    initOrderView(lastOrderInfo);

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

    public void initOrderView(OrderLastResponse lastOrderInfo){
        try{
            String roomType = lastOrderInfo.getRoom_type();
            String roomRate = lastOrderInfo.getRoom_rate();
            String arriveDate = lastOrderInfo.getArrival_date();
            String departureDate = lastOrderInfo.getDeparture_date();
            String orderStatusStr = lastOrderInfo.getStatus();
            String shopId = lastOrderInfo.getShopid();
            if (!TextUtils.isEmpty(orderStatusStr)) {
                CacheUtil.getInstance().setOrderStatus(shopId, Integer.parseInt(orderStatusStr));
            }
            //orderShopNameTv.setText(lastOrderInfo.getFullname());

            String arriveDateStr = "";
            int dayNum = 0;
            SimpleDateFormat mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");

            Date date = mSimpleFormat.parse(arriveDate);
            arriveDateStr = mChineseFormat.format(date);
            dayNum = TimeUtil.daysBetween(arriveDate, departureDate);

            //orderDetailTv.setText("" + roomType + "  |  " + arriveDateStr + "入住  |  " + dayNum + "晚" + "  |  ￥" + roomRate);
            handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void notifyMainTextChange(){
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
    }






    /**
     * 改变主语句
     */
    public synchronized void changeMainText(){

        try {
            showWelcomeText();
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

    //智能选择客服
    public void loadCleverServer(final String shopid){
        CustomerServicesManager.getInstance().requestServiceListTask(getActivity(),shopid , new ExtNetRequestListener(getActivity()) {
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
                                showServerDailog(shopid,customerService);
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

    public void showServerDailog(String shopId,CustomerServiceBean customerServiceBean){
        ServerDialog serverDialog = new ServerDialog(getActivity());
        serverDialog.shopId = shopId;
        serverDialog.customerService = customerServiceBean;
        serverDialog.shopName = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId);
        serverDialog.show();
    }

    /**
     * 设置欢迎语句
     */
    public void showWelcomeText(){
//        //未激活
//        if(!CacheUtil.getInstance().isActivate()){
//            simpleTextTv.setText("超级身份精选了很多优质服务，您可以直接向服务员预定沟通，尊享个性服务。");
//            codeTextTv.setText("您的VIP身份尚未激活");
//            codeClickTv.setText("立即激活");
//            codeClickTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent goInvitesCode = new Intent(mActivity, InviteCodeActivity.class);
//                    startActivityForResult(goInvitesCode, REQUEST_ACTIVATE_INVITE_CODE);
//                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                }
//            });
//            return;
//        }
//        //已经激活
//        String welcomeText = "";
//       int hour =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        if(hour > 6 && hour < 12){
//            welcomeText = "上午好";
//        }else if(hour > 12 && hour < 18){
//            welcomeText = "下午好";
//        }else{
//            welcomeText = "晚上好";
//        }
//        //有ibeacome 没订单
//        if(lastOrderInfo == null && svipApplication.mRegionList.size() > 0){
//            int index = svipApplication.mRegionList.size()-1;
//            final String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
//            String fullname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
//
//            simpleTextTv.setText("超级身份能与客服沟通，为你开启全新预定方式。");
//            codeTextTv.setText(fullname+"欢迎您");
//            codeClickTv.setText("立即预定");
//            codeClickTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadCleverServer(shopid);
//                }
//            });
//
//        }else if(checkIfInOrderShop()){
//            //有订单在酒店
//            simpleTextTv.setText("超级身份精选了很多优质服务，您可以直接向服务员预定沟通，尊享个性服务。");
//            codeTextTv.setText(lastOrderInfo.getFullname()+"欢迎您");
//            codeClickTv.setText("立即查看");
//            codeClickTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), HistoryOrderActivtiy.class);
//                    intent.putExtra("is_order", true);
//                    getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.slide_in_right,
//                            R.anim.slide_out_left);
//                }
//            });
//
//        }else if(lastOrderInfo != null){
//            //有订单 没ibeacome
//            simpleTextTv.setText("超级身份精选了很多优质服务，您可以直接向服务员预定沟通，尊享个性服务。");
//            codeTextTv.setText(welcomeText+"，您有1个行程");
//            codeClickTv.setText("立即查看");
//            codeClickTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), HistoryOrderActivtiy.class);
//                    intent.putExtra("is_order", true);
//                    getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.slide_in_right,
//                            R.anim.slide_out_left);
//                }
//            });
//
//        } else if(lastOrderInfo == null && svipApplication.mRegionList.size() == 0){
//            //没订单，没ibeacome
//            simpleTextTv.setText("超级身份精选了很多优质服务，您可以直接向服务员预定沟通，尊享个性服务。");
//            codeTextTv.setText(welcomeText+"，您没有任何行程");
//            codeClickTv.setText("开始预定");
//            codeClickTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mActivity instanceof MainActivity){
//                        MainActivity mainActivity = (MainActivity)mActivity;
//                        mainActivity.changTag(R.id.footer_tab_rb_shop);
//                    }
//                }
//            });
//        }
    }

    /**
     * 判读是否已经激活
     */
    public void checktActivate(){
        if(CacheUtil.getInstance().isActivate()){
            setBigPicZone();
            return;
        }
        String url = ProtocolUtil.getUserMysemp();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(mActivity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mActivity) {
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
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if (baseBean.isSet()) {
                        CacheUtil.getInstance().setActivate(true);
                    } else {
                        CacheUtil.getInstance().setActivate(false);
                    }
                    setBigPicZone();
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

}
