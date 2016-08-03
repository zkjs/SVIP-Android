package com.zkjinshi.svip.blueTooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.svip.manager.BleLogManager;
import com.zkjinshi.svip.manager.SSOManager;
import com.zkjinshi.svip.sqlite.BleStatDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.BeaconPushVo;
import com.zkjinshi.svip.vo.BeaconRssiVo;
import com.zkjinshi.svip.vo.PayloadVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by qinyejun on 8/3/16.
 */
public class AutoPayBeaconsManager {
    public static final String TAG = AutoPayBeaconsManager.class.getSimpleName();
    public static final int PUT_TO_HOST = 10;
    public static final int DELAY_TIME = 1000;
    public static final int RSSI_THRESHOLD = -50; // 扣款beacon信号强度阈值

    private boolean pushable = false;
    private HashMap<String,BeaconPushVo> beasconPushMap;
    private static AutoPayBeaconsManager ourInstance = new AutoPayBeaconsManager();
    public final static AsyncHttpClient client = new AsyncHttpClient();
    private Context mContext;

    public static AutoPayBeaconsManager getInstance() {
        return ourInstance;
    }

    private AutoPayBeaconsManager() {
        beasconPushMap = new HashMap<String,BeaconPushVo>();
    }

    public void init(Context context ){
        this.mContext = context;
    }

    public  final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PUT_TO_HOST:
                {
                    pushable = false;
                    lbsLocBeaconPush();
                }
                break;
            }
        }
    };


    public boolean isPushable() {
        return pushable;
    }

    public void setPushable(boolean pushable) {
        this.pushable = pushable;
    }

    public void add(IBeaconVo iBeaconVo){
        if(!pushable){
            return;
        }
        String key = iBeaconVo.getUuid()+iBeaconVo.getMajor()+iBeaconVo.getMinior();
        BeaconRssiVo beaconRssiVo = new BeaconRssiVo();
        beaconRssiVo.setRssi(iBeaconVo.getRssi());
        beaconRssiVo.setTimestamp(System.currentTimeMillis());

        Log.i(TAG,key + " : " + iBeaconVo.getRssi());

        if(beasconPushMap.containsKey(key)){
            BeaconPushVo beaconPushVo = beasconPushMap.get(key);
            beaconPushVo.setLastRssi(beaconRssiVo);

            // don't send beacon to server in 60 senconds
            if(System.currentTimeMillis() - beaconPushVo.getTimestamp() > 60*1000) {
                if(isRssiStrong(iBeaconVo)) {
                    //update upload timestamp
                    beaconPushVo.setTimestamp(System.currentTimeMillis());
                    handler.sendEmptyMessage(PUT_TO_HOST);
                }
            }
        }else{
            BeaconPushVo beaconPushVo = new BeaconPushVo();
            beaconPushVo.setUuid(iBeaconVo.getUuid());
            beaconPushVo.setMajor(iBeaconVo.getMajor()+"");
            beaconPushVo.setMinor(iBeaconVo.getMinor()+"");
            //ArrayList<BeaconRssiVo> rssis = new ArrayList<BeaconRssiVo>();
            //rssis.add(beaconRssiVo);
            //beaconPushVo.setRssis(rssis);
            beaconPushVo.setLastRssi(beaconRssiVo);

            beasconPushMap.put(key,beaconPushVo);

            if(isRssiStrong(iBeaconVo)) {
                handler.sendEmptyMessage(PUT_TO_HOST);
            }
        }
    }

    private boolean isRssiStrong(IBeaconVo iBeaconVo) {
        return iBeaconVo.getRssi() >= RSSI_THRESHOLD;
    }

    private String dataToJsonStr( ArrayList<BeaconPushVo> dataList){
        String jsonstr;

        if(dataList.size() > 0){
            jsonstr = new Gson().toJson(dataList);
        }else{
            jsonstr = "";
        }
        return jsonstr;
    }

    private ArrayList<BeaconPushVo> dataToArrayList(){
        ArrayList<BeaconPushVo> dataList = new ArrayList<BeaconPushVo>();
        Iterator<Map.Entry<String, BeaconPushVo>> iterator = beasconPushMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, BeaconPushVo> entry = iterator.next();
            BeaconPushVo beaconPushVo = entry.getValue();
            beaconPushVo.getRssis().add(beaconPushVo.getLastRssi());
            dataList.add(beaconPushVo);
        }
        return dataList;
    }

    private void lbsLocBeaconPush() {
        try {
            if(!NetWorkUtil.isNetworkConnected(mContext)){
                return;
            }
            if(!CacheUtil.getInstance().isLogin()){
                return;
            }
            if(TextUtils.isEmpty(CacheUtil.getInstance().getExtToken())){
                return;
            }
            PayloadVo payloadVo = SSOManager.getInstance().decodeToken(CacheUtil.getInstance().getExtToken());
            if(TextUtils.isEmpty(payloadVo.getSub())){
                return;
            }
            if(beasconPushMap.isEmpty()){
                return;
            }
            final ArrayList<BeaconPushVo> datalist = dataToArrayList();
            String jsonStr = dataToJsonStr(datalist);
            //beasconPushMap.clear();
            if(StringUtil.isEmpty(jsonStr)){
                return;
            }

            String url = ProtocolUtil.autoPayBeacon();
            client.setMaxRetriesAndTimeout(3,1000*3);
            client.setConnectTimeout(1000*10);
            client.setResponseTimeout(1000*10);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            StringEntity stringEntity = new StringEntity(jsonStr,"UTF-8");
            client.post(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                    LogUtil.getInstance().info(LogLevel.INFO,getMajors(datalist)+"支付蓝牙推送成功[autopay]");
                    BleStatDBUtil.getInstance().updateTotalCount();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e){
                    LogUtil.getInstance().info(LogLevel.INFO,getMajors(datalist)+"支付蓝牙推送失败[autopay]");
                    BleLogManager.getInstance().collectBleLog(mContext,e.getMessage());

                }

                @Override
                public void onRetry(int retryNo) {
                    Log.d(TAG,"retryNo:"+retryNo);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMajors(ArrayList<BeaconPushVo> datalist){
        String majors = "";
        for(BeaconPushVo beaconPushVo : datalist){
            majors += beaconPushVo.getMajor()+",";
        }
        return majors;
    }


}
