package com.zkjinshi.svip.view.zoomview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.zkjinshi.svip.R;

import me.kaede.tagview.Utils;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class CityDialog extends Dialog implements View.OnClickListener{

     private Activity activity;


     public CityDialog(Context context) {
         super(context);
         this.activity = (Activity)context;

     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setCanceledOnTouchOutside(true);
         init();
     }

     public void init() {
         LayoutInflater inflater = LayoutInflater.from(activity);
         View view = inflater.inflate(R.layout.dialog_city, null);
         setContentView(view);
         view.findViewById(R.id.changsha).setOnClickListener(this);
         view.findViewById(R.id.shenzhen).setOnClickListener(this);
         view.findViewById(R.id.kunming).setOnClickListener(this);

         Window dialogWindow = getWindow();
         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
         dialogWindow.setGravity( Gravity.TOP);

        // lp.x = 100; // 新位置X坐标
         lp.y = Utils.dpToPx(getContext(),40); // 新位置Y坐标
//         lp.width = 300; // 宽度
//         lp.height = 300; // 高度
//         lp.alpha = 0.7f; // 透明度

         // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
         // dialog.onWindowAttributesChanged(lp);
         dialogWindow.setAttributes(lp);
     }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.changsha:
                break;
            case R.id.kunming:
            case R.id.shenzhen:
                Toast.makeText(getContext(),"暂未开通",Toast.LENGTH_SHORT).show();
                break;
        }
        cancel();
    }
}
