package com.zkjinshi.svip.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import java.util.LinkedList;
import java.util.List;

/**
 * 应用层基类
 * 开发者：JimmyZhang
 * 日期：2015/12/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    protected List<Activity> activityStack = new LinkedList<Activity>();

    protected static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static BaseApplication getInst(){
        return application;
    }

    public int activityStackCount(){
        return activityStack.size();
    }

    public void addActivity(Activity activity){
        activityStack.add(activity);
    }

    public void remove(Activity activity){
        if(activityStack.contains(activity))
            activityStack.remove(activity);
    }

    public void restart(){
        for(Activity act:activityStack){
            act.finish();
        }
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void clear() {
        for(int i =activityStack.size()-1; i > -1; i -- ){
            Activity act = activityStack.get(i);
            act.finish();
        }
    }

    public void exit() {
        for(int i =activityStack.size()-1; i > -1; i -- ){
            Activity act = activityStack.get(i);
            act.finish();
        }
        System.exit(0);
    }
}
