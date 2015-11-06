package com.zkjinshi.svip.net.ext;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.zkjinshi.base.util.DialogUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 下载请求任务类
 * 开发者：JimmyZhang
 * 日期：2015/9/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DownloadTask extends AsyncTask<String,Integer,String>{

    private DownloadRequestListener downloadRequestListener;
    private String downloadUrl;
    private String filePath;
    private Context context;

    public boolean isShowLoadingDialog = true;

    public DownloadTask(Context context,String downloadUrl,String filePath){
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.filePath = filePath;
    }

    public DownloadTask(Context context,String downloadUrl,String filePath,DownloadRequestListener downloadRequestListener){
        this(context,downloadUrl,filePath);
        this.downloadRequestListener = downloadRequestListener;
    }

    public void setDownloadRequestListener(DownloadRequestListener downloadRequestListener) {
        this.downloadRequestListener = downloadRequestListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(null != downloadRequestListener){
            downloadRequestListener.beforeDownloadRequestStart();
        }
        if(isShowLoadingDialog){
            DialogUtil.getInstance().showProgressDialog(context);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        try {
            inputStream = HttpGetUtil.getInputStream(downloadUrl);
            if(null != inputStream){
                if(!TextUtils.isEmpty(filePath)){
                    HttpGetUtil.saveFile(inputStream,filePath);
                }
            }
            if(null != downloadRequestListener){
                downloadRequestListener.onDownloadResponseSucceed(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(null != downloadRequestListener){
                downloadRequestListener.onDownloadRequestError(e.getMessage());
            }
        } finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    @Override
    protected void onPostExecute(String filePath) {
        if (isShowLoadingDialog) {
            DialogUtil.getInstance().cancelProgressDialog();
        }
        super.onPostExecute(filePath);
    }

}
