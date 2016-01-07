package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.HomeGuideActivity;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.MainController;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.activity.order.ConsumeRecordActivtiy;
import com.zkjinshi.svip.activity.order.GoodListActivity;
import com.zkjinshi.svip.activity.shop.ShopDetailActivity;
import com.zkjinshi.svip.adapter.HomeMsgAdapter;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.MessageDefaultResponse;
import com.zkjinshi.svip.response.PrivilegeResponse;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.CleverDialog;
import com.zkjinshi.svip.vo.HomeMsgVo;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment implements LocationManager.LocationChangeListener{

    public static final String TAG = HomeFragment.class.getSimpleName();

    private final static int LOAD_DEFAULT_MSG= 0x0001;
    private final static int NOTIFY_UPDATE_MAIN_TEXT = 0x0002;
    private final static int REQUEST_ACTIVATE_INVITE_CODE = 0x03;
    private final static int NOTIFY_LOCATION = 0x04;
    private final static int REQUEST_CONSUME_RECORD = 0x05;

    public static String lastLocid = "";
    public Animation fadeAnimation = null;

    private View view = null;
    private View headerView = null;
    private ImageView homePicIv;
    private TextView hiTextTv;
    private TextView nameTv;
    private TextView activeCodeTv;
    private TextView simpleTextTv;
    private TextView logoTextTv;
    private ImageView logoIv;
    private CircleImageView avatarCiv;

    private Activity mActivity;
    public ListView listView;
    public HomeMsgAdapter homeMsgAdapter = null;


    SVIPApplication svipApplication;
    private OrderLastResponse lastOrderInfo = null;
    private CleverDialog cleverDialog = null;
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
                case LOAD_DEFAULT_MSG:
                    getMessageDefault(false);
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

                }
                    break;
            }
        }
    };


    private void initView(LayoutInflater inflater, ViewGroup container){
        svipApplication = (SVIPApplication)getActivity().getApplication();
        view = inflater.inflate(R.layout.fragment_home,container,false);

        listView = (ListView) view.findViewById(R.id.listview);

        if(cleverDialog == null){
            cleverDialog = new CleverDialog(getActivity());
        }

        if(headerView == null){
            headerView = inflater.inflate(R.layout.item_home_header,null,false);
            listView.addHeaderView(headerView);

            homePicIv = (ImageView)headerView.findViewById(R.id.home_pic_iv);
            hiTextTv = (TextView)headerView.findViewById(R.id.hi_text_tv);
            nameTv = (TextView)headerView.findViewById(R.id.name_tv);
            simpleTextTv = (TextView)headerView.findViewById(R.id.simple_text_tv);
            logoTextTv = (TextView) headerView.findViewById(R.id.logo_text);
            logoTextTv.setVisibility(View.GONE);
            logoIv = (ImageView)headerView.findViewById(R.id.logo_iv);
            avatarCiv = (CircleImageView)headerView.findViewById(R.id.avatar_civ);
            activeCodeTv = (TextView)headerView.findViewById(R.id.active_code_tv);
        }

        homeMsgList = new ArrayList<HomeMsgVo>();
        homeMsgAdapter = new HomeMsgAdapter(homeMsgList,getActivity());
        listView.setAdapter(homeMsgAdapter);


    }

    private void initData(){

    }

    //加载本地预设信息
    private void addLlocalDefaultHomeMsg(){
        HomeMsgVo welcomeMsg= new HomeMsgVo();
        welcomeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT);
        welcomeMsg.setClickAble(true);
        welcomeMsg.setMajorText("欢迎使用超级身份");
        welcomeMsg.setMinorText("超级身份精选了很多优质服务，您可以直接向商家等服务员沟通.");
        homeMsgList.add(welcomeMsg);
        homeMsgAdapter.setDatalist(homeMsgList);
        homeMsgAdapter.notifyDataSetChanged();
    }

    private void initListeners(){
        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getUserPrivilege();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Toast.makeText(mActivity.getApplicationContext(),"position:"+position,Toast.LENGTH_SHORT).show();
                if(position == 0){
                    return;
                }
                int index = position-1;
                HomeMsgVo homeMsgVo = homeMsgList.get(index);
                switch (homeMsgVo.getMsgType()){
                    case HOME_MSG_DEFAULT:
                    {
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("webview_url","http://www.zkjinshi.com/about_us/about_svip.html");
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }
                    break;
                    case HOME_MSG_LOCATION:
                    {
                        Intent intent = new Intent(mActivity, ShopDetailActivity.class);
                        intent.putExtra("shopId", homeMsgVo.getShopid());
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }
                    break;
                    case HOME_MSG_ORDER:
                    {
                        Intent intent = new Intent(getActivity(), ConsumeRecordActivtiy.class);
                        getActivity().startActivityForResult(intent, REQUEST_CONSUME_RECORD);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    break;
                }
            }
        });
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
        if(CacheUtil.getInstance().isHomeGuide()){
            Intent intent = new Intent(getActivity(), HomeGuideActivity.class);
            startActivity(intent);
            CacheUtil.getInstance().setHomeGuide(false);
        }
        mActivity = getActivity();
        LocationManager.getInstance().registerLocation(mActivity);
        LocationManager.getInstance().setLocationChangeListener(this);
        MainController.getInstance().init(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        setBigPicAnimation();
        if(CacheUtil.getInstance().isLogin()){
            checktActivate();
            getOrderMsgUrl();
        }else{
            getMessageDefault(true);
        }
    }

    public void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
        MainController.getInstance().pauseBigPicAnimation();
    }

    @Override
    public void onStop() {
        super.onStop();
        ImageLoader.getInstance().clearMemoryCache();
    }

    public void setBigPicAnimation(){
        MainController.getInstance().setBigPicAnimation(homePicIv);
    }





    /**
     * 设置首页大图区信息
     */
    public void setBigPicZone(){
        //设置头像

        if(CacheUtil.getInstance().isLogin() && !TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            String userId = CacheUtil.getInstance().getUserId();
            String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
            MainController.getInstance().setPhoto(userPhotoUrl,avatarCiv);
        }

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

            nameTv.setText("立即登录");
            nameTv.setTextColor(Color.parseColor("#ffc56e"));
            nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goLogin();
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
                    String fullname =svipApplication.mRegionList.get(index).getiBeacon().getRemark();
                   // String fullname = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
                    //simpleTextTv.setText("欢迎光临"+fullname);
                    simpleTextTv.setText("");
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
        Intent intent = new Intent(getActivity(),LoginActivity.class);
        intent.putExtra("isHomeBack",true);
        getActivity().startActivity(intent);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取位置信息
        geoLat = aMapLocation.getLatitude();//纬度
        geoLng = aMapLocation.getLongitude();//经度
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图返回位置信息：("+geoLat+","+geoLng+")");

        city = aMapLocation.getCity();
        city = city.replace("市","");
        if(!TextUtils.isEmpty(city)){
            CacheUtil.getInstance().saveCurrentCity(city);
        }
        handler.sendEmptyMessage(NOTIFY_LOCATION);
    }



    //获取推荐商家列表
//    private void getShopRecommended(String city){
//        String url = ProtocolUtil.getShopRecommendedUrl(city);
//        Log.i(TAG, url);
//        NetRequest netRequest = new NetRequest(url);
//        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
//        netRequestTask.methodType = MethodType.GET;
//        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
//            @Override
//            public void onNetworkRequestError(int errorCode, String errorMessage) {
//                Log.i(TAG, "errorCode:" + errorCode);
//                Log.i(TAG, "errorMessage:" + errorMessage);
//            }
//
//            @Override
//            public void onNetworkRequestCancelled() {
//
//            }
//
//            @Override
//            public void onNetworkResponseSucceed(NetResponse result) {
//                super.onNetworkResponseSucceed(result);
//                Log.i(TAG, "result.rawResult:" + result.rawResult);
//                try {
//                    Type listType = new TypeToken<ArrayList<ShopRecommendedResponse>>() {}.getType();
//                    ArrayList<ShopRecommendedResponse> messageList = new Gson().fromJson(result.rawResult, listType);
//
//                    for(ShopRecommendedResponse message : messageList){
//
//                        HomeMsgVo homeMsg= new HomeMsgVo();
//                        homeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION);
//                        homeMsg.setClickAble(true);
//                        homeMsg.setMajorText(message.getRecommend_title());
//                        homeMsg.setMinorText(message.getRecommend_content());
//                        homeMsg.setIcon( ConfigUtil.getInst().getHttpDomain()+message.getRecommend_logo());
//                        homeMsg.setShopid(message.getShopid());
//                        homeMsgList.add(homeMsg);
//
//                    }
//                    homeMsgAdapter.setDatalist(homeMsgList);
//                    homeMsgAdapter.notifyDataSetChanged();
//
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void beforeNetworkRequestStart() {
//
//            }
//        });
//        netRequestTask.isShowLoadingDialog = false;
//        netRequestTask.execute();
//
//    }

    public void showPrivilegeTips(){
        logoTextTv.setVisibility(View.VISIBLE);
        if(fadeAnimation == null){
            fadeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_fade);
            logoTextTv.startAnimation(fadeAnimation);
        }

    }

    public void hidePrivilegeTips(){
        if(fadeAnimation != null){
            fadeAnimation.cancel();
            fadeAnimation = null;
        }
        logoTextTv.clearAnimation();
        logoTextTv.setVisibility(View.GONE);
    }

    public synchronized void notifyIbeacon(){
        if(!CacheUtil.getInstance().isLogin()){
            logoIv.setVisibility(View.VISIBLE);
            return;
        }else{
            logoIv.setVisibility(View.GONE);
        }
        if(!CacheUtil.getInstance().isActivate()){
            return;
        }
        if(svipApplication.mRegionList.size() <= 0){
            lastLocid = "";
            hidePrivilegeTips();
            return;
        }
        int index = svipApplication.mRegionList.size()-1;
        final String locid = svipApplication.mRegionList.get(index).getiBeacon().getLocid();
        if(locid.equals(lastLocid) && !TextUtils.isEmpty(lastLocid)){
            hidePrivilegeTips();
            return;
        }
        showPrivilegeTips();
    }

    //根据酒店区域获取用户特权
    private void getUserPrivilege(){
        if(!CacheUtil.getInstance().isLogin()){
            return;
        }
        if(!CacheUtil.getInstance().isActivate()){
            return;
        }
        if(svipApplication.mRegionList.size() <= 0){
            return;
        }
        int index = svipApplication.mRegionList.size()-1;
        final String shopid = svipApplication.mRegionList.get(index).getiBeacon().getShopid();
        final String locid = svipApplication.mRegionList.get(index).getiBeacon().getLocid();

        if(locid.equals(lastLocid)){
            return;
        }
        lastLocid = locid;
        hidePrivilegeTips();
        String url = ProtocolUtil.getUserPrivilegeUrl(shopid,locid);
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
                    PrivilegeResponse privilegeResponse = new Gson().fromJson(result.rawResult, PrivilegeResponse.class);
                    if(cleverDialog != null){
                        cleverDialog.show();
                        cleverDialog.setPrivilegeResponse(privilegeResponse);
                        cleverDialog.setContentText(null,privilegeResponse.getShopName(),
                                privilegeResponse.getPrivilegeName(),
                                privilegeResponse.getPrivilegeDesc(),
                                privilegeResponse.getPrivilegeIcon()
                        );
                        cleverDialog.setPrivilegeListener(new CleverDialog.PrivilegeListener() {
                            @Override
                            public void callback(PrivilegeResponse privilegeResponse) {
                                HomeMsgVo homeMsg= new HomeMsgVo();
                                homeMsg.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_PRIVILEDGE);
                                homeMsg.setClickAble(false);
                                homeMsg.setMajorText(privilegeResponse.getMessageCard().getTitle());
                                homeMsg.setMinorText(privilegeResponse.getMessageCard().getContent());
                                homeMsgList.add(0,homeMsg);
                                homeMsgAdapter.notifyDataSetChanged();
                            }
                        });

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


    //获取用户推送消息(用户未登陆)
    private synchronized void getMessageDefault(final boolean isClear){
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
                addLlocalDefaultHomeMsg();
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
                    if(isClear){
                        homeMsgList.clear();
                    }
                    for(MessageDefaultResponse message : messageList){

                        HomeMsgVo homeMsgVo= new HomeMsgVo();
                        if(TextUtils.isEmpty(message.getShopid())){
                            homeMsgVo.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_DEFAULT);
                            homeMsgVo.setClickAble(true);
                        }else{
                            homeMsgVo.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_LOCATION);
                            homeMsgVo.setShopid(message.getShopid());
                            homeMsgVo.setClickAble(true);
                        }

                        homeMsgVo.setMajorText(message.getTitle());
                        homeMsgVo.setMinorText(message.getDesc());
                        homeMsgVo.setIcon(ProtocolUtil.getFitPicUrl(message.getIconbaseurl(),message.getIconfilename()));
                        homeMsgList.add(homeMsgVo);


                    }
                    homeMsgAdapter.setDatalist(homeMsgList);
                    homeMsgAdapter.notifyDataSetChanged();

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


    //获取用户订单状态消息
    private synchronized void getOrderMsgUrl(){
        String url = ProtocolUtil.getOrderMsgUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                handler.sendEmptyMessage(LOAD_DEFAULT_MSG);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    homeMsgList.clear();
                    Type listType = new TypeToken<ArrayList<MessageDefaultResponse>>() {}.getType();
                    ArrayList<MessageDefaultResponse> messageList = new Gson().fromJson(result.rawResult, listType);
                    for(MessageDefaultResponse message : messageList){

                        HomeMsgVo homeMsgVo= new HomeMsgVo();
                        homeMsgVo.setMsgType(HomeMsgVo.HomeMsgType.HOME_MSG_ORDER);
                        homeMsgVo.setClickAble(true);
                        homeMsgVo.setMajorText(message.getTitle());
                        homeMsgVo.setMinorText(message.getDesc());
                        homeMsgVo.setIcon(ProtocolUtil.getFitPicUrl(message.getIconbaseurl(),message.getIconfilename()));
                        homeMsgVo.setOrderNo(message.getOrderNo());
                        homeMsgList.add(homeMsgVo);

                    }
                    homeMsgAdapter.setDatalist(homeMsgList);
                    homeMsgAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessage(LOAD_DEFAULT_MSG);

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

    /**
     * 判读是否已经激活
     */
    public void checktActivate(){
        if(!CacheUtil.getInstance().isLogin()){
            return;
        }
        if(CacheUtil.getInstance().isActivate()){
            setBigPicZone();
            notifyIbeacon();
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
                    notifyIbeacon();
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
