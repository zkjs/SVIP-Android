package com.zkjinshi.svip.activity.im.actions;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.sqlite.MessageDBUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 消息发送失败定时检查器，并进行数据库操作
 * @author JimmyZhang
 * @date 2015-5-22下午5:54:08
 */
public class MessageSendFailChecker {

    private static MessageSendFailChecker instance;
    private MessageSendFailChecker(){};
    private long UPDATE_TIME = 5 * 60 * 1000;// 检测是否已经更新
    private Timer updateMessageTimer;

    public synchronized static MessageSendFailChecker getInstance(){
        if(null == instance){
            instance = new MessageSendFailChecker();
        }
        return instance;
    }

    /**
     * 开启消息发送失败检查服务
     */
    public void startCheckMessages() {
        if (null != updateMessageTimer) {
            updateMessageTimer.cancel();
            updateMessageTimer = null;
        }
        updateMessageTimer = new Timer();
        updateMessageTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.getInstance().info(LogLevel.INFO, "每五分钟查询一次数据库，更新正在发送未失败消息");
                // 每五分钟查询一次数据库，更新正在发送未失败消息
                // 关闭activity时，把正在发送消息状态，更新为发送失败
                MessageDBUtil.getInstance().updateMessageTimeOut();
            }
        }, UPDATE_TIME, UPDATE_TIME);
    }

    /**
     * 停止消息发送失败检查服务
     */
    public void stopCheckMessages() {
        if (null != updateMessageTimer) {
            updateMessageTimer.cancel();
            updateMessageTimer = null;
        }
    }

    /**
     * 每次启动应用时，把正在发送消息状态，更新为发送失败
     */
    public void resetSendFailMessages(){
        MessageDBUtil.getInstance().updateMessageTimeOut();
    }
}
