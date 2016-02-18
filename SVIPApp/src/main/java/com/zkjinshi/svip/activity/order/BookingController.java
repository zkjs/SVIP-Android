package com.zkjinshi.svip.activity.order;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemShowView;
import com.zkjinshi.svip.vo.TicketVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BookingController {
    private final static String TAG = BookingController.class.getSimpleName();

    private BookingController(){}
    private static BookingController instance;
    private Context context;

    public static synchronized BookingController getInstance(){
        if(null ==  instance){
            instance = new BookingController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;

    }

    //加载发票列表
    public  void setDefaultInvoice(final ItemShowView invoiceTsv) {
        String url = ProtocolUtil.geTicketListUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("set","0");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    ArrayList<TicketVo> datalist = gson.fromJson( result.rawResult, new TypeToken<ArrayList<TicketVo>>(){}.getType());                   ;
                    for(TicketVo ticketVo : datalist){
                        if(ticketVo.getIs_default().equals("1")){
                            String defaultInvoice = ticketVo.getInvoice_title();
                            invoiceTsv.setValue(defaultInvoice);
                            break;
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();

    }

}
