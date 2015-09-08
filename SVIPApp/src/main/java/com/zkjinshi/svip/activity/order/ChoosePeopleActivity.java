package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.InsertResponse;
import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by djd on 2015/8/28.
 */
public class ChoosePeopleActivity extends Activity {

    private EditText mEtInput;
    private ItemTitleView mTitle;
    private Button mBtnFinish;
    private ListView mPeopleLv;

    private ArrayList<OrderUsersResponse> listData = new ArrayList<OrderUsersResponse>();
    private MyAdapter myAdapter;

    private Response.Listener<String> loadPeopleListListener , addPeopleListener;
    private Response.ErrorListener   loadTicketsListErrorListener , addPeopleErrorListener;
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
                if (StringUtil.isEmpty(mEtInput.getText().toString())) {
                    DialogUtil.getInstance().showToast(ChoosePeopleActivity.this, "内容不能为空。");
                    return;
                }
               addPeople(mEtInput.getText().toString());
            }
        });

    }

    //添加入住人
    private void addPeople(final String realname){
        DialogUtil.getInstance().showProgressDialog(this);
        createAddPeopleListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.addPeopleListUrl(), addPeopleListener, addPeopleErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("realname",realname);
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void createAddPeopleListener() {
        addPeopleListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response)){
                    return ;
                }
                //解析json数据
                LogUtil.getInstance().info(LogLevel.ERROR, "public void onResponse:\n"+response);
                InsertResponse insertResponse = new Gson().fromJson(response,InsertResponse.class);
                if(insertResponse.isSet()){
                    Intent intent = new Intent();
                    OrderUsersResponse orderUsersResponse = new OrderUsersResponse();
                    orderUsersResponse.setId(insertResponse.getId());
                    orderUsersResponse.setRealname(mEtInput.getText().toString());
                    intent.putExtra("selectPeople", orderUsersResponse);
                    intent.putExtra("index",mIndex);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    LogUtil.getInstance().info(LogLevel.ERROR, "添加入住人失败。" );
                }
            }
        };

        //error listener
        addPeopleErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "添加入住人失败。" + volleyError.toString());
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
    }

    //加载发票列表
    private void loadPeopleList() {
        DialogUtil.getInstance().showProgressDialog(this);
        createLoadTicketListListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.getPeopleListUrl(), loadPeopleListListener, loadTicketsListErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    //加载发票列表监听
    private void createLoadTicketListListener() {
        loadPeopleListListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response)){
                    return ;
                }
                //解析json数据
                LogUtil.getInstance().info(LogLevel.ERROR, "public void onResponse:\n"+response);
                Gson gson = new Gson();
                listData = gson.fromJson(response, new TypeToken<ArrayList<OrderUsersResponse>>(){}.getType());
                myAdapter.notifyDataSetChanged();
            }
        };

        //error listener
        loadTicketsListErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "获取入住人列表失败。" + volleyError.toString());
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
    }
}
