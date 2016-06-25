package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.CustomImgDialog;
import com.zkjinshi.svip.vo.CallReadyVo;

/**
 * Created by dujiande on 2016/6/25.
 */
public class MessageUtil {
    public static void showImgToast(Context mContext, String msg){
//        final CustomImgDialog.Builder customBuilder = new CustomImgDialog.Builder(mContext);
//        customBuilder.setPositiveButton(msg);
//        final CustomImgDialog dialog= customBuilder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//            }
//        },500);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_img_dialog, null);
        TextView btnTv = (TextView)view.findViewById(R.id.btnTv);
        btnTv.setText(msg);
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

}
