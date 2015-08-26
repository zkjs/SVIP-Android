package com.zkjinshi.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ImageUtil {

    /**
     * 缩放或放大图片缩略图
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap loadThumbBitmap(Context context,Bitmap bitmap){
        int rawWidth = bitmap.getWidth();   //原宽度
        int rawHeight = bitmap.getHeight(); //原高度
        int maxWH = 99;  //显示最大宽度或高度
        int minWH = 49;  //显示最小宽度或高度
        float scale = 0; //缩放或放大倍数

        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        float density  = dm.density;  // 屏幕密度（像素比例）
        //计算缩放或放大倍数
        if (rawWidth > maxWH || rawHeight > maxWH) {
            if (rawWidth < rawHeight) {
                scale = density*maxWH / rawHeight;
            } else {
                scale = density*maxWH / rawWidth;
            }
        } else if (rawWidth < minWH || rawHeight < minWH){
            if (rawWidth < rawHeight) {
                scale = density*minWH / rawHeight;
            } else {
                scale = density*minWH / rawWidth;
            }
        } else {
            scale = density;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeight, matrix, true);

        return newbmp;
    }

    /**
     * 图片缩略图
     * @param bitmap
     * @return
     */
    public static Bitmap cropThumbBitmap(Bitmap bitmap){

        return cropBitmap(bitmap, 198, 198);
    }

    /**
     * 图片截剪
     * @param bitmap
     * @return
     */
    public static Bitmap cropBigBitmap(Bitmap bitmap){

        return cropBitmap(bitmap, 960, 960);
    }

    /**
     * 把图片截剪
     * @param bitmap
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        int rawWidth = bitmap.getWidth();   //原宽度
        int rawHeight = bitmap.getHeight(); //原高度
        float scale = 0;  //缩放或放大倍数

        if (rawWidth < reqWidth && rawHeight < reqHeight) {

            return bitmap;
        } else {
            if (rawWidth < rawHeight) {
                scale = ((float)reqHeight / rawHeight);
            } else {
                scale = ((float)reqWidth / rawWidth);
            }
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeight, matrix, true);

        return newbmp;

    }

    /**
     * 获得缩放图片
     * @param bitmap
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap,Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int heigh = dm.heightPixels;
        bitmap = cropBitmap(bitmap,width,heigh);
        return bitmap;
    }

    /**
     * 保存图片(发给服务器)
     * @param inPath  选中的图片路径
     * @param outPath 压缩后输出路径
     * @return
     */
    public static void saveBigBitmap(String inPath, String outPath) {
        Bitmap bitmap = decodeFile(inPath);
        int pictureDegree = readPictureDegree(inPath);
        bitmap = rotaingImageView(bitmap, pictureDegree);//
        saveBitmap2JPGE(cropBigBitmap(bitmap), outPath);
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            System.out.println("pic degree:" + degree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotaingImageView(Bitmap bitmap, int degree) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 获得缩放图片Bitmap
     * @param inPath
     * @return
     */
    public static Bitmap decodeFile(String inPath){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inPath, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1024*1024);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(inPath, opts);
    }

    /**
     * 计算缩放比例
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 计算缩放比例
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * Bitmap保存为JPEG图片
     * @param bitmap
     * @param path
     */
    public static void saveBitmap2JPGE(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(!bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
    }

    public static void saveBitmap(Bitmap bitmap, String savePath) {
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
