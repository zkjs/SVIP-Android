package com.zkjinshi.base.net.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * IM缓存工具类
 * 开发者：JimmyZhang
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */

public class ImCacheUtil {

    private static final String SVIP_CACHE = "zkjs_cache";
    private static ImCacheUtil instance;
    private Context context;

    private ImCacheUtil() {
    }

    public synchronized static ImCacheUtil getInstance() {
        if (null == instance) {
            instance = new ImCacheUtil();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 获取用户登录状态
     *
     * @return
     */
    public boolean isIMLogin() {
        if (null == context) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(
                SVIP_CACHE, Context.MODE_PRIVATE);
        return sp.getBoolean("is_im_login", false);
    }

    /**
     * 设置用户登录状态
     *
     * @param isLogin
     */
    public void setIMLogin(boolean isLogin) {
        if (null == context) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(
                SVIP_CACHE, Context.MODE_PRIVATE);
        sp.edit().putBoolean("is_im_login", isLogin).commit();
    }
}
