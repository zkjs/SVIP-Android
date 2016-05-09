package com.zkjinshi.svip.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.adapter.VideoAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.vo.AreaVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dujiande on 2016/5/7.
 */
public class VideoListActivity extends BaseActivity {

    private Context mContext;
    private ListView listView;
    private TextView titleTv;
    private VideoAdapter videoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.video_list_view);
        titleTv = (TextView)findViewById(R.id.title_tv);
     }

    private void initData() {
        //ArrayList<AreaVo> dataList =  ((SVIPApplication)getApplication()).getAreaVolist();
        ArrayList<AreaVo> dataList = new ArrayList<AreaVo>();
        AreaVo areaVo = new AreaVo();
        areaVo.setLocdesc("四季秀");
        areaVo.setVideo_url("http://www.tudou.com/programs/view/EkI_Khntk8o");
        dataList.add(areaVo);
        videoAdapter = new VideoAdapter(dataList,this);
        listView.setAdapter(videoAdapter);
        titleTv.setText("视频直播");

    }

    private void initListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                AreaVo areaVo = (AreaVo)videoAdapter.getItem(index);
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("webview_url",areaVo.getVideo_url());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
