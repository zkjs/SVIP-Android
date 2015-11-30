package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.MainUiController;
import com.zkjinshi.svip.activity.common.SettingActivity;
import com.zkjinshi.svip.activity.common.SettingNologinActivity;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.activity.mine.MineActivity;
import com.zkjinshi.svip.activity.mine.PrivilegeActivity;
import com.zkjinshi.svip.activity.order.ConsumeRecordActivtiy;
import com.zkjinshi.svip.activity.order.HistoryOrderActivtiy;
import com.zkjinshi.svip.activity.order.OrderEvaluateActivity;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.AdPushResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 左侧滑菜单
 * 开发者：JimmyZhang
 * 日期：2015/11/02
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuLeftFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "MenuLeftFragment";
    private View mView;
    private CircleImageView photoCtv;
    private TextView nameTv;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mView == null)
        {
            initView(inflater, container);
        }
        return mView;
    }

    public void onStart()    {
        super.onStart();
        this.mActivity = this.getActivity();
        LogUtil.getInstance().info(LogLevel.DEBUG, "onStart...");
    }

    public void onResume(){
        super.onResume();
        LogUtil.getInstance().info(LogLevel.DEBUG,"onResume...");
        initData();
    }

    private void initView(LayoutInflater inflater, ViewGroup container)
    {
        mView = inflater.inflate(R.layout.left_menu, container, false);

        photoCtv = (CircleImageView)mView.findViewById(R.id.leftmenu_user_photo_civ);
        nameTv = (TextView)mView.findViewById(R.id.leftmenu_user_name);

        photoCtv.setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_set_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_front_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_order_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_footprint_tv).setOnClickListener(this);
        mView.findViewById(R.id.tv_exit).setOnClickListener(this);
    }

    private void initData()
    {
        setAvatar();
    }

    public void setAvatar(){
        MainUiController.getInstance().init(getActivity());
        String userId = CacheUtil.getInstance().getUserId();
        String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
        MainUiController.getInstance().setUserPhoto(userPhotoUrl, photoCtv);
        nameTv.setText(CacheUtil.getInstance().getUserName());
    }

    // 点击事件处理
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            //头像
            case R.id.leftmenu_user_photo_civ:
            {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //设置
            case R.id.leftmenu_set_tv:
            {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //特权
            case R.id.leftmenu_front_tv:
            {
                Intent intent = new Intent(getActivity(), PrivilegeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //订单
            case R.id.leftmenu_order_tv:
            {
                Intent intent = new Intent(getActivity(), HistoryOrderActivtiy.class);
                intent.putExtra("is_order", true);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //足迹
            case R.id.leftmenu_footprint_tv:
            {
                Intent intent = new Intent(getActivity(), ConsumeRecordActivtiy.class);
                intent.putExtra("is_order", false);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;

            //退出
            case R.id.tv_exit:
                final CustomDialog.Builder customerBuilder = new CustomDialog.Builder(mActivity);
                customerBuilder.setTitle(getString(R.string.exit));
                customerBuilder.setMessage(getString(R.string.if_exit_the_current_account_or_not));
                customerBuilder.setGravity(Gravity.CENTER);
                customerBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                customerBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //环信接口退出
                        EasemobIMHelper.getInstance().logout();
                        //http接口推出
                        String userID = CacheUtil.getInstance().getUserId();
                        logoutHttp(userID);
                        //熊推接口推出
                        WebSocketManager.getInstance().logoutIM(VIPContext.getInstance().getContext());
                        //修改登录状态
                        CacheUtil.getInstance().setLogin(false);
                        CacheUtil.getInstance().savePicPath("");
                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().clearMemoryCache();
                        Intent loginActiviy = new Intent(mActivity, LoginActivity.class);
                        startActivity(loginActiviy);
                        mActivity.finish();

                    }
                });
                customerBuilder.create().show();
            break;
        }
    }

    /**
     * 断开用户登录连接
     */
    private void logoutHttp(String userID) {
        String logoutUrl = ProtocolUtil.getLogoutUrl(userID);
        Log.i(TAG, logoutUrl);
        NetRequest netRequest = new NetRequest(logoutUrl);
        NetRequestTask netRequestTask = new NetRequestTask(mActivity, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mActivity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出失败");
            }

            @Override
            public void onNetworkRequestCancelled() {
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                LogUtil.getInstance().info(LogLevel.ERROR, "http退出成功");
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
