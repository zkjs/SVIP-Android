package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.factory.ShopInfoFactory;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HotelFragment  extends Fragment {
    public static String TAG = HotelFragment.class.getSimpleName();
    public View view = null;

    private ListView shopListView;
    private List<ShopInfoResponse> shopResponseList;
    private List<ShopInfoVo> shopInfoList;
    private ShopAdapter shopAdapter;

    public HotelFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel,null);
        this.view = view;
        Log.i(TAG, "onCreateView");

        initView();
        initData();
        initListeners();
        return view;
    }

    private void initView(){
        shopListView = (ListView)view.findViewById(R.id.shop_list_view);
    }

    private void initData(){
        String url = Constants.GET_SHOP_LIST;
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<List<ShopInfoResponse>>(){}.getType();
                    Gson gson = new Gson();
                    shopResponseList = gson.fromJson(result.rawResult, listType);
                    if(null != shopResponseList && !shopResponseList.isEmpty()){
                        shopInfoList = ShopInfoFactory.getInstance().bulidShopList(shopResponseList);
                        shopAdapter = new ShopAdapter(shopInfoList,getActivity());
                        shopListView.setAdapter(shopAdapter);
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

        //ListView列表
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopInfoVo shopInfoVo = (ShopInfoVo) shopAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), OrderBookingActivity.class);
                intent.putExtra("shopid", shopInfoVo.getShopid());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }
}
