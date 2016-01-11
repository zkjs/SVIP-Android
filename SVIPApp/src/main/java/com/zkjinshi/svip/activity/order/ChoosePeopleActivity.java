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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;

import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.factory.PersonCheckInFactory;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.InsertResponse;
import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.sqlite.PersonCheckInDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;

import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.PersonCheckInVo;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by djd on 2015/8/28.
 */
public class ChoosePeopleActivity extends BaseActivity {
    private final static String TAG = ChoosePeopleActivity.class.getSimpleName();

    private EditText mEtInput;
    private ItemTitleView mTitle;
    private Button mBtnFinish;
    private ListView mPeopleLv;

    private ArrayList<OrderUsersResponse> listData = new ArrayList<OrderUsersResponse>();
    private MyAdapter myAdapter;
    private int mIndex;

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
            holder.ticketName.setText(listData.get(position).getRealname());
            holder.ticketIndex.setText("入住人"+(position+1));

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
        mPeopleLv = (ListView)findViewById(R.id.lv_tickets);
    }

    private void initData() {
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.GONE);
        mTitle.setTextTitle("选择入住人");
        mIndex = getIntent().getIntExtra("index",0);
        mEtInput.setHint("输入新的入住人");

        loadPeopleList();
        myAdapter = new MyAdapter(this);
        mPeopleLv.setAdapter(myAdapter);
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPeopleLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("selectPeople", listData.get(i));
                intent.putExtra("index", mIndex);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameInput = mEtInput.getText().toString();
                if (StringUtil.isEmpty(nameInput)) {
                    DialogUtil.getInstance().showToast(ChoosePeopleActivity.this, "内容不能为空。");
                    return;
                }
               addPeople(nameInput);
            }
        });

    }

    //添加入住人
    private void addPeople(final String realname){
        String url = ProtocolUtil.addPeopleListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("realname",realname);
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
                    InsertResponse insertResponse = new Gson().fromJson(result.rawResult, InsertResponse.class);
                    if(insertResponse.isSet()){
                        String inputName = mEtInput.getText().toString();
                        Intent intent = new Intent();
                        OrderUsersResponse orderUsersResponse = new OrderUsersResponse();
                        orderUsersResponse.setId(insertResponse.getId());
                        orderUsersResponse.setRealname(inputName);

                        PersonCheckInVo personCheckInVo = PersonCheckInFactory.getInstance().
                                         buildPersonCheckInVoByOrderUser(orderUsersResponse);
                        PersonCheckInDBUtil.getInstance().addNewPersonCheckIn(personCheckInVo);

                        intent.putExtra("selectPeople", orderUsersResponse);
                        intent.putExtra("index", mIndex);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        LogUtil.getInstance().info(LogLevel.ERROR, "添加入住人失败。");
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
    private void loadPeopleList() {
        String url =  ProtocolUtil.getPeopleListUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
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
                    listData = gson.fromJson( result.rawResult, new TypeToken<ArrayList<OrderUsersResponse>>(){}.getType());
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
