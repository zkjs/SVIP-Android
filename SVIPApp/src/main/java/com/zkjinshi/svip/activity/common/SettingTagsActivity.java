package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;



/**
 * Created by djd on 2015/8/20.
 */
public class SettingTagsActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();
    private ItemTitleView mTitle;
    private CircleImageView photoCtv;
    private TextView mNameTv;
    private TextView mFlagTv;
    private ToggleButton mToggleBtn;
    private LinearLayout mContainerLlt;


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
        mContainerLlt     = (LinearLayout)findViewById(R.id.llt_tags_content);
    }

    private void initData() {
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        MainUiController.getInstance().setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), photoCtv);
        mNameTv.setText(CacheUtil.getInstance().getUserPhone());

        mTitle.getmRight().setVisibility(View.VISIBLE);
        mTitle.mRightImage.setVisibility(View.GONE);
        mTitle.mRightText.setVisibility(View.VISIBLE);
        mTitle.mRightText.setText("完成");
        mTitle.setTextTitle("偏好标签");

        loadTags();

    }

    private void loadTags() {

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
                if(isChecked){
                    mFlagTv.setText("公开");
                }else{
                    mFlagTv.setText("不公开");
                }
            }
        });

    }

    //保存喜好标签
    private void saveTags() {

    }
}
