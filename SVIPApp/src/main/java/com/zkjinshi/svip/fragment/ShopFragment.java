package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.CityListActivity;
import com.zkjinshi.svip.activity.order.OrderBookingActivity;
import com.zkjinshi.svip.activity.order.ShopActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.bean.BaseShopBean;
import com.zkjinshi.svip.bean.RecommendShopBean;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商店列表Fragment
 */
public class ShopFragment extends Fragment {

    private final String TAG = ShopFragment.class.getSimpleName();

    public final static int REQUEST_CHOOSE_CITY = 0x00;

    private Activity       mActivity;
    private RelativeLayout mRlDingWei;
    private EditText       mEtCity;
    private ListView       mLvShopList;

    private List<BaseShopBean> mShopList;
    private ShopAdapter        mShopAdapter;

    private int mPage = 1;
    private int mPageSize     = 5;

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
        initData();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        ImageLoader.getInstance().clearMemoryCache();
    }

    private void initView(View view){
        mLvShopList = (ListView) view.findViewById(R.id.lv_shop_list);
        mRlDingWei  = (RelativeLayout) view.findViewById(R.id.rl_dingwei);
        mEtCity     = (EditText) view.findViewById(R.id.et_city);
    }

    private void initData(){
        mActivity = this.getActivity();
        SoftInputUtil.hideSoftInputMode(mActivity, mEtCity);
        View emptyView = mActivity.getLayoutInflater().inflate(R.layout.empty_layout, null);
        TextView tips  = (TextView)emptyView.findViewById(R.id.empty_tips);
        tips.setText("暂无商家");
        ((ViewGroup)mLvShopList.getParent()).addView(emptyView);
        mLvShopList.setEmptyView(emptyView);

        mShopList = new ArrayList<>();
        mShopAdapter = new ShopAdapter(mShopList, mActivity);
        mLvShopList.setAdapter(mShopAdapter);

        String city = mEtCity.getText().toString();
        if (!TextUtils.isEmpty(city)){
            getRecommendShopList(city);
        } else {
            getRecommendShopList(getString(R.string.shenzhen_city));
        }
    }

    private void initListeners(){

        //商店条目点击事件
        mLvShopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseShopBean baseShopBean = (BaseShopBean) mShopAdapter.getItem(position);
                if(baseShopBean instanceof RecommendShopBean){
                    //TODO:进入推荐的链接地址
                    String linkUrl = ((RecommendShopBean) baseShopBean).getLink_url();
                    Uri uri = Uri.parse(linkUrl);
                    Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(mActivity, OrderBookingActivity.class);
                    intent.putExtra("shopid", baseShopBean.getShopid());
                    startActivityForResult(intent, ShopActivity.KILL_MYSELF);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });

        //输入框点击事件
        mRlDingWei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cityChoose = new Intent(mActivity, CityListActivity.class);
                startActivityForResult(cityChoose, REQUEST_CHOOSE_CITY);
            }
        });

        //输入框点击事件
        mEtCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent cityChoose = new Intent(mActivity, CityListActivity.class);
                    startActivityForResult(cityChoose, REQUEST_CHOOSE_CITY);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                return false;
            }
        });
    }

    private void getShopListByCity(String city, int page, int size){
        String url = ProtocolUtil.getShopListByCityUrl(city, 1, 5);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(mActivity, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<List<ShopBean>>(){}.getType();
                    Gson gson = new Gson();

                    mPage++;
                    mShopList = gson.fromJson(result.rawResult, listType);
                    mShopAdapter.setData(mShopList);
                    LogUtil.getInstance().info(LogLevel.INFO, "shoplistByCity:" + mShopList);
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
                    mEtCity.setText(city);
                    mPage = 1;
                    getShopListByCity(city, mPage, mPageSize);
                }
            }
        }
    }

    public void getShopList(int page, int pageSize) {
        String url = ProtocolUtil.getShopListUrl(page, pageSize);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(mActivity, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<List<ShopBean>>(){}.getType();
                    Gson gson = new Gson();
                    List<BaseShopBean> shopBeanList = gson.fromJson(result.rawResult, listType);

                    mPage++;
                    mShopList.addAll(shopBeanList);
                    mShopAdapter.setData(mShopList);
                    LogUtil.getInstance().info(LogLevel.INFO, "shoplistByCity:" + mShopList);
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

    /**
     * 获得推荐酒店商家列表
     */
    public void getRecommendShopList(String city) {
        String url = ProtocolUtil.getRecommendedShopListUrl(city);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(mActivity, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<List<RecommendShopBean>>(){}.getType();
                    Gson gson = new Gson();
                    List<RecommendShopBean> recommendShopList = gson.fromJson(result.rawResult, listType);
                    mShopList.add(recommendShopList.get(0));
                    LogUtil.getInstance().info(LogLevel.INFO, "recommendShopList:" + recommendShopList);

                    getShopList(mPage, mPageSize);
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
