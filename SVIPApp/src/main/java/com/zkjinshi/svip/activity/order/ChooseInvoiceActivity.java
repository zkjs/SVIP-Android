package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;

import com.zkjinshi.svip.response.InsertResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.utils.CacheUtil;

import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.TicketVo;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by djd on 2015/8/28.
 */
public class ChooseInvoiceActivity extends Activity {
    private final static String TAG = ChooseInvoiceActivity.class.getSimpleName();

    private EditText mEtInput;
    private ItemTitleView mTitle;
    private Button mBtnFinish;
    private ListView mTicketsLv;

    private ArrayList<TicketVo> listData = new ArrayList<TicketVo>();
    private MyAdapter myAdapter;

    private class MyAdapter extends BaseAdapter{
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ticket_choose_list_item, null);
                holder = new ViewHolder();
                holder.ticketName = (TextView)convertView.findViewById(R.id.tv_ticket_name);
                holder.ticketIndex = (TextView)convertView.findViewById(R.id.tv_ticket_index);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            holder.ticketName.setText(listData.get(position).getInvoice_title());
            holder.ticketIndex.setText("发票"+(position+1));

            return convertView;
        }
    }

    /*存放控件*/
    public final class ViewHolder{
        public TextView ticketName;
        public TextView ticketIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ticket);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mEtInput = (EditText)findViewById(R.id.et_setting_input);
        mTitle   = (ItemTitleView) findViewById(R.id.itv_title);
        mBtnFinish = (Button)findViewById(R.id.btn_confirm);
        mTicketsLv = (ListView)findViewById(R.id.lv_tickets);
    }

    private void initData() {
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.GONE);
        mTitle.setTextTitle("选择发票");

        loadTicketsList();
        myAdapter = new MyAdapter(this);
        mTicketsLv.setAdapter(myAdapter);
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTicketsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                OrderInvoiceResponse orderInvoiceResponse = new OrderInvoiceResponse();
                orderInvoiceResponse.setId(listData.get(i).getId());
                orderInvoiceResponse.setInvoice_title(listData.get(i).getInvoice_title());
                orderInvoiceResponse.setInvoice_get_id("1");
                intent.putExtra("selectInvoice", orderInvoiceResponse);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtil.isEmpty(mEtInput.getText().toString())) {
                    DialogUtil.getInstance().showToast(ChooseInvoiceActivity.this, "内容不能为空。");
                    return;
                }
               addInvoice();
            }
        });

    }

    //添加发票
    private void addInvoice(){
        String url = ProtocolUtil.addTicketUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("invoice_title",mEtInput.getText().toString());
        bizMap.put("is_default","0");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                    InsertResponse insertResponse = new Gson().fromJson(result.rawResult,InsertResponse.class);
                    if(insertResponse.isSet()){
                        Intent intent = new Intent();
                        OrderInvoiceResponse orderInvoiceResponse = new OrderInvoiceResponse();
                        orderInvoiceResponse.setId(insertResponse.getId());
                        orderInvoiceResponse.setInvoice_title(mEtInput.getText().toString());
                        orderInvoiceResponse.setInvoice_get_id("1");
                        intent.putExtra("selectInvoice", orderInvoiceResponse);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        LogUtil.getInstance().info(LogLevel.ERROR, "添加发票失败。");
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




    //加载发票列表
    private void loadTicketsList() {
        String url =ProtocolUtil.geTicketListUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("set","0");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                    Gson gson = new Gson();
                    ArrayList<TicketVo> datalist = gson.fromJson(result.rawResult, new TypeToken<ArrayList<TicketVo>>(){}.getType());
                    listData.clear();
                    for(TicketVo ticketVo : datalist){
                        if(ticketVo.getIs_default().equals("1")){
                            listData.add(0,ticketVo);
                        }
                        else{
                            listData.add(ticketVo);
                        }
                    }
                    myAdapter.notifyDataSetChanged();

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
