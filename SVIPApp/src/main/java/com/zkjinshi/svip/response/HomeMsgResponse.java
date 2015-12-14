package com.zkjinshi.svip.response;

/**
 * Created by dujiande on 2015/12/13.
 */
public class HomeMsgResponse extends BaseResponse {

    public enum HomeMsgType {
        HOME_MSG_DEFAULT,
        HOME_MSG_LOCATION,
        HOME_MSG_ORDER,
        HOME_MSG_OTHER
    }

    private String majorText;
    private String minorText;
    private boolean clickAble;
    private String icon="";
    private HomeMsgType msgType;

    public HomeMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(HomeMsgType msgType) {
        this.msgType = msgType;
    }

    public String getMajorText() {
        return majorText;
    }

    public void setMajorText(String majorText) {
        this.majorText = majorText;
    }

    public String getMinorText() {
        return minorText;
    }

    public void setMinorText(String minorText) {
        this.minorText = minorText;
    }

    public boolean isClickAble() {
        return clickAble;
    }

    public void setClickAble(boolean clickAble) {
        this.clickAble = clickAble;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
