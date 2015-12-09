package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainUiController;
import com.zkjinshi.svip.activity.common.SettingActivity;
import com.zkjinshi.svip.activity.mine.PrivilegeActivity;
import com.zkjinshi.svip.activity.order.ConsumeRecordActivtiy;
import com.zkjinshi.svip.activity.order.HistoryOrderActivtiy;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.view.CircleImageView;

import java.util.HashMap;

/**
 * 首页Fragment
 */
public class SetFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = SetFragment.class.getSimpleName();
    private View mView;
    private CircleImageView photoCtv;
    private TextView nameTv;
    private TextView vipTv,setCodeTv;
    private Activity mActivity;
    private ImageView homePicIv;


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

    }

    public void onResume(){
        super.onResume();
        Animation bigAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bigger);
        homePicIv.startAnimation(bigAnimation);
        initData();
    }

    private void initView(LayoutInflater inflater, ViewGroup container)
    {
        mView = inflater.inflate(R.layout.fragment_set, container, false);

        photoCtv = (CircleImageView)mView.findViewById(R.id.leftmenu_user_photo_civ);
        nameTv = (TextView)mView.findViewById(R.id.leftmenu_user_name);
        vipTv = (TextView)mView.findViewById(R.id.level_tv);
        setCodeTv = (TextView)mView.findViewById(R.id.set_code_tv);

        homePicIv = (ImageView)mView.findViewById(R.id.home_pic_iv);


        photoCtv.setOnClickListener(this);
        setCodeTv.setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_set_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_front_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_order_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_footprint_tv).setOnClickListener(this);

    }

    private void initData(){
        setAvatar();
        nameTv.setText(CacheUtil.getInstance().getUserName());
        vipTv.setText("VIP "+CacheUtil.getInstance().getUserApplevel());
        checktActivate();
    }

    public void setAvatar(){
        MainUiController.getInstance().init(getActivity());
        String userId = CacheUtil.getInstance().getUserId();
        String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
        MainUiController.getInstance().setUserPhoto(userPhotoUrl, photoCtv);
    }

    /**
     * 判读是否已经激活
     */
    public void checktActivate(){
        if(CacheUtil.getInstance().isActivate()){
            setCodeTv.setVisibility(View.GONE);
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
                        setCodeTv.setVisibility(View.GONE);
                        CacheUtil.getInstance().setActivate(true);
                    } else {
                        setCodeTv.setVisibility(View.VISIBLE);
                        CacheUtil.getInstance().setActivate(false);
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
            //消费记录
            case R.id.leftmenu_footprint_tv:
            {
                Intent intent = new Intent(getActivity(), ConsumeRecordActivtiy.class);
                intent.putExtra("is_order", false);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //立即激活
            case R.id.set_code_tv:
            {
                Intent goInvitesCode = new Intent(mActivity, InviteCodeActivity.class);
                startActivity(goInvitesCode);
            }

        }
    }

}
