package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.MainController;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.MapUtil;
import com.zkjinshi.svip.utils.OrderUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ListenerDialog;
import com.zkjinshi.svip.vo.UserDetailVo;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    public static final int NOTIFY_UPDATE_VIEW = 0x0001;
    public static final int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;

    private View view = null;
    private ImageView homePicIv;
    private TextView nameTv;
    private ImageView orderShopIconIv;
    private TextView orderShopNameTv;
    private TextView orderTipsTv;
    private TextView orderDetailTv,simpleTextTv;
    private View lastOrderLayout;
    private Activity mActivity;
    private View codeLayout;

    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;
    public static double geoLat = 100;
    public static double geoLng;


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
                    changeMainText();
                    break;
            }
        }
    };


    private void initView(View view){
        svipApplication = (SVIPApplication)getActivity().getApplication();

        nameTv = (TextView)view.findViewById(R.id.name_tv);
        codeLayout = view.findViewById(R.id.code_layout);

        orderShopIconIv = (ImageView)view.findViewById(R.id.order_shop_iv);
        orderShopNameTv = (TextView)view.findViewById(R.id.order_shopname_tv);
        orderTipsTv = (TextView)view.findViewById(R.id.order_tips_tv);
        orderDetailTv = (TextView)view.findViewById(R.id.order_detail_tv);
        lastOrderLayout = view.findViewById(R.id.last_order_layout);
        simpleTextTv = (TextView)view.findViewById(R.id.simple_text_tv);

        homePicIv = (ImageView)view.findViewById(R.id.home_pic_iv);
        Animation bigAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bigger);
        homePicIv.startAnimation(bigAnimation);
    }

    private void initData(){

        String nameStr = "";
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
            nameStr = CacheUtil.getInstance().getUserName();
        }
        if(CacheUtil.getInstance().getSex().equals("0")){
            nameStr = nameStr + "  女士";
        }else{
            nameStr = nameStr + "  男士";
        }
        nameTv.setText(nameStr);

    }

    private void initListeners(){
        //立即激活
        mActivity.findViewById(R.id.code_click_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goInvitesCode = new Intent(mActivity, InviteCodeActivity.class);
                startActivityForResult(goInvitesCode, REQUEST_ACTIVATE_INVITE_CODE);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        if(view == null){
            view = inflater.inflate(R.layout.fragment_home,container,false);
            initView(view);
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
        loadLastOrderInfo();
        checktActivate();
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
                        lastOrderLayout.setVisibility(View.GONE);
                        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
                        return;
                    }
                    lastOrderLayout.setVisibility(View.VISIBLE);
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
           // MainController.getInstance().setPhoto(ProtocolUtil.getShopIconUrl(shopId), orderShopIconIv);
            orderShopNameTv.setText(lastOrderInfo.getFullname());

            String arriveDateStr = "";
            int dayNum = 0;
            SimpleDateFormat mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");

            Date date = mSimpleFormat.parse(arriveDate);
            arriveDateStr = mChineseFormat.format(date);
            dayNum = TimeUtil.daysBetween(arriveDate, departureDate);

            orderDetailTv.setText("" + roomType + "  |  " + arriveDateStr + "入住  |  " + dayNum + "晚" + "  |  ￥" + roomRate);
            handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void notifyMainTextChange(){
        handler.sendEmptyMessage(NOTIFY_UPDATE_MAIN_TEXT);
    }



    /**
     * 改变位置提示信息。
     */
    private void changLocationTips(){
//        geoLat = 22.596328;
//        geoLng = 113.990493;
        if(lastOrderInfo == null && svipApplication.mRegionList.size() > 0){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            simpleTextTv.setText("没订单  "+"在"+region.getiBeacon().getLocdesc());
        }else if(checkIfInOrderShop()){
            RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
            simpleTextTv.setText("有订单  " + "在"+region.getiBeacon().getLocdesc());
        }else if(lastOrderInfo != null && geoLat != 100){
            double shopLat;
            double shopLng;
            shopLat = lastOrderInfo.getMap_latitude();
            shopLng = lastOrderInfo.getMap_longitude();
            double distancedouble =  MapUtil.GetDistance(geoLat,geoLng,shopLat,shopLng);
            LogUtil.getInstance().info(LogLevel.DEBUG,"酒店位置信息("+shopLat+","+shopLng+")");
            DecimalFormat df = new DecimalFormat("###.0");
            LogUtil.getInstance().info(LogLevel.DEBUG,"距离："+distancedouble);
            simpleTextTv.setText("有订单  " +"距离"+df.format(distancedouble)+"KM");
        }else{
            simpleTextTv.setText("超级身份精选了很多优质服务，您可以直接向商家等服务员沟通，让您尊享个性服务。");
        }
    }

    /**
     * 改变中间块提示信息。
     */
    private void changMiddleBlockTips() throws  Exception{
        switch (getMainTextStatus()){
            //其他状态
            case DEFAULT_NULL:
                //majorTv.setText("如有疑问，请和客服联系!");
               // minorTv.setText("长按或点击智键开始快速预定");
                break;
            //没订单，不在酒店
            case NO_ORDER_NOT_IN:
//                majorTv.setText("您没有任何预订信息 立即预订");
//                minorTv.setText("长按或点击智键开始快速预定");
//                shopIcon.setImageResource(R.mipmap.img_avatar_hotel);
                break;
            //没订单，在酒店
            case NO_ORDER_IN:
                int index = svipApplication.mRegionList.size()-1;
                String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
                String fullname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
                orderShopNameTv.setText(fullname);
                orderTipsTv.setText("欢迎光临");
               // MainController.getInstance().setPhoto(ProtocolUtil.getShopIconUrl(shopid), shopIcon);
                break;
            //有预定状态订单，不在酒店
            case BOOKING_NOT_IN:
                orderTipsTv.setText("订单已生成，请尽快确定");
                if(OrderUtil.isOrderTimeOut(lastOrderInfo.getArrival_date())){
                    orderTipsTv.setText("订单已经过期。");
                }
                break;
            //有预定状态订单，在酒店
            case BOOKING_IN:
                orderTipsTv.setText("订单已生成，请确定");
                if(OrderUtil.isOrderTimeOut(lastOrderInfo.getArrival_date())){
                    orderTipsTv.setText("订单已经过期。");
                }
                break;
            //有确认状态的订单，在酒店，有免前台特权
            case SURE_IN_HAVE_NOLOGIN:
                orderTipsTv.setText("到达酒店，将为您办理入住手续");
                break;
            //有确认状态的订单，在酒店，没有免前台特权
            case SURE_IN_NOT_NOLOGIN:
                orderTipsTv.setText("到达酒店，请办理入住手续");
                break;
            //有确认状态订单，不在酒店
            case SURE_NOT_IN:
                Calendar todayC = Calendar.getInstance();
                Date today = todayC.getTime();
                SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivelDay = mSimpleFormat.parse(lastOrderInfo.getArrival_date());
                int offsetDay = TimeUtil.daysBetween(today,arrivelDay);
                if(offsetDay != 0){
                    orderTipsTv.setText(offsetDay+"天后入住酒店");
                    if(offsetDay < 0){
                        orderTipsTv.setText("订单已经过期。");
                    }
                }else{
                    orderTipsTv.setText("今天入住酒店");
                }

                break;
            //有已经入住订单，在酒店
            case CHECKIN_IN:
                String locdesc = svipApplication.mRegionList.get(svipApplication.mRegionList.size()-1).getiBeacon().getLocdesc();
                orderTipsTv.setText("您到达" + locdesc + ","  + "随时为您服务！");

                Calendar todayC2 = Calendar.getInstance();
                Date today2 = todayC2.getTime();
                SimpleDateFormat mSimpleFormat2  = new SimpleDateFormat("yyyy-MM-dd");
                Date departureDay = mSimpleFormat2.parse(lastOrderInfo.getDeparture_date());
                int offsetDay2 = TimeUtil.daysBetween(today2,departureDay);
                if(offsetDay2 == 0){
                    orderTipsTv.setText("您今天需要退房，旅途愉快!");
                }
                break;
            //有已经入住订单，不在酒店
            case CHECKIN_NOT_IN:
                orderTipsTv.setText("随时为您服务！");
                break;
            //订单结束
            case ORDER_FINISH:
                if(lastOrderInfo.getScore() == 0){
                    orderTipsTv.setText("行程结束，请评价");
                }else{
                    orderTipsTv.setText("行程结束，已评价");
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
     * 判读是否已经激活
     */
    public void checktActivate(){
        if(CacheUtil.getInstance().isActivate()){
            codeLayout.setVisibility(View.GONE);
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
                        codeLayout.setVisibility(View.GONE);
                    } else {
                        CacheUtil.getInstance().setActivate(false);
                        codeLayout.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}
