package com.zkjinshi.svip.activity.common;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.adapter.CommentAdapter;
import com.zkjinshi.svip.adapter.CommentAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.sqlite.AreaCommentDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.AreaVo;
import com.zkjinshi.svip.vo.CommentVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/5/7.
 */
public class AreaDetailActivity extends BaseActivity {

    private Context mContext;
    private View headLayout;
    private View footLayout;
    private ListView listView;
    private TextView titleTv,contentTv;
    private EditText commentInputEt;
    private CommentAdapter commentAdapter;
    private Button commentBtn;
    private AreaVo currentArea = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.comment_list_view);
        titleTv = (TextView)findViewById(R.id.title_tv);

        headLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_header,null);
        footLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_footer,null);

        listView.addHeaderView(headLayout);
        listView.addFooterView(footLayout);

        contentTv = (TextView) headLayout.findViewById(R.id.content_tv);
        commentInputEt = (EditText)footLayout.findViewById(R.id.inputEt);
        commentBtn = (Button)footLayout.findViewById(R.id.comment_btn);
    }

    private void initData() {
        currentArea = (AreaVo) getIntent().getSerializableExtra("currentAreaVo");
        ArrayList<CommentVo> dataList =  new ArrayList<CommentVo>();
        ArrayList<CommentVo> dblist = AreaCommentDBUtil.getInstance().queryComments();
        if(dblist != null){
            dataList = dblist;
        }
        commentAdapter = new CommentAdapter(dataList,this);
        listView.setAdapter(commentAdapter);
        titleTv.setText("区域简介");
        if(currentArea != null){
            titleTv.setText(currentArea.getLocdesc()+"区域简介");
            contentTv.setText(currentArea.getBrief());
        }
    }

    private void initListener() {
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentStr = commentInputEt.getText().toString();
                if(TextUtils.isEmpty(commentStr)){
                    Toast.makeText(mContext,"内容不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                CommentVo commentVo = new CommentVo();
                commentVo.setArea_key(currentArea.getKey());
                commentVo.setComment(commentStr);
                commentVo.setAvatarUrl(CacheUtil.getInstance().getUserPhotoUrl());
                commentVo.setTimestamp(System.currentTimeMillis());
                commentVo.setName(CacheUtil.getInstance().getUserName());
                AreaCommentDBUtil.getInstance().insertComment(commentVo);
                ArrayList<CommentVo> datalist = AreaCommentDBUtil.getInstance().queryComments();
                if(datalist != null ){
                    commentAdapter.refresh(datalist);
                    listView.setSelection(datalist.size() -1);
                }

            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
