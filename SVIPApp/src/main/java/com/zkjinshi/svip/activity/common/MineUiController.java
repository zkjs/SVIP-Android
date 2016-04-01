package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.zkjinshi.svip.R;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;

import java.io.File;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 设置模块选择弹出框控制器
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MineUiController{

    private static MineUiController instance;

    private MineUiController(){}

    public static synchronized MineUiController getInstance(){
        if(null ==  instance){
            instance = new MineUiController();
        }
        return  instance;
    }

    private Context context;
    private Bitmap displayBitmap;
    private String photoFilePath;

    public void init(Context context){
        this.context = context;
    }

    /**
     * 显示选择图片对话框
     */
    public void showChoosePhotoDialog(){

        final Dialog dlg = new Dialog(context, R.style.ActionTheme_DataSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.set_actionsheet_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button takeBtn = (Button) layout.findViewById(R.id.dialog_btn_take);
        Button pickBtn = (Button) layout.findViewById(R.id.dialog_btn_pick);
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
        //拍照
        takeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                String photoFileName = System.currentTimeMillis() + ".jpg";
                CacheUtil.getInstance().savePicName(photoFileName);
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(FileUtil.getInstance().getImageTempPath() + photoFileName)));
                ((Activity)context).startActivityForResult(i, Constants.FLAG_CHOOSE_PHOTO);
                dlg.dismiss();
            }
        });
        //本地选择图片
        pickBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                Intent intent = new Intent(context, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                ((Activity)context).startActivityForResult(intent, Constants.FLAG_CHOOSE_IMG);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();

    }

    /**
     * 选择图片回调
     * @param requestCode
     * @param resultCode
     * @param data
     * @param simpleDraweeView
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data,SimpleDraweeView simpleDraweeView, TextView uploadTv){

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.FLAG_CHOOSE_IMG:// 选择本地图片
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        Intent intent = new Intent(context,
                                CutActivity.class);
                        intent.putExtra("path", pathList.get(0));
                        ((Activity)context).startActivityForResult(intent,
                                Constants.FLAG_MODIFY_FINISH);
                    }
                    break;

                case Constants.FLAG_CHOOSE_PHOTO:// 打开照相机
                    Intent intent = new Intent(context, CutActivity.class);
                    String photoFileName = CacheUtil.getInstance().getPicName();
                    intent.putExtra("path", FileUtil.getInstance().getImageTempPath()+ photoFileName);
                    ((Activity)context). startActivityForResult(intent, Constants.FLAG_MODIFY_FINISH);
                    break;

                case Constants.FLAG_MODIFY_FINISH:// 修改完成
                    if (data != null) {
                        photoFilePath = data.getStringExtra("path");
                    }else{
                        photoFilePath =  FileUtil.getInstance().getImageTempPath() + CacheUtil.getInstance().getPicName();
                    }
                    simpleDraweeView.setImageURI(Uri.parse("file://"+photoFilePath));
                    CacheUtil.getInstance().savePicPath(photoFilePath);
                    if(uploadTv != null){
                        uploadTv.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
