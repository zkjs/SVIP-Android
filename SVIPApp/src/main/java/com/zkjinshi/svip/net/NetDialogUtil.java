package com.zkjinshi.svip.net;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;

import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.base.BaseApplication;

/**
 * Created by dujiande on 2015/12/17.
 */
public class NetDialogUtil {

    public static void showLoginDialog(final Activity activity){
        final CustomDialog.Builder customerBuilder = new CustomDialog.Builder(activity);
        customerBuilder.setTitle("登录");
        customerBuilder.setMessage("Token失效，请重新登录!");
        customerBuilder.setGravity(Gravity.CENTER);
//        customerBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        customerBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                BaseApplication.getInst().clearLeaveTop();
                Intent intent = new Intent(activity,LoginActivity.class);
                activity.startActivity(intent);
            }
        });
        customerBuilder.create().show();
    }
}
