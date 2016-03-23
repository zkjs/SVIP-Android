package com.zkjinshi.svip.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.activity.shop.ShopDetailActivity;
import com.zkjinshi.svip.activity.shop.SearchShopActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.response.GetShopListResponse;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 商店列表Fragment
 */
public class ShopFragment extends BaseFragment {

    private final String TAG = ShopFragment.class.getSimpleName();

    public final static int REQUEST_CHOOSE_CITY = 0x00;

    private RelativeLayout  mRlDingWei;
    private EditText        mEtCity;
    private RefreshListView mLvShopList;

    private ShopAdapter         mShopAdapter;
    private ArrayList<ShopBean> mShopList;

    private int mPage;
    private int mPageSize = 5;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_shop, null);
        mLvShopList = (RefreshListView) view.findViewById(R.id.lv_shop_list);
        mRlDingWei  = (RelativeLayout) view.findViewById(R.id.rl_dingwei);
        mEtCity     = (EditText) view.findViewById(R.id.et_city);
        SoftInputUtil.hideSoftInputMode(mActivity, mEtCity);
        return view;
    }

    @Override
    protected void initData() {

        super.initData();
        mShopList    = new ArrayList<>();
        mShopAdapter = new ShopAdapter(mShopList, mActivity);
        mLvShopList.setAdapter(mShopAdapter);
        mPage = 0;
        getShopList( mPage, mPageSize);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(null != savedInstanceState){
            mPage     = savedInstanceState.getInt("page");
            mShopList = (ArrayList<ShopBean>) savedInstanceState.getSerializable("shop_list");
            if(null != mShopAdapter){
                mShopAdapter.setShopList(mShopList);
            }
        }
    }

    @Override
    protected void initListener() {
        super.initListener();

        //实现上拉加载下拉刷新
        mLvShopList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mPage = 0;
                getShopList( mPage, mPageSize);
            }

            @Override
            public void onLoadingMore() {
                getShopList( mPage, mPageSize);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                ShopBean shopBean = null;
                int realPostion = position - 1;
                int itemType = mShopAdapter.getItemViewType(realPostion);
                if(itemType == mShopAdapter.ITEM_NORMAL_SHOP){
                    shopBean = mShopList.get(realPostion);
                    Intent intent = new Intent(mActivity, ShopDetailActivity.class);
                    String shopid = shopBean.getShopid();
                    intent.putExtra("shopId", shopid);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }else if(itemType == mShopAdapter.ITEM_ADVERTISE){
                    shopBean = mShopList.get(realPostion);
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
                Intent intent = new Intent(mActivity, SearchShopActivity.class);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //输入框点击事件
        mEtCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(mActivity, SearchShopActivity.class);
                    startActivity(intent);
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
    }

    public void getShopList(int page, int pageSize) {
        final Context mContext = getActivity();
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getShopList(page,pageSize);
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                }

                public void onFinish(){
                    mLvShopList.refreshFinish();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetShopListResponse getShopListResponse = new Gson().fromJson(response,GetShopListResponse.class);
                        if (getShopListResponse == null){
                            return;
                        }
                        if(getShopListResponse.getRes() == 0){
                            List<ShopBean> shopList = getShopListResponse.getData();
                            if(mPage == 0){
                                mShopList.clear();
                                mShopList.addAll(shopList);
                                mShopAdapter.setShopList(mShopList);
                            }else if(!shopList.isEmpty()){
                                mShopList.addAll(shopList);
                                mShopAdapter.setShopList(mShopList);
                            }
                            if(!shopList.isEmpty()){
                                mPage++;
                            }
                        }else{
                            Toast.makeText(mContext,getShopListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
