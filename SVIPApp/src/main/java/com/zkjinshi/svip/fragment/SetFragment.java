package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MemberCreditActivity;
import com.zkjinshi.svip.activity.common.SettingActivity;
import com.zkjinshi.svip.activity.mine.SetActivity;
import com.zkjinshi.svip.activity.order.ConsumeRecordActivtiy;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.OrderForDisplay;

import java.util.ArrayList;

/**
 * 设置Fragment
 *
 */
public class SetFragment extends Fragment{

    public static final String TAG = SetFragment.class.getSimpleName();

    public static final int KILL_MYSELT = 1;
    private final static int REQUEST_CONSUME_RECORD = 0x05;

    private RelativeLayout accountInfoLayout,orderManagerLayout,setLayout;
    private RelativeLayout rlCredit;
    private TextView       tvCredit;
    private CircleImageView userPhotoIv;
    private TextView userNameTv,orderNumTv;
    private DisplayImageOptions options;

    private void initView(View view){
        userPhotoIv = (CircleImageView)view.findViewById(R.id.set_uv_user_photo);
        userNameTv = (TextView)view.findViewById(R.id.set_uv_user_name);
        orderNumTv = (TextView)view.findViewById(R.id.set_layout_order_num);
        accountInfoLayout = (RelativeLayout)view.findViewById(R.id.set_layout_user_account_info);
        orderManagerLayout = (RelativeLayout)view.findViewById(R.id.set_layout_order_manager);
        setLayout = (RelativeLayout)view.findViewById(R.id.set_layout_set);
        rlCredit = (RelativeLayout)view.findViewById(R.id.rl_credit);
        tvCredit = (TextView) view.findViewById(R.id.tv_credit_num);

        orderNumTv.setVisibility(View.GONE);
    }

    private void initData(){
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();

        if(CacheUtil.getInstance().isLogin() && !TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            String userId = CacheUtil.getInstance().getUserId();
            String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(userPhotoUrl, userPhotoIv, options);
        }else {
            userPhotoIv.setImageResource(R.mipmap.ic_main_user_default_photo_nor);
        }

        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
            userNameTv.setText(CacheUtil.getInstance().getUserName());
        }else {
            userNameTv.setText("登录/注册");
        }

        if(CacheUtil.getInstance().isLogin()){
            loadOrderNum();
        }else{
            orderNumTv.setVisibility(View.GONE);
        }
    }


    public void loadOrderNum() {
        String url =  ProtocolUtil.orderGetUnconfirmedUrl(CacheUtil.getInstance().getUserId(),1,100);
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
                    ArrayList<OrderForDisplay> bookOrders = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<OrderForDisplay>>(){}.getType());
                    if(null != bookOrders && bookOrders.size() > 0){
                        orderNumTv.setVisibility(View.VISIBLE);
                        orderNumTv.setText(bookOrders.size()+"个待确认订单");
                    } else {
                        orderNumTv.setVisibility(View.GONE);
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

    private void initListeners(){

        //账户信息
        accountInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //订单管理
        orderManagerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), ConsumeRecordActivtiy.class);
                getActivity().startActivityForResult(intent, REQUEST_CONSUME_RECORD);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //我的积分
        rlCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), MemberCreditActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        //设置
        setLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), SetActivity.class);
                getActivity().startActivityForResult(intent,KILL_MYSELT);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
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
        View view = inflater.inflate(R.layout.fragment_set,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

}
