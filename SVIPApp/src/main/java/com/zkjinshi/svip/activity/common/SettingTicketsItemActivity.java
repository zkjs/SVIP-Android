package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

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
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.TicketVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by djd on 2015/8/24.
 */
public class SettingTicketsItemActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();
    private boolean isNew = true;
    private TicketVo tickeData = null;
    private int position;

    private ItemTitleView mTitle;
    private EditText  mInputEt;
    private CheckBox mDefaultRbtn;
    private Button mSaveBtn;

    private Response.Listener<String> addTicketListener;
    private Response.ErrorListener    addTicketErrorListener;

    private Response.Listener<String> updateTicketListener;
    private Response.ErrorListener    updateTicketErrorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ticket_item);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mInputEt          = (EditText)findViewById(R.id.et_setting_input);
        mDefaultRbtn      = (CheckBox)findViewById(R.id.ticket_checkbox);
        mSaveBtn          = (Button)findViewById(R.id.btn_confirm);

    }

    private void initData() {
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            isNew = true;
            mTitle.setTextTitle("新增发票");
        }else{
            Object data = bundle.getSerializable("TicketVo");
            position = bundle.getInt("position");
            isNew = false;
            mTitle.setTextTitle("修改发票"+(position+1));
            tickeData = (TicketVo)data;
            mInputEt.setText(tickeData.getInvoice_title());
            mDefaultRbtn.setChecked(tickeData.getIs_default().equals("1"));
        }


    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNew) {
                    addTicket();
                } else {
                    updateTicket();
                }
            }
        });

    }

    //添加发票
    private void addTicket(){
        if(StringUtil.isEmpty(mInputEt.getText().toString())){
            DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"发票抬头不能为空。");
            return;
        }
        DialogUtil.getInstance().showProgressDialog(this);
        createAddTicketListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.addTicketUrl(),addTicketListener, addTicketErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("invoice_title",mInputEt.getText().toString());
                map.put("is_default",mDefaultRbtn.isChecked() ? "1":"0");
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void createAddTicketListener() {
        addTicketListener = new Response.Listener<String>() {
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
                   DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"保存发票成功。");
                   back();
               }else{
                   DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"保存发票失败。");
               }
            }
        };

        //error listener
        addTicketErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "保存发票失败" + volleyError.toString());
                DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this, "保存发票失败。");
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
    }

    //修改发票
    private void updateTicket(){
        if(StringUtil.isEmpty(mInputEt.getText().toString())){
            DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"发票抬头不能为空。");
            return;
        }
        DialogUtil.getInstance().showProgressDialog(this);
        createUpdateTicketListener();
        DataRequestVolley request = new DataRequestVolley(
                HttpMethod.POST, ProtocolUtil.updateTicketUrl(),updateTicketListener, updateTicketErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("id",tickeData.getId()+"");
                map.put("set","2");
                map.put("invoice_title",mInputEt.getText().toString());
                map.put("is_default",mDefaultRbtn.isChecked() ? "1":"0");
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR, "request：" + request.toString());
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void createUpdateTicketListener() {
        updateTicketListener = new Response.Listener<String>() {
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
                    DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"更新发票成功。");
                    back();
                }else{
                    DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this,"更新发票失败。");
                }
            }
        };

        //error listener
        updateTicketErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.getInstance().info(LogLevel.ERROR, "更新发票失败" + volleyError.toString());
                DialogUtil.getInstance().showToast(SettingTicketsItemActivity.this, "更新发票失败。");
                DialogUtil.getInstance().cancelProgressDialog();
            }
        };
    }

    //回退
    private void back(){
        startActivity(new Intent(SettingTicketsItemActivity.this,SettingTicketsActivity.class));
        finish();
    }
}
