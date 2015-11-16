package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.BaseAdapter;
import android.widget.ListView;

import android.widget.TextView;


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
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;

import com.zkjinshi.svip.vo.TicketVo;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by djd on 2015/8/24.
 */
public class SettingTicketsActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();

    private ItemTitleView mTitle;
    private View footView;

    private ListView mTicketsLv;
    private ArrayList<TicketVo> listData = new ArrayList<TicketVo>();
    private MyAdapter myAdapter;

    private int deleteIndex;


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
                convertView = mInflater.inflate(R.layout.ticket_list_item, null);
                holder = new ViewHolder();
                holder.ticketName = (TextView)convertView.findViewById(R.id.tv_ticket_name);
                holder.ticketIndex = (TextView)convertView.findViewById(R.id.tv_ticket_index);
                holder.setTv      = (TextView)convertView.findViewById(R.id.tv_set);
                holder.delTv      = (TextView)convertView.findViewById(R.id.tv_del);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            holder.ticketName.setText(listData.get(position).getInvoice_title());
            if(position == 0){
                holder.ticketIndex.setText("默认发票");
            }else{
                holder.ticketIndex.setText("发票"+(position+1));
            }
            holder.setTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setItem(position);
                }
            });
            holder.delTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(position);
                }
            });

            return convertView;
        }
    }

    /*存放控件*/
    public final class ViewHolder{
        public TextView ticketName;
        public TextView ticketIndex;
        public TextView setTv;
        public TextView delTv;

    }

    private void setItem(int position){
       // DialogUtil.getInstance().showToast(this,"setItem"+position);
        TicketVo data = listData.get(position);
        Intent intent = new Intent(SettingTicketsActivity.this,SettingTicketsItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TicketVo", data);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void deleteItem(final int position){
        deleteIndex = position;
        new AlertDialog.Builder(this)
                .setTitle("确认")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("你确定要删除该信息吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteItemByNet(position);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();

    }

    private void deleteItemByNet(final int position){

        String url = ProtocolUtil.updateTicketUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        TicketVo ticketData = listData.get(position);
        bizMap.put("id",ticketData.getId()+"");
        bizMap.put("set", "3");
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {

                    Gson gson = new Gson();
                    BaseResponse baseResponse = gson.fromJson(result.rawResult, BaseResponse.class);
                    if (baseResponse.isSet()) {
                        listData.remove(deleteIndex);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        DialogUtil.getInstance().showToast(SettingTicketsActivity.this, "删除发票失败。");
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
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ticket_list);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mTicketsLv        = (ListView)findViewById(R.id.lv_tickets);
        footView    = getLayoutInflater().inflate(R.layout.ticket_list_item_new, null);
    }

    private void initData() {
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.GONE);
        mTitle.setTextTitle("设置发票");

        //test();
        loadTicketsList();
        mTicketsLv.addFooterView(footView, null, false);
        myAdapter = new MyAdapter(this);
        mTicketsLv.setAdapter(myAdapter);

    }

    //加载发票列表
    private void loadTicketsList() {
        String url = ProtocolUtil.geTicketListUrl();
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    ArrayList<TicketVo> datalist = gson.fromJson( result.rawResult, new TypeToken<ArrayList<TicketVo>>(){}.getType());
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




    private void initListener(){
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // DialogUtil.getInstance().showToast(SettingTicketsActivity.this,"new a Ticket");
                startActivity(new Intent(SettingTicketsActivity.this,SettingTicketsItemActivity.class));
                finish();
            }
        });


    }





}
