package com.zkjinshi.svip.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.listener.ReadListener;
import com.zkjinshi.base.net.listener.SendListener;
import com.zkjinshi.base.net.listener.SocketListener;
import com.zkjinshi.base.net.status.PacketUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgClientLogin;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.LoginType;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.util.Random;

/**
 * Socket登录服务
 */
public class SocketService extends Service implements SocketListener {

    private final static String TAG = SocketService.class.getSimpleName();

    private String          mURI;
    private String          mUserID;
    private String          mName;
    private MsgClientLogin  mMsgClientLogin;

    public SocketService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        mUserID = CacheUtil.getInstance().getUserId();
        mName   = CacheUtil.getInstance().getUserName();
        mURI    = "ws://"+ ConfigUtil.getInst().getIMHost() + ":" + ConfigUtil.getInst().getIMPort() + "/zkjs";
        Log.v(TAG, "uri:" + mURI);
        resgisterHostControl();//注册服务器更改IP地址
        WebSocketClient.getInstance().connect(getApplicationContext(), URI.create(mURI), this);
    }

    /**
     * 注册转移服务器IP
     * */
    private void resgisterHostControl(){
        WebSocketClient.getInstance().registReader(PacketUtil.MSG_TransferServer, new ReadListener() {
            @Override
            public void onRead(boolean isSuccess, JSONObject msg) {
                if (!isSuccess)
                    return;
                try {
                    String ip = msg.getString("ip");
                    int port = msg.getInt("port");
                    mURI = "ws://" + ip + ":" + port + "/zkjs";
                    socketLogin(mMsgClientLogin);//服务器转移后执行登录
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 1.创建商家登录请求
     */
    private void socketLogin(MsgClientLogin msgClientLogin) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(msgClientLogin);
        try {
            JSONObject loginPacket = new JSONObject(jsonString);
            login(loginPacket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 协议登录包进行登录
     * @param loginPacket
     */
    public void login(final JSONObject loginPacket){
        Log.e("登录包", loginPacket.toString());
        WebSocketClient.getInstance().send(loginPacket, new SendListener() {
            @Override
            public void onSend(boolean loginSuccess, JSONObject jsonObject) {
                if (loginSuccess) {
                    Log.e(TAG, "登录成功");
                } else {
                    Log.e(TAG, "登录失败");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Random random = new Random();
                                Thread.sleep(random.nextInt(10*1000));
                                login(loginPacket);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onConnect() {
        Log.e(TAG, "服务器连接成功");
        mMsgClientLogin = new MsgClientLogin();
        mMsgClientLogin.setType(PacketUtil.MSG_ClientLogin);
        mMsgClientLogin.setTimestamp(System.currentTimeMillis());
        mMsgClientLogin.setId(mUserID);
        mMsgClientLogin.setName(mName);
        mMsgClientLogin.setLogintype(LoginType.appUser.getVlaue());
        mMsgClientLogin.setVersion("");
        mMsgClientLogin.setPlatform("");
        mMsgClientLogin.setAppid("");
        socketLogin(mMsgClientLogin);//用户socket连接登陆
    }

    @Override
    public void onDisconnect(int i, String error) {
        Log.e(TAG, "连接断开:" + "error_code:" + i + "error_info" + error);
        newThread2Connect();
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "连接断开-重新连接:" + e.toString());
        newThread2Connect();
    }

    /**
     * 开启线程重新连接
     */
    public void newThread2Connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(10 *1000));
                    WebSocketClient.getInstance().connect(getApplicationContext(), URI.create(mURI), SocketService.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}