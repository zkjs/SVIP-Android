package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.CityListActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.ShopActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopListResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 首页Fragment
 */
public class ShopFragment extends Fragment {

    private final String TAG = ShopFragment.class.getSimpleName();

    public final static int REQUEST_CHOOSE_CITY = 0x00;

    private Activity     mActivity;
    private LinearLayout mLlCityInfo;
    private EditText     mEtCity;
    private TextView     mTvCityName;
    private ListView     mLvShopList;

    private List<ShopListResponse> mShopList;
    private ShopAdapter            mShopAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();;
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view){
        mLvShopList = (ListView) view.findViewById(R.id.lv_shop_list);
        mLlCityInfo = (LinearLayout) view.findViewById(R.id.ll_city_info);
        mEtCity     = (EditText) view.findViewById(R.id.et_city);
        mTvCityName = (TextView) view.findViewById(R.id.tv_city_name);
    }

    private void initData(){
        mActivity = this.getActivity();
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_layout, null);
        TextView tips = (TextView)emptyView.findViewById(R.id.empty_tips);
        tips.setText("暂无商家");
        ((ViewGroup)mLvShopList.getParent()).addView(emptyView);
        mLvShopList.setEmptyView(emptyView);

        mShopList = new ArrayList<>();
        mShopAdapter = new ShopAdapter(mShopList, mActivity);
        mLvShopList.setAdapter(mShopAdapter);

        String currentCity = CacheUtil.getInstance().getCurrentCity();
        if(!TextUtils.isEmpty(currentCity)){
            mTvCityName.setText(currentCity);
        }
        getShopList();
    }

    private void initListeners(){
        //进入城市选择
        mLlCityInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cityChoose = new Intent(mActivity, CityListActivity.class);
                startActivityForResult(cityChoose, REQUEST_CHOOSE_CITY);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //商店条目点击事件
        mLvShopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopListResponse shopInfoVo = (ShopListResponse) mShopAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), OrderBookingActivity.class);
                intent.putExtra("shopid", shopInfoVo.getShopid());
                getActivity().startActivityForResult(intent, ShopActivity.KILL_MYSELF);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //输入框点击事件
        mEtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftInputUtil.hideSoftInputMode(mActivity, mEtCity);
                Intent cityChoose = new Intent(mActivity, CityListActivity.class);
                startActivityForResult(cityChoose, REQUEST_CHOOSE_CITY);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void getShopList(){
        String url = ProtocolUtil.getShopList(1, 1);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
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
                    Type listType = new TypeToken<List<ShopListResponse>>(){}.getType();
                    Gson gson = new Gson();
                    mShopList = gson.fromJson(result.rawResult, listType);
                    if(null != mShopList && !mShopList.isEmpty()){
                        mShopAdapter.setData(mShopList);
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

    private void getShopListByCity(String city){
        String url = ProtocolUtil.getShopLIstByCityUrl(city);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
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
                    Type listType = new TypeToken<List<ShopListResponse>>(){}.getType();
                    Gson gson = new Gson();
                    mShopList = gson.fromJson(result.rawResult, listType);
                    if(null != mShopList && !mShopList.isEmpty()){
                       mShopAdapter.setData(mShopList);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == mActivity.RESULT_OK){
            if(requestCode == REQUEST_CHOOSE_CITY){
                if(null != data){
                    String city = data.getStringExtra("city");
                    mTvCityName.setText(city);

                    getShopListByCity(city);
                }
            }
        }

    }
}
