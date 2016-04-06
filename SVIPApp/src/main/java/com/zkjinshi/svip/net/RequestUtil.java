package com.zkjinshi.svip.net;


import android.text.TextUtils;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FileUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
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

    public static int CONNECT_TIMEOUT = 3*1000;//连接超时时间
    public static int SO_TIMEOUT = 5*1000;//请求超时时间

    /**
     * 发送Get请求
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String sendGetRequest(String requestUrl,NetRequestListener requestListener) throws Exception{
        String result = null;
        HttpGet httpRequest = new HttpGet(requestUrl);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                HttpProtocolParams.USER_AGENT,
                "android");
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            httpRequest.addHeader("Token",token);
        }
        HttpResponse httpResponse = httpclient.execute(httpRequest);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK)
        {
            result = EntityUtils.toString(httpResponse.getEntity());
        }else if(statusCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
        return result;
    }

    public static String sendPostRequest(String requestUrl,HashMap<String,String> bizParamsMap,NetRequestListener requestListener) throws Exception{
        String resultInfo = null;
        JSONObject jsonObject = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(SO_TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            connection.setRequestProperty("Token",token);
        }
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
        int responseCode =  connection.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
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
        }else if(responseCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
        connection.disconnect();
        return  resultInfo;
    }

    public static String sendJsonPostRequest(String requestUrl,HashMap<String,Object> objectParamsMap,NetRequestListener requestListener) throws Exception{
        String resultInfo = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(SO_TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            connection.setRequestProperty("Token",token);
        }
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
        out.write(obj.toString().getBytes("UTF-8"));// 这样可以处理中文乱码问题
        out.flush();
        out.close();
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
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
        }else if(responseCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
        return  resultInfo;
    }

    public static String sendPutRequest(String requestUrl,HashMap<String,Object> objectParamsMap,NetRequestListener requestListener) throws Exception{
        String resultInfo = null;
        JSONObject jsonObject = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(SO_TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("PUT");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            connection.setRequestProperty("Token",token);
        }
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
        out.write(obj.toString().getBytes("UTF-8"));// 这样可以处理中文乱码问题
        out.flush();
        out.close();
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
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
        }else if(responseCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
        return  resultInfo;
    }

    public static String sendDeleteRequest(String requestUrl,HashMap<String,Object> objectParamsMap,NetRequestListener requestListener) throws Exception{
        String resultInfo = null;
        JSONObject jsonObject = null;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(SO_TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("DELETE");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            connection.setRequestProperty("Token",token);
        }
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
        out.write(obj.toString().getBytes("UTF-8"));// 这样可以处理中文乱码问题
        out.flush();
        out.close();
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
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
        }else if(responseCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
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
    public static String sendPostRequest(String requestUrl, HashMap<String, String> bizParamsMap, HashMap<String, String> fileParamsMap,NetRequestListener requestListener) throws Exception {
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
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        httpPost.setEntity(multipartEntity);
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            httpPost.addHeader("Token",token);
        }
        HttpResponse response = httpClient.execute(httpPost);
        int respCode = 0;
        if (response != null && null != response.getStatusLine()) {
            respCode = response.getStatusLine().getStatusCode();
            if(respCode == HttpStatus.SC_OK ){
                resultInfo = EntityUtils.toString(response.getEntity(),"UTF-8");
            }else if(respCode == HttpStatus.SC_UNAUTHORIZED){
                if(null != requestListener){
                    requestListener.onCookieExpired();
                }
            }
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
                              Map<String, File> fileMap,NetRequestListener requestListener) throws IOException {
        String result = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(CONNECT_TIMEOUT);
        conn.setConnectTimeout(SO_TIMEOUT);
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
        int responseCode = conn.getResponseCode();
        if(responseCode == HttpStatus.SC_OK){
            InputStream in = conn.getInputStream();
            InputStreamReader isReader = new InputStreamReader(in);
            BufferedReader bufReader = new BufferedReader(isReader);
            String line = null;
            result = new String();
            while ((line = bufReader.readLine()) != null){
                result += line;
            }
            outStream.close();
        }else if(responseCode == HttpStatus.SC_UNAUTHORIZED){
            if(null != requestListener){
                requestListener.onCookieExpired();
            }
        }
        conn.disconnect();
        return result.toString();
    }



}
