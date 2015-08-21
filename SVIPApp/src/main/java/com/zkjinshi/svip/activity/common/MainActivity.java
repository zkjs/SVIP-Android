package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.activity.order.ShopListActivity;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.fragment.MenuRightFragment;
import com.zkjinshi.svip.ibeacon.IBeaconController;
import com.zkjinshi.svip.ibeacon.IBeaconObserver;
import com.zkjinshi.svip.ibeacon.IBeaconSubject;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.service.IMService;
import com.zkjinshi.svip.service.SocketService;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.kenburnsview.KenBurnsView;
import com.zkjinshi.svip.view.kenburnsview.Transition;
import com.zkjinshi.svip.vo.UserInfoVo;

public class MainActivity extends FragmentActivity implements IBeaconObserver {

    public static final String TAG = "MainActivity";

    private KenBurnsView kbv;
    private CircleImageView photoCtv;
    private ImageButton menuIbtn,msgListIBtn,logoIBtn;
    private ImageView addHotelIv;
    private LinearLayout msgNotifyLayout;
    private SlidingMenu slidingMenu;


    private void initView(){

        initMenu();
        kbv  = new KenBurnsView(this);
        photoCtv = (CircleImageView)findViewById(R.id.main_user_photo_civ);
        menuIbtn = (ImageButton)findViewById(R.id.main_menu_ibtn);
        msgListIBtn = (ImageButton)findViewById(R.id.main_msg_list_ibtn);
        logoIBtn = (ImageButton)findViewById(R.id.main_logo_ibtn);
        addHotelIv = (ImageView)findViewById(R.id.main_refresh_ibtn);
        msgNotifyLayout = (LinearLayout)findViewById(R.id.main_center_notify_msg_layout);
    }

    private void initData(){
        MainUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        //MainUiController.getInstance().setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), photoCtv);
        initAvatar();
        initDBName();
        LocationManager.getInstance().registerLocation(this);

        initSocketService();
        initIMService();
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
                        LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                UserInfoResponse userInfoResponse =  new Gson().fromJson(response, UserInfoResponse.class);
                                if(null != userInfoResponse){
                                    UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                                    if(null != userInfoVo){
                                        String userPhotoSuffix = userInfoVo.getUserAvatar();
                                        if(!TextUtils.isEmpty(userPhotoSuffix)){
                                            String userPhotoUrl = ConfigUtil.getInst().getHttpDomain()+userPhotoSuffix;
                                            CacheUtil.getInstance().saveUserPhotoUrl(userPhotoUrl);
                                            MineUiController.getInstance().setUserPhoto(userPhotoUrl,photoCtv);
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();;
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
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    private void initMenu(){
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

    public void toggleMenu(){
        slidingMenu.toggle();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.getInstance().removeLocation();
    }

    private void initListeners(){

        //动态图片背景
        kbv.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });



        //足迹
        menuIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showMenu();
            }
        });

        //消息列表
        msgListIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showSecondaryMenu();
            }
        });

        //智能键
        logoIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //聊天页面

            }
        });

        //添加
        addHotelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //中间消息提示布局
        msgNotifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
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
    }

    @Override
    public void outRegin(RegionVo regionVo) {
        LogUtil.getInstance().info(LogLevel.ERROR,"--欢迎下次光临-----");
        LogUtil.getInstance().info(LogLevel.ERROR,"inTime:"+regionVo.getInTime());
        LogUtil.getInstance().info(LogLevel.ERROR,"outTime:"+regionVo.getOutTime());
        LogUtil.getInstance().info(LogLevel.ERROR,"standTime:"+regionVo.getStandTime());
        LogUtil.getInstance().info(LogLevel.ERROR,"beacon info:"+regionVo.getiBeacon().toString());
        LogUtil.getInstance().info(LogLevel.ERROR,"---------------------");
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
     * 初始化SocketService
     */
    private void initSocketService() {
        Intent socketService = new Intent(getApplicationContext(), SocketService.class);
        startService(socketService);
    }

    /**
     * 初始化IMService用于聊天
     */
    private void initIMService() {
        Intent imService = new Intent(getApplicationContext(), IMService.class);
        startService(imService);
    }

}
