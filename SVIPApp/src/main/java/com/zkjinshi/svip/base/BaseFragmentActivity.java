package com.zkjinshi.svip.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;



/**
 * FragmentActivity基类
 * 开发者：JimmyZhang
 * 日期：2015/12/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getInst().addActivity(this);
    }


    @Override
    protected void onDestroy() {
        BaseApplication.getInst().remove(this);
        super.onDestroy();
    }

    protected void onResume(){
        super.onResume();

    }

    protected void onPause(){
        super.onPause();

    }

    /**
     * 退出程序方法
     */
    protected void onExit(){
        BaseApplication.getInst().exit();
    }


    /**
     * 退出并且重启动
     * 为了不与Activity的onRestart()冲突
     */
    protected void onExitAndStart(){
        BaseApplication.getInst().restart();
    }
}
