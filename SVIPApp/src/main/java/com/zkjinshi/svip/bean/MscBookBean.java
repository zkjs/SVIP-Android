package com.zkjinshi.svip.bean;

/**
 * 语音订房返回对象
 * 开发者：WinkyQin
 * 日期：2016/1/29 0029
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MscBookBean {

    private int    rc;
    private String text;
    private String operation;
    private String service;
    private SemanticBean semantic;

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public SemanticBean getSemantic() {
        return semantic;
    }

    public void setSemantic(SemanticBean semantic) {
        this.semantic = semantic;
    }
}
