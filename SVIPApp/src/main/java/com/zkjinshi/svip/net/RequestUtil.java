package com.zkjinshi.svip.net;

import com.zkjinshi.svip.utils.FileUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 网络请求帮助类
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RequestUtil {

    /**
     * 发送Get请求
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String sendGetRequest(String requestUrl) throws Exception{
        StringBuffer buffer = new StringBuffer();
        URL url = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
        httpUrlConn.setDoOutput(false);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        httpUrlConn.setRequestMethod("GET");
        httpUrlConn.connect();
        InputStream inputStream = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        httpUrlConn.disconnect();
        return buffer.toString();
    }

    /**
     * 发送post请求
     * @param requestUrl
     * @param bizParamsMap
     * @param fileParamsMap
     * @return
     * @throws Exception
     */
    public static String sendPostRequest(String requestUrl, HashMap<String, String> bizParamsMap, HashMap<String, String> fileParamsMap) throws Exception {
        String resultInfo = null;
        MultipartEntity multipartEntity = new MultipartEntity();
        if (null != bizParamsMap) {
            Iterator<Map.Entry<String, String>> bizIterator = bizParamsMap.entrySet()
                    .iterator();
            while (bizIterator.hasNext()) {
                HashMap.Entry bizEntry = (HashMap.Entry) bizIterator.next();
                StringBody bizStringBody = new StringBody((URLEncoder.encode(bizEntry
                        .getValue().toString(), "UTF-8")));
                multipartEntity.addPart(bizEntry.getKey().toString(), bizStringBody);
            }
        }
        if (null != fileParamsMap) {
            String filePath = null;
            File file = null;
            FileBody fileBody = null;
            Iterator<Map.Entry<String, String>> fileIterator = fileParamsMap.entrySet()
                    .iterator();
            while (fileIterator.hasNext()) {
                HashMap.Entry fileEntry = (HashMap.Entry) fileIterator.next();
                filePath = (String) fileEntry.getValue();
                file = new File(filePath);
                fileBody = new FileBody(file);
                multipartEntity.addPart(URLEncoder.encode((String) fileEntry.getKey(), "UTF-8"), fileBody);
            }
        }
        HttpPost httpPost = new HttpPost(requestUrl);
        HttpClient httpClient = new DefaultHttpClient();
        httpPost.setEntity(multipartEntity);
        HttpResponse response = httpClient.execute(httpPost);
        int respCode = 0;
        if (response != null && null != response.getStatusLine() && ((respCode = response.getStatusLine().getStatusCode()) == HttpStatus.SC_OK )) {
            resultInfo = EntityUtils.toString(response.getEntity(),"UTF-8");
        }
        return  resultInfo;
    }

    /**
     * post上传文件
     * @param requestUrl
     * @param bizParamsMap
     * @param fileMap
     * @return
     * @throws IOException
     */
    public static String sendPostRequest(String requestUrl, Map<String, String> bizParamsMap,
                              Map<String, File> fileMap) throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(500 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : bizParamsMap.entrySet()) {
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
        if (fileMap != null)
            for (Map.Entry<String, File> file : fileMap.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"" + "; filename=\""
                        + FileUtil.getInstance().getFileName(file.getValue().getPath()) + "\"" + LINEND);
                sb1.append("Content-Type: image/jpeg; charset="
                        + CHARSET + LINEND);
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
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
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
