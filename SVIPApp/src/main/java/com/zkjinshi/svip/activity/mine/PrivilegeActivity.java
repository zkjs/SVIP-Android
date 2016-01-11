package com.zkjinshi.svip.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.view.ItemTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开发者：dujiande
 * 日期：2015/11/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PrivilegeActivity  extends BaseActivity {

    private final static String TAG = PrivilegeActivity.class.getSimpleName();

    private ItemTitleView title;
    private GridView gridView;

    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.mipmap.ic_mianqiantai,R.mipmap.ic_jifen,R.mipmap.ic_zhuanshukefu , R.mipmap.ic_gengduojianshezhong };
    private String[] iconName = { "免前台", "积分", "专属客服", "更多建设中"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privilege);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        title = (ItemTitleView)findViewById(R.id.Itv_title);
        gridView = (GridView)findViewById(R.id.gridview);
    }

    private void initData() {
        title.setTextTitle("特权");
        title.setTextColor(this, R.color.White);
        title.getmRight().setVisibility(View.GONE);


        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_privilege, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);
    }

    private void initListener() {
        title.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(iconName[i].equals("免前台")){
                    Intent intent = new Intent(PrivilegeActivity.this, WebViewActivity.class);
                    intent.putExtra("webview_url","http://iwxy.cc/mqt/");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                }else  if(iconName[i].equals("积分")){

                }
                else  if(iconName[i].equals("专属客服")){

                }else  if(iconName[i].equals("更多建设中")){

                }

            }
        });
    }

    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }
}