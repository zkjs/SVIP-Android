package com.zkjinshi.svip.activity.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.vo.YunBaMsgVo;

/**
 * Created by dujiande on 2016/4/5.
 */
public class BeaconMsgActivity extends BaseActivity{

    private final static String TAG = BeaconMsgActivity.class.getSimpleName();

    private Context mContext;
    private TextView titleTv,contentTv;
    private LinearLayout contentLlt;
    private RelativeLayout mohuRlt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_msg);

        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.ad_title_tv);
        contentTv = (TextView)findViewById(R.id.ad_des_tv);
        contentLlt = (LinearLayout)findViewById(R.id.content_llt);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
    }

    private void initData() {
        YunBaMsgVo yunBaMsgVo = (YunBaMsgVo) getIntent().getSerializableExtra("data");
        if(yunBaMsgVo != null){
            titleTv.setText(yunBaMsgVo.getTitle());
            contentTv.setText(yunBaMsgVo.getContent());
        }else{
            titleTv.setText("");
            contentTv.setText("");
        }

        applyBlur();
    }

    private void initListener() {
        contentLlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });
    }


    private void applyBlur() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap mScreenBitmap = view.getDrawingCache();

                if(mScreenBitmap != null){
                    blur(mScreenBitmap);
                }
            }
        },2000);


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float radius = 20;

        bkg = small(bkg);
        Bitmap bitmap = bkg.copy(bkg.getConfig(), true);

        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, bkg, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        bitmap = big(bitmap);
        mohuRlt.setBackground(new BitmapDrawable(getResources(), bitmap));
        rs.destroy();
        Log.d("zhangle","blur take away:" + (System.currentTimeMillis() - startMs )+ "ms");
    }

    private  Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(4f,4f); //长和宽放大缩小的比例
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return resizeBmp;
    }

    private  Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.25f,0.25f); //长和宽放大缩小的比例
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return resizeBmp;
    }


}
