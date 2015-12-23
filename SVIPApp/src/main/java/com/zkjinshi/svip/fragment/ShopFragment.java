package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.CityListActivity;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.activity.order.GoodListActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.bean.BaseShopBean;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商店列表Fragment
 */
public class ShopFragment extends BaseFragment {

    private final String TAG = ShopFragment.class.getSimpleName();

    public final static int REQUEST_CHOOSE_CITY = 0x00;

    private RelativeLayout mRlDingWei;
    private EditText       mEtCity;
    private ListView       mLvShopList;

    private ShopAdapter         mShopAdapter;
    private ArrayList<ShopBean> mShopList;
    private String mUserID;

    private int mPage;
    private int mPageSize = 5;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_shop, null);
        mLvShopList = (ListView) view.findViewById(R.id.lv_shop_list);
        mRlDingWei  = (RelativeLayout) view.findViewById(R.id.rl_dingwei);
        mEtCity     = (EditText) view.findViewById(R.id.et_city);
        SoftInputUtil.hideSoftInputMode(mActivity, mEtCity);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        mUserID = CacheUtil.getInstance().getUserId();
        if (null == mShopList || mShopList.isEmpty()) {
            mShopList    = new ArrayList<>();
            mShopAdapter = new ShopAdapter(mShopList, mActivity);
            mLvShopList.setAdapter(mShopAdapter);
            mPage = 1;
            getShopList(mUserID, mPage, mPageSize);
        } else {
            if(null != mShopAdapter){
                mLvShopList.setAdapter(mShopAdapter);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(null != savedInstanceState){
            mPage     = savedInstanceState.getInt("page");
            mShopList = (ArrayList<ShopBean>) savedInstanceState.getSerializable("shop_list");
            if(null != mShopAdapter){
                mShopAdapter.setData(mShopList);
            }
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        //商店条目点击事件
        mLvShopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopBean shopBean = (ShopBean) mShopAdapter.getItem(position);
                if(position > 0){
                    Intent intent = new Intent(mActivity, GoodListActivity.class);
                    String shopid = shopBean.getShopid();
                    intent.putExtra("shopBean", shopBean);
                    intent.putExtra("showHeader",true);
                    intent.putExtra("shopid",shopid);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    String linkUrl = shopBean.getShopaddress();
                    if(!TextUtils.isEmpty(linkUrl)){
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("webview_url", linkUrl);
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != mShopList && !mShopList.isEmpty()){
            outState.putInt("page", mPage);
            outState.putSerializable("shop_list", mShopList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == mActivity.RESULT_OK){
//            if(requestCode == REQUEST_CHOOSE_CITY){
//                if(null != data){
//                    String city = data.getStringExtra("city");
//                    if(!city.equals(getString(R.string.locating))){
//                        mEtCity.setText(city);
//                    }
//                    mBaseShopList.removeAll(mBaseShopList);
//                    mPage = 1;
//                    getShopListByCity(city, mPage, mPageSize);
//                }
//            }
//        }
//    }

    public void getShopList(String userID, int page, int pageSize) {
        getShopListByCity(userID, null, page, pageSize);
    }

    private void getShopListByCity(String userID, String city, int page, int pageSize){
        String url = null;
        if(!TextUtils.isEmpty(city)){
            url = ProtocolUtil.getShopListUserByCityUrl(userID, city, page, pageSize);
        }else{
            url = ProtocolUtil.getShopListUserUrl(userID, page, pageSize);
        }

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
                Gson gson = new Gson();
                Type type = new TypeToken<List<ShopBean>>(){}.getType();

                mPage++;
                List<ShopBean> shopList = gson.fromJson(result.rawResult, type);
                if(null != shopList && !shopList.isEmpty()){
                    mShopList.addAll(shopList);
                    mShopAdapter.setData(mShopList);
                }
                LogUtil.getInstance().info(LogLevel.INFO, "getShopListByCity:" + shopList);
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }


}
