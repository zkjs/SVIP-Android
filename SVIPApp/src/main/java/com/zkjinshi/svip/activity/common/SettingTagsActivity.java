package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;

import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.TagInfoVo;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


/**
 * Created by djd on 2015/8/20.
 */
public class SettingTagsActivity extends Activity{

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();
    private ItemTitleView mTitle;
    private CircleImageView photoCtv;
    private TextView mNameTv;
    private TextView mFlagTv;
    private ToggleButton mToggleBtn;
    private LinearLayout mContainerCheckedLlt;
    private LinearLayout mContainerUnCheckedLlt;
    private TagView mCheckedTagView;
    private TagView mUnCheckTagView;

    private boolean mIsTagOpen;
    private Response.ErrorListener saveTagsErrorListener;
    private Response.Listener<String> saveTagsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_tags);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        photoCtv          = (CircleImageView) findViewById(R.id.user_photo_civ);
        mNameTv           = (TextView) findViewById(R.id.tv_name);
        mFlagTv           = (TextView) findViewById(R.id.tv_flag);
        mToggleBtn        = (ToggleButton)findViewById(R.id.toggleButton);
        mContainerCheckedLlt     = (LinearLayout)findViewById(R.id.llt_checked_tags);
        mContainerUnCheckedLlt     = (LinearLayout)findViewById(R.id.llt_unchecked_tags);
        mCheckedTagView   = (TagView)findViewById(R.id.tagview_checked);
        mUnCheckTagView   = (TagView)findViewById(R.id.tagview_unchecked);
    }

    private void initData() {
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        MainUiController.getInstance().setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), photoCtv);
        mNameTv.setText(CacheUtil.getInstance().getUserName());

        mTitle.getmRight().setVisibility(View.VISIBLE);
        mTitle.mRightImage.setVisibility(View.GONE);
        mTitle.mRightText.setVisibility(View.VISIBLE);
        mTitle.mRightText.setText("完成");
        mTitle.setTextTitle("偏好标签");

        mIsTagOpen = CacheUtil.getInstance().getTagsOpen();
        mToggleBtn.setChecked(mIsTagOpen);
        //test();
        loadUserTagsInfo();
        loadRandTagsInfo();
    }

   //加载用户选择的标签
    private void loadUserTagsInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                ProtocolUtil.getUserTagsUrl(CacheUtil.getInstance().getToken(),
                        CacheUtil.getInstance().getUserId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取用户标签信息响应结果" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                GsonBuilder gsonb = new GsonBuilder();
                                Gson gson = gsonb.create();
                                ArrayList<TagInfoVo> responseList = gson.fromJson(response, new TypeToken<ArrayList<TagInfoVo>>() {
                                }.getType());
                                if(responseList.size() > 0){
                                    mContainerCheckedLlt.setVisibility(View.VISIBLE);
                                    findViewById(R.id.white_line).setVisibility(View.GONE);
                                    for(TagInfoVo item : responseList){
                                        LogUtil.getInstance().info(LogLevel.INFO, item.toString());
                                        mCheckedTagView.addTag(createTag(item.getTagid(),item.getTag()));
                                    }
                                } else{
                                    mContainerCheckedLlt.setVisibility(View.GONE);
                                    findViewById(R.id.white_line).setVisibility(View.VISIBLE);
                                }


                            }catch (Exception e){
                                e.printStackTrace();
                                mContainerCheckedLlt.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户标签信息错误信息:" +  error.getMessage());
            }
        });
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    //加载随机选择的标签
    private void loadRandTagsInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                ProtocolUtil.getRandTagsUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取随机标签信息响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                GsonBuilder gsonb = new GsonBuilder();
                                Gson gson = gsonb.create();
                                ArrayList<TagInfoVo> responseList = gson.fromJson(response, new TypeToken<ArrayList<TagInfoVo>>() {
                                }.getType());
                                for(TagInfoVo item : responseList){
                                    LogUtil.getInstance().info(LogLevel.INFO, item.toString());
                                    mUnCheckTagView.addTag(createTag(item.getTagid(),item.getTag()));
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取随机标签信息错误信息:" +  error.getMessage());
            }
        });
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTitle.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTags();
            }
        });

        mToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsTagOpen = isChecked;
                if (isChecked) {
                    mFlagTv.setText("公开");
                } else {
                    mFlagTv.setText("隐藏");
                }
            }
        });

        mCheckedTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                mCheckedTagView.remove(position);
                mUnCheckTagView.addTag(tag);
            }
        });

        mUnCheckTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                mUnCheckTagView.remove(position);
                mContainerCheckedLlt.setVisibility(View.VISIBLE);
                mCheckedTagView.addTag(tag);
            }
        });

    }



    private Tag createTag(int id,String tagstr){
        Tag tag = new Tag(id,tagstr);
        tag.tagTextColor = Color.parseColor("#333333");
        tag.layoutColor =  Color.parseColor("#ffffff");
        tag.layoutColorPress = Color.parseColor("#DDDDDD");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 20f;
        tag.tagTextSize = 14f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#bbbbbb");
        tag.isDeletable = false;
       return tag;
    }

    //保存喜好标签
    private void saveTags() {
        createSaveTagsListenr();
        DataRequestVolley requestString = new DataRequestVolley(
                HttpMethod.POST,ProtocolUtil.getPostTagsUrl(), saveTagsListener, saveTagsErrorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("tagid", getCheckedTagIds() );
                map.put("tagopen", mIsTagOpen?"1":"0" );
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.INFO, requestString.toString());
        DialogUtil.getInstance().showProgressDialog(this);
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(requestString);
    }

    //注册提交标签时的监听器
    private void createSaveTagsListenr() {
        saveTagsListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.getInstance().info(LogLevel.INFO,response.toString());
                DialogUtil.getInstance().cancelProgressDialog();
                if(JsonUtil.isJsonNull(response))
                    return ;
                //解析json数据
                Gson gson = new Gson();
                BaseResponse baseResponseVo = gson.fromJson(response, BaseResponse.class);
                if(baseResponseVo.isSet()){
                    CacheUtil.getInstance().saveTagsOpen(mIsTagOpen);
                    DialogUtil.getInstance().showToast(SettingTagsActivity.this, "保存标签信息成功");
                    finish();
                }else{
                    DialogUtil.getInstance().showToast(SettingTagsActivity.this,"保存标签信息失败");
                }
            }
        };

        saveTagsErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                volleyError.printStackTrace();
                DialogUtil.getInstance().cancelProgressDialog();
                DialogUtil.getInstance().showToast(SettingTagsActivity.this, "保存标签信息失败");
                LogUtil.getInstance().info(LogLevel.INFO, "保存标签信息失败"+volleyError.toString());
            }
        };
    }

    //获取用逗号分割的tagid
    public String getCheckedTagIds(){
        String idsString = "";
        List<Tag> tags = mCheckedTagView.getTags();
       for(int i=0;i<tags.size();i++){
           Tag tag = tags.get(i);
           if(i == 0){
               idsString = idsString + tag.id;
           }else{
               idsString = idsString + "," + tag.id;
           }
       }
        return idsString;
    }
}
