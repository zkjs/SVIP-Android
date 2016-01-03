package com.zkjinshi.svip.net;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FileUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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


    public static int TIMEOUT = 3*1000;  //超时时间

    /**
     * 发送Get请求
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String sendGetRequest(String requestUrl) throws Exception{
        String result = null;
        HttpGet httpRequest = new HttpGet(requestUrl);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                HttpProtocolParams.USER_AGENT,
                "android");
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
        HttpResponse httpResponse = httpclient.execute(httpRequest);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            result = EntityUtils.toString(httpResponse.getEntity());
        }
        return result;
    }

    public static String sendPostRequest(String requestUrl,HashMap<String,String> bizParamsMap) throws Exception{
        String resultInfo = null;
        JSONObject jsonObject = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        jsonObject = new JSONObject();
        if (null != bizParamsMap) {
            Iterator<Map.Entry<String, String>> bizIterator = bizParamsMap.entrySet()
                    .iterator();
            while (bizIterator.hasNext()) {
                HashMap.Entry bizEntry = (HashMap.Entry) bizIterator.next();
                jsonObject.put(bizEntry.getKey().toString(),bizEntry
                        .getValue().toString());
            }
        }
        out.write(jsonObject.toString().getBytes("UTF-8"));
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        int responseCode =  connection.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            resultInfo = sb.toString();
            reader.close();
        }
        connection.disconnect();
        return  resultInfo;
    }

    public static String sendJsonPostRequest(String requestUrl,HashMap<String,Object> objectParamsMap) throws Exception{
        String resultInfo = null;
        JSONObject jsonObject = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.connect();
        // POST请求
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());
        JSONObject obj = new JSONObject();

        if (null != objectParamsMap) {
            Iterator<Map.Entry<String, Object>> bizIterator = objectParamsMap.entrySet().iterator();
            while (bizIterator.hasNext()) {
                HashMap.Entry<String, Object> bizEntry = (HashMap.Entry<String, Object>) bizIterator.next();
                obj.put(bizEntry.getKey(),bizEntry.getValue());
            }
        }
//        obj.put("data", objectParamsMap.get("data"));
//        obj.put("category", "0");

        out.write(obj.toString().getBytes("UTF-8"));// 这样可以处理中文乱码问题
        out.flush();
        out.close();

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String lines;
        StringBuffer sb = new StringBuffer("");
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            sb.append(lines);
        }
        resultInfo = sb.toString();
        reader.close();

        return  resultInfo;
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
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
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
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
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
