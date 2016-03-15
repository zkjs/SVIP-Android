package com.zkjinshi.svip.activity.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.CommentAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.CommentsResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.CommentVo;

import java.util.ArrayList;

/**
 * 开发者：WinkyQin
 * 修改着：JimmyZhang
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

        mShopID = getIntent().getStringExtra("shopID");
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText(getString(R.string.comment));

        mCommentList    = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(CommentListActivity.this,mCommentList);
        mRlvCommentList.setAdapter(mCommentAdapter);
        //获取商家评论列表
        if (!TextUtils.isEmpty(mShopID)) {
            mPage = 0;
            getShopCommentList(mShopID, mPage, mPageSize,true);
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
                mPage = 0;
                getShopCommentList(mShopID, mPage, mPageSize,true);
            }

            @Override
            public void onLoadingMore() {
                getShopCommentList(mShopID, mPage, mPageSize,false);
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
    private void getShopCommentList(String shopID, int page, int pageSize,final boolean isRefresh) {
        String url = ProtocolUtil.getShopCommentListUrl(shopID, page, pageSize);
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
                    CommentsResponse commentsResponse = new Gson().fromJson(result.rawResult,CommentsResponse.class);
                    if(null != commentsResponse){
                        int resultCode = commentsResponse.getRes();
                        if(0 == resultCode){
                            ArrayList<CommentVo> commentList = commentsResponse.getData();
                            if (null != commentList && !commentList.isEmpty()) {
                                if(isRefresh){
                                    mCommentList = commentList;
                                }else {
                                    mCommentList.addAll(commentList);
                                }
                                mCommentAdapter.setCommentList(mCommentList);
                                mPage++;
                            }
                        }else {
                            String resultMsg = commentsResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(CommentListActivity.this,resultMsg, Gravity.CENTER);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if(null != mRlvCommentList){
                        mRlvCommentList.refreshFinish();
                    }
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
