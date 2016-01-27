package com.zkjinshi.svip.activity.shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.order.ShopCityActivity;
import com.zkjinshi.svip.adapter.SearchHistoryAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.response.BigPicResponse;
import com.zkjinshi.svip.utils.CacheUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/1/26.
 */
public class SearchShopActivity extends BaseActivity {
    private final static String TAG = SearchShopActivity.class.getSimpleName();

    private EditText inputTextEt;
    private TextView showTextTv;
    private LinearLayout showLayout,histroyLayout;
    private ImageView closeIv;
    private TextView currentCityTv;
    private ListView histroyListView;
    private SearchHistoryAdapter searchHistoryAdapter;


    private ArrayList<String> histroyList = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);

        initView();
        initData();
        initListener();
    }

    public void onStop(){
        super.onStop();
    }

    public void onResume(){
        super.onResume();
        String searchHistoryStr = CacheUtil.getInstance().getListStrCache("search_history");
        if(TextUtils.isEmpty(searchHistoryStr)){
            return;
        }
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        histroyList = gson.fromJson(searchHistoryStr, listType);
        searchHistoryAdapter.dataList = histroyList;
        searchHistoryAdapter.notifyDataSetChanged();
        if(histroyList.isEmpty()){
            histroyLayout.setVisibility(View.GONE);
        }
    }

    private void initView() {
        inputTextEt = (EditText)findViewById(R.id.input_et);
        showTextTv = (TextView)findViewById(R.id.showtextTv);
        closeIv = (ImageView)findViewById(R.id.close_iv);
        showLayout = (LinearLayout)findViewById(R.id.show_layout);

        histroyLayout  = (LinearLayout)findViewById(R.id.search_history_layout);
        currentCityTv = (TextView)findViewById(R.id.tv_city_located);
        histroyListView = (ListView)findViewById(R.id.lv_keys_list);
    }

    private void initData(){
        inputTextEt.setText("");
        showTextTv.setText("");
        closeIv.setVisibility(View.GONE);
        showLayout.setVisibility(View.GONE);

        currentCityTv.setText(HomeFragment.mCity);
        searchHistoryAdapter = new SearchHistoryAdapter(this,histroyList);
        histroyListView.setAdapter(searchHistoryAdapter);

    }

    private void initListener(){
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        showLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShopByKey(inputTextEt.getText().toString());
            }
        });
        inputTextEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜查”键*/
                if(actionId == EditorInfo.IME_ACTION_SEARCH && showLayout.getVisibility() == View.VISIBLE){
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken(), 0);
                    }
                    searchShopByKey(inputTextEt.getText().toString());
                    return true;
                }
                return false;
            }
        });

        inputTextEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if (inputStr.length() <= 0) {
                    closeIv.setVisibility(View.GONE);
                    showLayout.setVisibility(View.GONE);
                }else{
                    closeIv.setVisibility(View.VISIBLE);
                    showLayout.setVisibility(View.VISIBLE);
                    showTextTv.setText(inputStr);
                }
            }
        });

        histroyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String key = histroyList.get(index);
                searchShopByKey(key);
            }
        });

        findViewById(R.id.current_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchShopActivity.this, ShopCityActivity.class);
                intent.putExtra("city", currentCityTv.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void searchShopByKey(String key){
        if(TextUtils.isEmpty(key)){
            return;
        }
        histroyLayout.setVisibility(View.VISIBLE);
        if(histroyList != null){
            boolean haveAdd = false;
            for(int i=0;i<histroyList.size();i++){
                if(histroyList.get(i).equals(key)){
                    String temp = histroyList.get(i);
                    histroyList.remove(i);
                    histroyList.add(0,temp);
                    haveAdd = true;
                    break;
                }
            }
            if(!haveAdd){
                histroyList.add(0,key);
                int length = histroyList.size();
                if(length > 4){
                    ArrayList<String> temp = new ArrayList<String>();
                   for(int i=0;i<4;i++){
                       temp.add(histroyList.get(i));
                   }
                    histroyList = temp;
                }
            }
            searchHistoryAdapter.dataList = histroyList;
            searchHistoryAdapter.notifyDataSetChanged();
        }

        if(histroyList != null && !histroyList.isEmpty()){
            CacheUtil.getInstance().saveListCache("search_history",histroyList);
        }

        Intent intent = new Intent(this, ShopCityActivity.class);
        intent.putExtra("city", key);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}
