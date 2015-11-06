package com.zkjinshi.svip.net.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http Get请求工具类
 * 开发者：JimmyZhang
 * 日期：2015/9/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HttpGetUtil {

    /**
     * 获取文件下载流
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String downloadUrl)
            throws IOException
    {
        URL url = new URL(downloadUrl);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        InputStream inputStream = httpConn.getInputStream();
        return inputStream;
    }


    /**
     * 保存下载文件
     * @param inputStream
     * @param filePath
     */
    public static void saveFile( InputStream inputStream , String filePath) {
        OutputStream ouput = null;
        try {
            File file  = new File(filePath);
            ouput =new FileOutputStream(file);
            byte buffer[] = new byte[ 4 * 1024 * 1024 ];
            int length = 0;
            while((length = inputStream.read(buffer)) != -1) {
                ouput.write(buffer,0,length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(null != ouput){
                    ouput.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
