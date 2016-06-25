package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/6/25.
 */
public class CallReadyVo implements Serializable {
    /*
    "createtime": "2016-07-01 06:00:00",  //就绪时间戳
     "taskid": "S9E44U",   //呼叫任务id
     "srvname": "S9E44U",  //服务名称
     "waiterid": "b_eaieuaow", //服务员userid
     "waitername": "王婆",   //服务员用户名
     "waiterimage": "/uploads/head.png", //服务员头像
     "operationseq": 2,    //操作序列号
     "statuscode": 3,  //任务状态码
     "status": "已就绪"
     */

    private String createtime;
    private String taskid;
    private String srvname;
    private String waiterid;
    private String waitername;
    private String waiterimage;
    private int operationseq;
    private int statuscode;
    private String status;


    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getSrvname() {
        return srvname;
    }

    public void setSrvname(String srvname) {
        this.srvname = srvname;
    }

    public String getWaiterid() {
        return waiterid;
    }

    public void setWaiterid(String waiterid) {
        this.waiterid = waiterid;
    }

    public String getWaitername() {
        return waitername;
    }

    public void setWaitername(String waitername) {
        this.waitername = waitername;
    }

    public String getWaiterimage() {
        return waiterimage;
    }

    public void setWaiterimage(String waiterimage) {
        this.waiterimage = waiterimage;
    }

    public int getOperationseq() {
        return operationseq;
    }

    public void setOperationseq(int operationseq) {
        this.operationseq = operationseq;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
