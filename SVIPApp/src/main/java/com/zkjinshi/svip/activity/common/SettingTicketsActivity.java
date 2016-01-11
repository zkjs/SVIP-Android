package com.zkjinshi.svip.activity.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.swipelistview.SwipeMenu;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuCreator;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuItem;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuListView;
import com.zkjinshi.svip.vo.TicketVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by djd on 2015/8/24.
 */
public class SettingTicketsActivity extends BaseActivity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private Button addBtn;
    private SwipeMenuListView mTicketsLv;
    private ArrayList<TicketVo> listData = new ArrayList<TicketVo>();
    private MyAdapter myAdapter;
    private int deleteIndex;
    private boolean isChoose = false;

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
            return convertView;
        }
    }

    /*存放控件*/
    public final class ViewHolder{
        public TextView ticketName;
        public TextView ticketIndex;
    }

    private void setItem(int position){
        TicketVo data = listData.get(position);
        Intent intent = new Intent(SettingTicketsActivity.this,SettingTicketsItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TicketVo", data);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void deleteItem(final int position){
        deleteIndex = position;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("你确定要删除该信息吗？");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteItemByNet(position);
            }
        });
        customBuilder.create().show();
    }

    private void deleteItemByNet(final int position){

        String url = ProtocolUtil.updateTicketUrl();;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ticket_list);

        isChoose = getIntent().getBooleanExtra("isChoose",false);
        initView();
        initData();
        initListener();
    }

    protected void onResume(){
        super.onResume();
        listData.clear();
        loadTicketsList();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        mTicketsLv = (SwipeMenuListView)findViewById(R.id.lv_tickets);
        addBtn = (Button)findViewById(R.id.btn_add);
    }

    private void initData() {
        MineNetController.getInstance().init(this);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("发票信息");

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(SettingTicketsActivity.this,88));
                deleteItem.setIcon(R.mipmap.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        mTicketsLv.setMenuCreator(creator);
        myAdapter = new MyAdapter(this);
        mTicketsLv.setAdapter(myAdapter);
        initMenuData();
    }

    private void initMenuData(){
        //删除发票信息
        mTicketsLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0://删除
                        deleteItem(position);
                        break;
                }
                return false;
            }
        });
        //编辑发票信息
        mTicketsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position-1;
                if(position < 0){
                    return;
                }
                if(isChoose){
                    Intent intent = new Intent();
                    OrderInvoiceResponse orderInvoiceResponse = new OrderInvoiceResponse();
                    orderInvoiceResponse.setId(listData.get(position).getId());
                    orderInvoiceResponse.setInvoice_title(listData.get(position).getInvoice_title());
                    orderInvoiceResponse.setInvoice_get_id("1");
                    intent.putExtra("selectInvoice", orderInvoiceResponse);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    setItem(position);
                }

            }
        });
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

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //新增发票
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingTicketsActivity.this,SettingTicketsItemActivity.class));
                finish();
            }
        });
    }

}
