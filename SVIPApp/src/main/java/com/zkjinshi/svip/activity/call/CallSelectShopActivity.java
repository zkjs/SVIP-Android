package com.zkjinshi.svip.activity.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.CallSelectAreaAdapter;
import com.zkjinshi.svip.adapter.SelectShopAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.HanziToPinyin;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.SideBar;
import com.zkjinshi.svip.vo.GetMyShopVo;
import com.zkjinshi.svip.vo.MyShopVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/23.
 */
public class CallSelectShopActivity extends BaseActivity {
    private final static String TAG = CallSelectShopActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn,menuIBtn;
    private TextView titleTv;

    private ListView mListView;
    private TextView mFooterView;
    private SelectShopAdapter mAdapter;
    private  SideBar mSideBar;
    private EditText mSearchInput;

    private ArrayList<MyShopVo> datas = new ArrayList<>();
    private int    mCurrentPage = 0;//记录当前查询页
    private int mPageSize = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_select_shop);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        menuIBtn = (ImageButton)findViewById(R.id.btn_menu);

        mListView = (ListView)findViewById(R.id.school_friend_member);
        mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
        mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);
        TextView mDialog = (TextView) findViewById(R.id.school_friend_dialog);

        mSideBar.setTextView(mDialog);

        // 给listView设置adapter
        mFooterView = (TextView) View.inflate(this, R.layout.item_list_contact_count, null);
        mListView.addFooterView(mFooterView);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("呼叫中心");
        menuIBtn.setImageResource(R.drawable.btn_menu_selector);
        menuIBtn.setVisibility(View.VISIBLE);

        loadShops();
    }

    private void initListener() {
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        menuIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CallOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               MyShopVo myShopVo = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, CallSelectAreaActivity.class);
                intent.putExtra("shopid",myShopVo.getShopid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = 0;
                // 该字母首次出现的位置
                if (mAdapter != null) {
                    position = mAdapter.getPositionForSection(s.charAt(0));
                }
                if (position != -1) {
                    mListView.setSelection(position);
                } else if (s.contains("#")) {
                    mListView.setSelection(0);
                }
            }
        });
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                ArrayList<MyShopVo> temp = new ArrayList<>(datas);
                for (MyShopVo data : datas) {
                    if (data.getShopname().contains(s) || data.getPinyin().contains(s)) {
                    } else {
                        temp.remove(data);
                    }
                }
                if (mAdapter != null) {
                    mFooterView.setText(temp.size() + "个商家");
                    mAdapter.refresh(temp);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadShops(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.shopBelong(mCurrentPage,mPageSize);
            client.get(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetMyShopVo getMyShopVo = new Gson().fromJson(response,GetMyShopVo.class);
                        if(getMyShopVo.getRes() == 0){
                            datas = getMyShopVo.getData();
                            mFooterView.setText(datas.size() + "个商家");
                            for(MyShopVo myShopVo : datas){
                                myShopVo.setPinyin(HanziToPinyin.getPinYin(myShopVo.getShopname()));
                            }
                            mAdapter = new SelectShopAdapter(mListView, datas);
                            mListView.setAdapter(mAdapter);
                        }else{
                            Toast.makeText(mContext, getMyShopVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
