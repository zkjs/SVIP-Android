package com.zkjinshi.svip.utils;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.zkjinshi.base.config.ConfigUtil;

import java.util.Map;
import java.util.Random;

/**
 * 短信验证码发送
 */
public class SmsUtil {

    private final static String SERVER_IP   = "app.cloopen.com";
    private final static String SERVER_PORT = "8883";

    private final static String ACCOUNT_SID = ConfigUtil.getInst().getSmsAccountSID();
    private final static String APP_ID      = ConfigUtil.getInst().getSmsAppID();
    private final static String AUTH_TOKEN  = ConfigUtil.getInst().getSmsAuthToken();

    private final static String TEMPLATE_ID = "22143";
    private final static int    VERIFY_TIME = 2;

    private static SmsUtil ccpRestSms;

    private CCPRestSmsSDK restAPI;

    private SmsUtil(String serverIP, String serverPort, String accountSid, String authToken, String appID) {
        this.restAPI = new CCPRestSmsSDK();
        this.restAPI.init(serverIP, serverPort);
        this.restAPI.setAccount(accountSid, authToken);
        this.restAPI.setAppId(appID);
    }

    public static SmsUtil getInstance() {
        if (ccpRestSms == null) {
            ccpRestSms = new SmsUtil(SERVER_IP, SERVER_PORT, ACCOUNT_SID, AUTH_TOKEN, APP_ID);
        }
        return ccpRestSms;
    }

    public Map<String, Object> sendTemplateSMS(String to, String verifyCode){
       return restAPI.sendTemplateSMS(to, TEMPLATE_ID, new String[]{verifyCode, VERIFY_TIME+""});
    }

    /**
     * 生成6位数手机验证码
     * @return
     */
    public String generateVerifyCode(){
        Random random   = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<6; i++){
            sb.append(String.valueOf(random.nextInt(9)));
        }
        return sb.toString();
    }

}
