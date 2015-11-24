package com.zkjinshi.svip.emchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.zkjinshi.svip.utils.VIPContext;

import java.util.ArrayList;

/**
 * 环信帮助类
 * 开发者：JimmyZhang
 * 日期：2015/11/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EasemobIMHelper {

    public static final String TAG = EasemobIMHelper.class.getSimpleName();

    private Context context;

    private EasemobIMHelper (){}

    private static EasemobIMHelper instance;

    public synchronized static EasemobIMHelper getInstance(){
        if(null == instance){
            instance = new EasemobIMHelper();
        }
        return instance;
    }

    /**
     * 初始化环信IM
     * @param context
     */
    public void init(Context context){
        this.context = context;
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(true);
    }

    /**
     * 注册用户
     * @param username
     * @param pwd
     */
    public void registerUser(final String username,final String pwd){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(username, pwd);
                } catch (final EaseMobException e) {
                    //注册失败
                    int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        Toast.makeText(context, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        Toast.makeText(context, "用户已存在！", Toast.LENGTH_SHORT).show();
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        Toast.makeText(context, "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    /**
     * 登录用户
     * @param currentUsername
     * @param currentPassword
     * @param emCallBack
     */
    public void loginUser(String currentUsername,String currentPassword,EMCallBack emCallBack){
        EMChatManager.getInstance().login(currentUsername, currentPassword, emCallBack);
    }

    /**
     * 网络重连监听
     */
    public void initConnectionListener(){
        EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                Log.i(TAG, "环信重连成功");
            }

            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    Log.i(TAG, "环信重连异常-显示帐号已经被移除");
                } else if (error == EMError.CONNECTION_CONFLICT) {
                    // 显示帐号在其他设备登陆
                    Log.i(TAG, "环信重连异常-帐号在其他设备登陆");
                    Intent intent = new Intent();
                    intent.setAction("com.zkjinshi.svip.CONNECTION_CONFLICT");
                    VIPContext.getInstance().getContext().sendBroadcast(intent);
                } else {
                    if (NetUtils.hasNetwork(context)) {
                        //连接不到聊天服务器
                        Log.i(TAG, "环信重连异常-连接不到聊天服务器");
                    } else {
                        //当前网络不可用，请检查网络设置
                        Log.i(TAG, "环信重连异常-当前网络不可用，请检查网络设置");
                    }
                }
            }
        });
    }

    public void addFriend(final String toAddUsername,final String reason){
        //参数为要添加的好友的username和添加理由
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMContactManager.getInstance().addContact(toAddUsername, reason);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    Log.i(TAG,"errorCode:"+e.getErrorCode());
                    Log.i(TAG,"errorMessage:"+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    /**
     * 接收好友请求
     * @param username
     */
    public void acceptInvitation(final String username){
        //参数为要添加的好友的username和添加理由
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //同意username的好友请求
                    EMChatManager.getInstance().acceptInvitation(username);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    Log.i(TAG, "errorCode:" + e.getErrorCode());
                    Log.i(TAG,"errorMessage:"+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    /**
     * 获取好友列表
     */
    public void getFriendList(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ArrayList<String> usernames = (ArrayList<String>) EMContactManager.getInstance().getContactUserNames();//需异步执行
                    for (String userName : usernames){
                        Log.i(TAG,"userName:"+userName);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    /**
     * 退出环信
     */
    public void logout(){
        //此方法为异步方法
        EMChatManager.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i(TAG,"logout.onSuccess()");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.i(TAG,"logout.code:"+code);
                Log.i(TAG,"logout.message:"+message);
            }
        });
    }


}
