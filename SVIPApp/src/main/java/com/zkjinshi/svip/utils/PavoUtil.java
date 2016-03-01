package com.zkjinshi.svip.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dujiande on 2016/3/1.
 */
public class PavoUtil {
    public static void showErrorMsg(Context context, int res){
        String msg = getErrorMsg(res);
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static String getErrorMsg(int res){
        String msg = "";
        switch (res){
            case 1:msg="非合法的传入参数";
                break;
            case 2:msg="密码不正确";
                break;
            case 3:msg="不存在用户名";
                break;
            case 4:msg="验证码不正确或过时";
                break;
            case 5:msg="不存在的手机号";
                break;
            case 6:msg="token过旧";
                break;
            case 7:msg="非法签名";
                break;
            case 8:msg="无效Token";
                break;
            case 9:msg="请求验证码过频";
                break;
            case 10:msg="号码解密失败";
                break;
            case 11:msg="其他错误";
                break;
        }
        return  msg;
    }
}
