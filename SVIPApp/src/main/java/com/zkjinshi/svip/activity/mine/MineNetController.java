package com.zkjinshi.svip.activity.mine;

import android.content.Context;

import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;

/**
 * 个人设置网络请求控制器
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MineNetController {

    private static MineNetController instance;
    private Context context;

    private MineNetController(){}
    public synchronized static MineNetController getInstance(){
        if(null == instance){
            instance = new MineNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
    }

    public void requestSetInfoTask(NetRequest request, ExtNetRequestListener requestListener){
        NetRequestTask httpAsyncTask = new NetRequestTask(context, request, NetResponse.class);
        httpAsyncTask.setNetRequestListener(requestListener);
        httpAsyncTask.isShowLoadingDialog = true;
        httpAsyncTask.execute();
    }

}
