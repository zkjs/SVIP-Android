package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.base.BaseUiController;
import com.zkjinshi.svip.activity.common.CutActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;

import java.io.File;

/**
 * 商品列表UI控制层
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListUiController extends BaseUiController{

    private static GoodListUiController instance;

    private GoodListUiController(){}

    public static synchronized GoodListUiController getInstance(){
        if(null ==  instance){
            instance = new GoodListUiController();
        }
        return  instance;
    }

    private Context context;

    public void init(Context context){
        this.context = context;
    }

    /**
     * 显示选择预定布局
     * @param view
     */
    public void showChooseAndPayLaout(final View view){
        Animation showAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        view.startAnimation(showAnimation);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 隐藏选择预定布局
     * @param view
     */
    public  void hiddleChooseAndPayLaout(final View view){
        Animation hiddleAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom);
        view.startAnimation(hiddleAnimation);
        hiddleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
