package com.zkjinshi.svip;


import android.content.Context;

import android.os.Environment;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogConfig;
import com.zkjinshi.base.log.LogSwitch;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.base.util.DeviceUtils;

import com.zkjinshi.svip.base.BaseApplication;


import com.zkjinshi.svip.blueTooth.BlueToothManager;

import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.utils.CacheUtil;

import com.zkjinshi.svip.utils.VIPContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import io.yunba.android.manager.YunBaManager;

/**
 * 超级身份入口
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SVIPApplication extends BaseApplication {

    public static final String TAG = SVIPApplication.class.getSimpleName();
    private BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        initYunBa();
        saveConfig();
        initLog();
        initCache();
        initDevice();
        initImageLoader();
        LocationManager.getInstance().init(this);
        BlueToothManager.getInstance().init(this);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        Fresco.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化上下文
     */
    private void initContext(){
        VIPContext.getInstance().init(this);
        BaseContext.getInstance().init(this);
    }

    /**
     * 初始化云巴区域推送
     */
    private void initYunBa(){
        YunBaManager.start(getApplicationContext());
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.memoryCacheExtraOptions(1080, 780); // maxwidth, max height，即保存的每个缓存文件的最大长宽
        config.threadPoolSize(3);//线程池内加载的数量
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCache(new LruMemoryCache(2 * 1024 * 1024));
        config.memoryCacheSize(2 * 1024 * 1024);
        config.memoryCacheSizePercentage(13);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(500 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 初始化配置
     */
    private void saveConfig(){
        try {
            File f = new File(this.getFilesDir(), "config.xml");
            InputStream is;
            if (f.exists()) {
                is = new FileInputStream(f);
                ConfigUtil.getInst(is);
            } else {
                is = this.getResources().getAssets()
                        .open("config.xml");
                ConfigUtil.getInst(is);
                ConfigUtil.getInst().save(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Log配置项
     */
    private void initLog() {
        LogConfig config = new LogConfig();
        config.setContext(this)
                .setLogSwitch(LogSwitch.OPEN)
                .setLogPath(
                        Environment.getExternalStorageDirectory() + "/"
                                + this.getPackageName() + "/log/");
        LogUtil.getInstance().init(config);
    }

    /**
     * 初始化缓存
     */
    private void initCache(){
        CacheUtil.getInstance().init(this);
        CacheUtil.getInstance().setScreenOff(false);
    }


    /**
     * 初始化设备信息
     */
    private void initDevice(){
        DeviceUtils.init(this);
    }


}
