package com.zkjinshi.svip;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.testin.agent.TestinAgent;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogConfig;
import com.zkjinshi.base.log.LogSwitch;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.util.ImCacheUtil;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.svip.activity.im.actions.MessageSendFailChecker;

import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.EmotionUtil;
import com.zkjinshi.svip.utils.VIPContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by JimmyZhang on 2015/7/18.
 */
public class SVIPApplication extends Application {

    public static final String TAG = "SVIPApp";

    public Vector<RegionVo> mRegionList = new Vector<RegionVo>();

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        initEmchat();
        saveConfig();
        initLog();
        initCache();
        initImCache();
        initImageLoader();
        initDevice();
        initFace();
        initTest();
        initChecker();

    }



    /**
     * 初始化上下文
     */
    private void initContext(){
        VIPContext.getInstance().init(this);
        BaseContext.getInstance().init(this);
    }

    /**
     * 设置环信ios推送昵称
     */
    private void initEmchat(){
        EMChat.getInstance().init(this);
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(1);
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
            Log.e(TAG, "找不到assets/目录下的config.xml配置文件", e);
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
    }

    /**
     * 初始化IM缓存
     */
    private void initImCache(){
        ImCacheUtil.getInstance().init(this);
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 初始化设备信息
     */
    private void initDevice(){
        DeviceUtils.init(this);
    }

    /**
     * 初始化云测试
     */
    private void initTest(){
        TestinAgent.init(this);
    }

    /**
     * 初始化表情库
     */
    private void initFace(){
        EmotionUtil.getInstance().initEmotion();
    }

    /**
     * 初始化发送状态更新Checker
     */
    private void initChecker() {
        MessageSendFailChecker.getInstance().startCheckMessages();
    }

}
