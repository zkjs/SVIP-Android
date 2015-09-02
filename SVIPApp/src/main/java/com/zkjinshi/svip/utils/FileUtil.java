package com.zkjinshi.svip.utils;

import android.os.Environment;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件操作相关工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FileUtil {
    private FileUtil(){}

    private static FileUtil instance;

    public synchronized static FileUtil getInstance(){
        if(null ==  instance){
            instance = new FileUtil();
        }
        return instance;
    }

    public static final String FOLDER_SVIP = "com.zkjinshi.svip";
    public static final String FOLDER_AUDIO = "audios";
    public static final String FOLDER_IMAGE = "images";
    public static final String FOLDER_APPLICATION = "applications";
    public static final String FOLDER_VIDEO = "videos";
    public static final String FOLDER_TEMP_IMAGE = "temps";

    public static final String DIRPATH_AUDIO = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_AUDIO + "/";

    /**
     * 获取语音文件本地路径
     * @return
     */
    public String getAudioPath(){
        File file = new File(DIRPATH_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_AUDIO;
    }

    public static final String DIRPATH_VIDEO = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_VIDEO + "/";

    /**
     * 获取语音文件本地路径
     * @return
     */
    public String getVideoPath(){
        File file = new File(DIRPATH_VIDEO);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_VIDEO;
    }

    public static final String DIRPATH_IMAGE = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_IMAGE + "/";

    /**
     * 获取图片文件本地路径
     * @return
     */
    public String getImagePath(){
        File file = new File(DIRPATH_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_IMAGE;
    }

    public static final String DIRPATH_TEMP_IMAGE = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_TEMP_IMAGE + "/";

    /**
     * 获取图片文件本地临时路径
     * @return
     */
    public String getImageTempPath(){
        File file = new File(DIRPATH_TEMP_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_TEMP_IMAGE;
    }

    public static final String DIRPATH_APPLICATION = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_APPLICATION + "/";

    /**
     * 获取application文件路径
     * @return
     */
    public String getApplicationPath(){
        File file = new File(DIRPATH_APPLICATION);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_APPLICATION;
    }


    public static final String DIRPATH_CAMERA =  Environment
            .getExternalStorageDirectory().getPath()+"/DCIM/Camera/";

    /**
     * 获取拍照相册路径
     * @return
     */
    public String getCameraPath(){
        File file = new File(DIRPATH_CAMERA);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_CAMERA;
    }

    /**
     * 根据文件路径获取文件名称
     * @param filePath
     * @return
     */
    public String getFileName(String filePath){
        if (!TextUtils.isEmpty(filePath)) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return null;
    }

    /**
     * @param base64
     * @param audioPath
     */
    public void saveBase64IntoPath(String base64, String audioPath) {
        byte[] buffer = Base64Decoder.decodeToBytes(base64);
        File file = new File(audioPath);
        FileOutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(file);
            outputFile.write(buffer);
            outputFile.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != outputFile)
                    outputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将指定路径音频文换成base64
     * @param filePath
     * @return
     */
    public String filePath2Base64(String filePath){

        File file;
        BufferedInputStream bis = null;//文件输入流
        file = new File(filePath);
        if(!file.exists()){
            return null;
        }
        //用于接收音频文件的转换
        String base64Media = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[(int)file.length()];
            int i = 0;
            while((i = bis.read(buffer)) != -1){
                base64Media = Base64Encoder.encode(buffer);
            }
            return base64Media;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
