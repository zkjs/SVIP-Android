package com.zkjinshi.svip.activity.mine;

import android.content.Context;


import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.http.post.HttpAsyncTask;
import com.zkjinshi.svip.http.post.HttpRequest;
import com.zkjinshi.svip.http.post.HttpRequestListener;


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

    public void requestSetInfoTask(HttpRequest request,HttpRequestListener requestListener){
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask(context,request);
        httpAsyncTask.setHttpRequestListener(requestListener);
        httpAsyncTask.isShowLoadingDialog = true;
        httpAsyncTask.execute();
    }

}
