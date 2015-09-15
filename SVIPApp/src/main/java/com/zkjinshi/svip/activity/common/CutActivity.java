package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.zoomview.CropImage;
import com.zkjinshi.svip.view.zoomview.CropImageView;

/**
 * 裁剪图片Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CutActivity extends Activity {

    private TextView centerTitleTv,finishCutTv;
    private ImageButton backIBtn;

    private CropImageView mImageView;
    private Bitmap mBitmap;
    private CropImage mCrop;

    private Button mSave;
    private Button mCancel, rotateLeft, rotateRight;
    private String mPath = "CropImageActivity";
    public int screenWidth = 0;
    public int screenHeight = 0;

    private ProgressBar mProgressBar;

    public static final int SHOW_PROGRESS = 2000;

    public static final int REMOVE_PROGRESS = 2001;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SHOW_PROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case REMOVE_PROGRESS:
                    mHandler.removeMessages(SHOW_PROGRESS);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    break;
            }

        }
    };

    public void initView() {
        centerTitleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        finishCutTv = (TextView)findViewById(R.id.header_bar_tv_finish);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
    }

    public void initData() {

    }

    public void initListeners() {

        // 返回
        backIBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        //完成
        finishCutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = mCrop.saveToLocal(mCrop.cropAndSave(screenWidth));
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut);
        initView();
        init();
        initData();
        initListeners();
    }

    private void init() {
        getWindowWH();
        mPath = getIntent().getStringExtra("path");
        mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
        Bitmap bitmap = null;
        try {
            int degree = ImageUtil.readPictureDegree(mPath);
            bitmap =  ImageUtil.decodeFile(mPath);
            bitmap = ImageUtil.rotaingImageView(bitmap,degree);
            mBitmap = ImageUtil.cropBitmap(bitmap, screenWidth, screenHeight);
            if (mBitmap == null) {
                DialogUtil.getInstance().showCustomToast(CutActivity.this, "没有找到图片", Gravity.CENTER);
                finish();
            } else {
                resetImageView(mBitmap);
            }
        } catch (Exception e) {
            DialogUtil.getInstance().showCustomToast(CutActivity.this, "没有找到图片", Gravity.CENTER);
            finish();
        }
        addProgressbar();
    }

    /**
     * 获取屏幕的高和宽
     */
    private void getWindowWH() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void resetImageView(Bitmap b) {
        mImageView.clear();
        mImageView.setImageBitmap(b);
        mImageView.setImageBitmapResetBase(b, true);
        mCrop = new CropImage(this, mImageView, mHandler);
        mCrop.crop(b);
    }

    protected void addProgressbar() {
        mProgressBar = new ProgressBar(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addContentView(mProgressBar, params);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

}
