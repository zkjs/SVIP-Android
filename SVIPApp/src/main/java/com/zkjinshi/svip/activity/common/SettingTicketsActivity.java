package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
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
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.OrderInfoVo;
import com.zkjinshi.svip.vo.TicketVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private Response.Listener<String> loadTicketsListListener;
    private Response.ErrorListener    loadTicketsListErrorListener;

    private Response.Listener<String> deleteTicketListener;
    private Response.ErrorListener    deleteTicketErrorListener;
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

        DialogUtil.getInstance().showProgressDialog(this);
        createDeleteTicketListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.updateTicketUrl(),deleteTicketListener, deleteTicketErrorListener){
            @Override
            protected Map<String, String> getParams() {
                TicketVo ticketData = listData.get(position);
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("id",ticketData.getId());
                map.put("set","3");
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void createDeleteTicketListener() {
        deleteTicketListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response)){
                    return ;
                }
                //解析json数据
                LogUtil.getInstance().info(LogLevel.ERROR, "public void onResponse:\n"+response);
                Gson gson = new Gson();
                BaseResponse result = gson.fromJson(response,BaseResponse.class);
                if(result.isSet()){
                    listData.remove(deleteIndex);
                    myAdapter.notifyDataSetChanged();
                }else{
                    DialogUtil.getInstance().showToast(SettingTicketsActivity.this,"删除发票失败。");
                }
            }
        };

        //error listener
       deleteTicketErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "删除发票失败" + volleyError.toString());
                DialogUtil.getInstance().showToast(SettingTicketsActivity.this, "删除发票失败。");
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
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
        DialogUtil.getInstance().showProgressDialog(this);
        createLoadTicketListListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.geTicketListUrl(), loadTicketsListListener, loadTicketsListErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("set","0");
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    //加载发票列表监听
    private void createLoadTicketListListener() {
        loadTicketsListListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response)){
                    return ;
                }
                //解析json数据
                LogUtil.getInstance().info(LogLevel.ERROR, "public void onResponse:\n"+response);
                Gson gson = new Gson();
                ArrayList<TicketVo> datalist = gson.fromJson(response, new TypeToken<ArrayList<TicketVo>>(){}.getType());
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
            }
        };

        //error listener
        loadTicketsListErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "获取发票列表失败。" + volleyError.toString());
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
    }


    //测试
    private void test(){
        listData = new ArrayList<TicketVo>();
        for(int i=0;i<4;i++)
        {
            TicketVo ticket = new TicketVo();
            ticket.setId("" + i);
            ticket.setInvoice_title("中科金石科技有限公司 " + i);
            listData.add(ticket);
        }
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
