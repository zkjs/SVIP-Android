package com.zkjinshi.svip.utils;

import android.content.Context;

import java.io.InputStream;

import cz.msebera.android.httpclient.util.EncodingUtils;

/**
 * Created by dujiande on 2016/6/23.
 */
public class AssetUtil {

    public static String getContent(Context context, String fileName){

        String ret = "";
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            int len = is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            ret = EncodingUtils.getString(buffer, "utf-8");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
