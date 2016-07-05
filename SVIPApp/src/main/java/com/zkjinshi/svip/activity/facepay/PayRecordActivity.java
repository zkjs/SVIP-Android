package com.zkjinshi.svip.activity.facepay;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ExpenseAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PayUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ExListView;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.PayRecordVo;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/3/8.
 */
public class PayRecordActivity extends BaseActivity {

    private Context mContext;
    private ImageButton backBtn,accoutBtn;
    private TextView titleTv;
    private ExListView mRefreshListView;
    private ExpenseAdapter mPayRecordAdapter = null;
    private int    mCurrentPage;//记录当前查询页
    private String status = "2";
    private TextView emptyTv;
    private View maskView;
    private String shopid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        mContext = this;

        if(!TextUtils.isEmpty(getIntent().getStringExtra("status"))){
            status = getIntent().getStringExtra("status");
        }

        shopid = getIntent().getStringExtra("shopid");

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        accoutBtn = (ImageButton)findViewById(R.id.btn_menu);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mRefreshListView = (ExListView)findViewById(R.id.expense_list_view);
        emptyTv = (TextView)findViewById(R.id.empty_tv);
        maskView = findViewById(R.id.mask_view);
    }

    private void initData() {
        ArrayList<PayRecordDataVo> payRecordDataList = new ArrayList<PayRecordDataVo>();
        mPayRecordAdapter = new ExpenseAdapter(PayRecordActivity.this,payRecordDataList);
        mRefreshListView.setAdapter(mPayRecordAdapter);
        mRefreshListView.contentAdapter = mPayRecordAdapter;
        titleTv.setText("支付记录");
        mRefreshListView.setEmptyView(emptyTv);
        emptyTv.setVisibility(View.GONE);
        accoutBtn.setVisibility(View.GONE);

    }

    public void onResume(){
        super.onResume();
        mCurrentPage = 0;
        loadRecord(mCurrentPage);
    }

    private void initListener() {
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 0;
                loadRecord(mCurrentPage);
            }

            @Override
            public void onLoadingMore() {
                loadRecord(mCurrentPage);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realPostion = position - 1;
                ArrayList<PayRecordDataVo> expenseList = (ArrayList<PayRecordDataVo>)mPayRecordAdapter.getExpenseList();
                expenseList.get(realPostion).setShow(!expenseList.get(realPostion).isShow());
                mPayRecordAdapter.isClick = true;
                mPayRecordAdapter.clickIndex = realPostion;
                mPayRecordAdapter.notifyDataSetChanged();
                if(realPostion == expenseList.size() - 1){
                    mRefreshListView.setSelection(realPostion);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        accoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAccount();
            }
        });
    }

    private void getAccount(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getAccount();
            client.get(mContext, url, stringEntity, "application/json", new JsonHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            double balance = response.getDouble("balance");
                            String myAccount = "¥ " + PayUtil.changeMoney(balance);
                            showPopupWindow(accoutBtn,myAccount);
                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showPopupWindow(View view,final String myAccount) {
        maskView.setVisibility(View.VISIBLE);
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate( R.layout.pop_account_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        ColorDrawable color = new ColorDrawable(Color.parseColor("#00000000"));
        popupWindow.setBackgroundDrawable(color);
        TextView accountTv = (TextView)contentView.findViewById(R.id.account_tv);
        accountTv.setText(myAccount);
        // 设置好参数之后再show
        int stausHeight = getStatusBarHeight();
        int offsetX =  DisplayUtil.dip2px(mContext,7);
        int offsetY = DisplayUtil.dip2px(mContext,40) + stausHeight;
       // popupWindow.showAsDropDown(view,-offsetX,-offsetY);
        popupWindow.showAtLocation(view,Gravity.TOP|Gravity.RIGHT,offsetX,offsetY);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                maskView.setVisibility(View.GONE);
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }




    private void loadRecord(int page){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getPayList(status,page,shopid);
            client.get(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    if(mCurrentPage == 0){
                        DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                    }
                }

                public void onFinish(){
                    super.onFinish();
                    if(mCurrentPage == 0 || mCurrentPage == 1){
                        DialogUtil.getInstance().cancelProgressDialog();
                    }
                    mRefreshListView.refreshFinish();//结束刷新状态
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        PayRecordVo payRecordVo = new Gson().fromJson(response,PayRecordVo.class);
                        if(payRecordVo == null){
                            return;
                        }
                        if(payRecordVo.getRes() == 0){
                            ArrayList<PayRecordDataVo> payRecordDataList = payRecordVo.getData();
                            if (mCurrentPage == 0) {
                                mPayRecordAdapter.refresh(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                }else{
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mPayRecordAdapter.loadMore(payRecordDataList);
                                if(!payRecordDataList.isEmpty()){
                                    mCurrentPage++;
                                    //mRefreshListView.setSelection(mPayRecordAdapter.datalist.size() - 1);
                                }

                            }

                        }else{
                            Toast.makeText(mContext, payRecordVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    //Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(PayRecordActivity.this,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
