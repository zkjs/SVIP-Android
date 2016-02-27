package com.zkjinshi.base.config;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import com.zkjinshi.base.util.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 保存配置相关类
 * 开发者：JimmyZhang
 * 日期：2015/7/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ConfigUtil {

    private static ConfigUtil configUtil;
    private Map<String, String> configValue = new HashMap<String, String>();

    /**
     * 根据context和xml配置文件构造FrameworkConfig对象
     *
     * @param context
     * @param xmlConfig
     */
    public ConfigUtil(Context context, int xmlConfig) {
        buildConfig(context, xmlConfig);
    }

    /**
     * @param inputStream
     */
    public ConfigUtil(InputStream inputStream) {
        buildConfig(inputStream);
    }

    /**
     * 根据InputStream取得FrameworkConfig对象
     * 该方法在第一次取得FrameworkConfig对象时必须使用该方法
     *
     * @param inputStream
     * @return FrameworkConfig
     */
    public static ConfigUtil getInst(InputStream inputStream) {
        if (configUtil == null) {
            configUtil = new ConfigUtil(inputStream);
        }
        return configUtil;
    }

    /**
     * 取得FrameworkConfig对象
     * 如果是在第一次取得FrameworkConfig对象，请使用getInst(ctx,cfg);方法
     *
     * @return FrameworkConfig
     */
    public static ConfigUtil getInst() {
        if (configUtil == null) {
            configUtil = new ConfigUtil(null, -1);
        }
        return configUtil;
    }

    /**
     * 根据Context和配置文件的id取得FrameworkConfig对象
     * 该方法在第一次取得FrameworkConfig对象时必须使用该方法
     *
     * @param ctx
     * @param cfg
     * @return FrameworkConfig
     */
    public static ConfigUtil getInst(Context ctx, int cfg) {
        if (configUtil == null) {
            configUtil = new ConfigUtil(ctx, cfg);
        }
        return configUtil;
    }

    /**
     * 获取php请求链接
     * @return
     */
    public String getPhpDomain() {
        return "http://" + getConfigValue(Constants.PHP_HOST)+ "/";
    }

    /**
     * 获取java请求链接
     * @return
     */
    public String getJavaDomain() {
        return "http://" + getConfigValue(Constants.JAVA_HOST)+ "/";
    }

    /**
     * 获得统一登录认证链接
     * @return
     */
    public String getSsoDomain(){
        return "http://" + getConfigValue(Constants.SSO_HOST)+ "/";
    }

    /**
     * 根据标签名取得配置文件中的对应的文本
     *
     * @param key 标签名
     * @return String    对应标签名的文本
     */
    public String getConfigValue(String key) {
        if (key == null || key.equals("")) return null;
        String value = null;
        if (configValue.containsKey(key.toLowerCase()))
            value = configValue.get(key.toLowerCase());
        return value;
    }

    /**
     * 取得配置文件键值对
     *
     * @return
     */
    public Map<String, String> getConfigValue() {
        return configValue;
    }

    /**
     * 设置配置文件键值对
     */
    public void setConfigValue(Map<String, String> configValue) {
        this.configValue = configValue;
    }

    /**
     * 将键值对存入到临时配置文件对象中
     * 修改后可以调用save(Context)将改变保存住
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        this.configValue.put(key.toLowerCase(), value);
    }


    /**
     * 把xmlConfig中的值加载到本对象中
     *
     * @param context
     * @param xmlConfig
     */
    private void buildConfig(Context context, int xmlConfig) {
        XmlResourceParser xrp = context.getResources().getXml(xmlConfig);
        try {
            String tn = "";
            while (xrp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlPullParser.START_TAG) {
                    tn = xrp.getName();
                }
                if (xrp.getEventType() == XmlPullParser.TEXT) {
                    String tv = xrp.getText();
                    this.configValue.put(tn.toLowerCase(), tv);
                }
                xrp.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(this.toString(), "" + e.getDetail());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(this.toString(), "" + e.getMessage());
            e.printStackTrace();
        } finally {
            xrp.close();
        }

    }

    /**
     * 把xmlConfig中的值加载到本对象中
     *
     * @param inputStream
     */
    private void buildConfig(InputStream inputStream) {
        XmlPullParser pullParser = Xml.newPullParser();
        try {
            pullParser.setInput(inputStream, "UTF-8");
            int event = pullParser.getEventType();
            String tn = "";
            String tv = "";
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (!pullParser.getName().toLowerCase().equals("config")) {
                            tn = pullParser.getName();
                            tv = pullParser.nextText();
                            this.configValue.put(tn.toLowerCase(), tv);
                        }
                        break;
                }

                event = pullParser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(this.toString(), "" + e.getDetail());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(this.toString(), "" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(this.toString(), "" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * 将临时配置信息永久性的保存到配置文件
     * 除非将此项目卸载或者清空项目的数据，否则该配置文件会永久性存在
     *
     * @param context
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public void save(Context context) throws IllegalArgumentException, IllegalStateException, IOException {
        File xmlFile = new File(context.getFilesDir(), "config.xml");
        FileOutputStream outStream = new FileOutputStream(xmlFile);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outStream, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "config");

        for (Map.Entry<String, String> item : configValue.entrySet()) {
            serializer.startTag(null, item.getKey());
            serializer.text(item.getValue());
            serializer.endTag(null, item.getKey());
        }
        serializer.endTag(null, "config");
        serializer.endDocument();
        outStream.flush();
        outStream.close();
    }
}
