package com.zkjinshi.svip.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;

import com.zkjinshi.svip.activity.common.MainActivity;

/**
 * Created by dujiande on 2016/4/7.
 */
public class GaoshiBlur {

    public static void applyBlur( View showView, Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bmp1 = view.getDrawingCache();
        int height = FastBlur.getOtherHeight(activity);
        /**
         * 除去状态栏和标题栏
         */
        Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);
        blur(bmp2,showView,activity);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void blur(Bitmap bkg, View showView, Activity activity) {
        long startMs = System.currentTimeMillis();
        float radius = 5;

        bkg = small(bkg);
        Bitmap bitmap = bkg.copy(bkg.getConfig(), true);

        final RenderScript rs = RenderScript.create(activity);
        final Allocation input = Allocation.createFromBitmap(rs, bkg, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        bitmap = big(bitmap);
        showView.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
        rs.destroy();
        Log.d("zhangle","blur take away:" + (System.currentTimeMillis() - startMs )+ "ms");
    }

    public static  Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(4f,4f); //长和宽放大缩小的比例
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return resizeBmp;
    }

    public static  Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.25f,0.25f); //长和宽放大缩小的比例
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return resizeBmp;
    }
}
