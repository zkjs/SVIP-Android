package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.MainController;
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
import com.zkjinshi.svip.response.MessageDefaultResponse;
import com.zkjinshi.svip.response.ShopRecommendedResponse;
import com.zkjinshi.svip.vo.HomeMsgVo;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RecyclerWrapAdapter;
import com.zkjinshi.svip.view.ServerDialog;
import com.zkjinshi.svip.view.WrapRecyclerView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment implements LocationManager.LocationChangeListener{

    public static final String TAG = HomeFragment.class.getSimpleName();

    private final static int NOTIFY_UPDATE_VIEW = 0x0001;
    private final static int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;
    private final static int NOTIFY_LOCATION = 0x04;

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
    private ArrayList<HomeMsgVo> homeMsgList;
    private String city = "";

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
                case NOTIFY_LOCATION:
                {
                    HomeMsgVo homeMsg= new HomeMsgVo();
                    homeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION);
                    homeMsg.setClickAble(true);
                    homeMsg.setMajorText("我们为您精心推荐了本地服务");
                    homeMsg.setMinorText("限量KTV到店服务");
                    homeMsgList.add(homeMsg);
                    homeAdapter.notifyItemInserted(homeMsgList.size()-1);
                }
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

        homeMsgList = new ArrayList<HomeMsgVo>();
        homeAdapter = new HomeAdapter(homeMsgList,getActivity());
        wrapRecyclerView.setAdapter(homeAdapter);

    }

    private void initData(){
        homeMsgList.clear();
        HomeMsgVo welcomeMsg= new HomeMsgVo();
        welcomeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT);
        welcomeMsg.setClickAble(false);
        welcomeMsg.setMajorText("欢迎使用超级服务");
        welcomeMsg.setMinorText("超级身份精选了很多优质服务，您可以直接向商家等服务员沟通.");
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

        getMessageDefault();

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
        handler.sendEmptyMessage(NOTIFY_LOCATION);
    }



    //获取推荐商家列表
    private void getShopRecommended(String city){
        String url = ProtocolUtil.getShopRecommendedUrl(city);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<ArrayList<ShopRecommendedResponse>>() {}.getType();
                    ArrayList<ShopRecommendedResponse> messageList = new Gson().fromJson(result.rawResult, listType);

                    for(ShopRecommendedResponse message : messageList){

                        HomeMsgVo homeMsg= new HomeMsgVo();
                        homeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION);
                        homeMsg.setClickAble(true);
                        homeMsg.setMajorText(message.getRecommend_title());
                        homeMsg.setMinorText(message.getRecommend_content());
                        homeMsg.setIcon( ConfigUtil.getInst().getHttpDomain()+message.getRecommend_logo());
                        homeMsg.setShopid(message.getShopid());
                        homeMsgList.add(homeMsg);

                    }
                    homeAdapter.notifyDataSetChanged();

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


    //获取用户推送消息(用户未登陆)
    private void getMessageDefault(){
        String url = ProtocolUtil.getMessageDefaultUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<ArrayList<MessageDefaultResponse>>() {}.getType();
                    ArrayList<MessageDefaultResponse> messageList = new Gson().fromJson(result.rawResult, listType);
                    if(messageList.size() > 0){
                        homeMsgList.clear();
                    }
                    for(MessageDefaultResponse message : messageList){

                        HomeMsgVo welcomeMsg= new HomeMsgVo();
                        welcomeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT);
                        welcomeMsg.setClickAble(false);
                        welcomeMsg.setMajorText(message.getTitle());
                        welcomeMsg.setMinorText(message.getDesc());
                        welcomeMsg.setIcon(message.getIconbaseurl()+message.getIconfilename());
                        homeMsgList.add(welcomeMsg);

                    }
                    homeAdapter.notifyDataSetChanged();
                    if(TextUtils.isEmpty(city)){
                        city = "深圳";
                    }
                    getShopRecommended(city);


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
