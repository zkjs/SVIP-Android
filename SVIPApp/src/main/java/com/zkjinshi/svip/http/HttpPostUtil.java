package com.zkjinshi.svip.http;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.vo.MimeType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * post请求封装类
 * 开发者：JimmyZhang
 * 日期：2015/7/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HttpPostUtil {

    /**
     * post请求发送图片
     * @param actionUrl
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public static String post(String actionUrl, Map<String, String> params,
                              Map<String, File> files) throws IOException {
        return post(actionUrl,params,files,null);
    }

    /**
     * post请求发送图片
     * @param actionUrl
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public static String post(String actionUrl, Map<String, String> params,
                              Map<String, File> files,MimeType mimeType) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(500 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(conn
                .getOutputStream());
        outStream.write(sb.toString().getBytes());

        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"" + "; filename=\""
                                + FileUtil.getInstance().getFileName(file.getValue().getPath()) + "\"" + LINEND);
                LogUtil.getInstance().info(LogLevel.INFO, "requestContent:" + sb1.toString());
                if(null ==  mimeType){
                    sb1.append("Content-Type: image/jpeg; charset="
                            + CHARSET + LINEND);
                }else{
                    if(mimeType == MimeType.IMAGE){//图片
                        sb1.append("Content-Type: image/jpeg; charset="
                                + CHARSET + LINEND);
                    }else if(mimeType == MimeType.AUDIO){//语音
                        sb1.append("Content-Type: multipart/form-data; charset="
                                + CHARSET + LINEND);
                    }else if(mimeType == MimeType.VIDEO){//视频
                        sb1.append("Content-Type: video/mpeg4; charset="
                                + CHARSET + LINEND);
                    }else if(mimeType == MimeType.APPLICATION){//应用文件
                        sb1.append("Content-Type:application/octet-stream; charset="
                                + CHARSET + LINEND);
                    }
                }
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[8*1024*1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(in);
        BufferedReader bufReader = new BufferedReader(isReader);
        String line = null;
        String result = new String();
        if (res == 200) {
            while ((line = bufReader.readLine()) != null){
                result += line;
            }
        }
        outStream.close();
        conn.disconnect();
        return result.toString();
    }
}
