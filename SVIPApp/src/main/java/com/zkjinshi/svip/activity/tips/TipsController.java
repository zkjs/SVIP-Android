package com.zkjinshi.svip.activity.tips;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.response.WaitListResponse;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/4/23.
 */
public class TipsController {

    private final static String TAG = TipsController.class.getSimpleName();

    private TipsController(){}
    private static TipsController instance;

    public static synchronized TipsController getInstance(){
        if(null ==  instance){
            instance = new TipsController();
        }
        return instance;
    }

    public interface CallBackListener{
        public void successCallback(WaitListResponse waitListResponse);
    }

    /**
     * 获取服务员列表
     */
    public void requestWaitListTask(final Context context ,final CallBackListener callBackListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.getWaiterListUrl();
            client.get(context,url,stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        WaitListResponse waitListResponse = new Gson().fromJson(response,WaitListResponse.class);
                        if(null != waitListResponse){
                            int resultCode = waitListResponse.getRes();
                            if(0 == resultCode){
                                callBackListener.successCallback(waitListResponse);
                            }else {
                                String errorMsg = waitListResponse.getResDesc();
                                if(!TextUtils.isEmpty(errorMsg)){
                                    DialogUtil.getInstance().showCustomToast(context,errorMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(context,statusCode,url);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
