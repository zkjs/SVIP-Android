package com.zkjinshi.svip.activity.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.CommentAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.CommentVo;

import org.w3c.dom.Comment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentListActivity extends BaseActivity {

    private final static String TAG = CommentListActivity.class.getSimpleName();

    private ImageButton     backIBtn;
    private TextView        titleTv;
    private LinearLayout    mEmptyView;

    private RefreshListView mRlvCommentList;
    private ArrayList<CommentVo> mCommentList;
    private CommentAdapter  mCommentAdapter;

    private String mShopID;
    private int    mPage; //分页初始值
    private int    mPageSize = 10;//每页获取评论数

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv  = (TextView)findViewById(R.id.header_bar_tv_title);
        mEmptyView      = (LinearLayout) findViewById(R.id.empty_view);
        mRlvCommentList = (RefreshListView) findViewById(R.id.rlv_comment_list);
        mRlvCommentList.setEmptyView(mEmptyView);
    }

    private void initData() {

        mShopID = getIntent().getStringExtra("shop_id");
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText(getString(R.string.comment));

        mCommentList    = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(CommentListActivity.this,mCommentList);

        //获取商家评论列表
        if (!TextUtils.isEmpty(mShopID)) {
            mPage = 1;
            getShopCommentList(mShopID, mPage, mPageSize);
        }
    }

    private void initListener() {

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentListActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        mRlvCommentList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                getShopCommentList(mShopID, mPage, mPageSize);
            }

            @Override
            public void onLoadingMore() {
                getShopCommentList(mShopID, mPage, mPageSize);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realPostion = position -1;

            }
        });
    }

    /**
     * 获取商家评论列表
     * @param shopID
     * @param page
     * @param pageSize
     */
    private void getShopCommentList(String shopID, int page, int pageSize) {

        String url = ProtocolUtil.getShopCommentListUrl(shopID, page, pageSize);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);

                mRlvCommentList.refreshFinish();
            }

            @Override
            public void onNetworkRequestCancelled() {
                mRlvCommentList.refreshFinish();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);

                try {
                    Type listType = new TypeToken<ArrayList<CommentVo>>() {}.getType();
                    Gson gson     = new Gson();

                    //TODO 评论列表显示
                    ArrayList<CommentVo> commentList = gson.fromJson(result.rawResult, listType);
                    if (null != commentList && !commentList.isEmpty()) {
                        mCommentList.addAll(commentList);
                        //设置数据显示
                        if(mPage == 1){
                            mRlvCommentList.setAdapter(mCommentAdapter);
                        } else {
                            mCommentAdapter.setCommentList(mCommentList);
                        }
                        mPage++;
                    }

                    mRlvCommentList.refreshFinish();
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
