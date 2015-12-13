package com.zkjinshi.svip.response;

/**
 * Created by dujiande on 2015/12/13.
 */
public class HomeMsgResponse extends BaseResponse {

    private String majorText;
    private String minorText;
    private boolean clickAble;
    private String icon="";

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
