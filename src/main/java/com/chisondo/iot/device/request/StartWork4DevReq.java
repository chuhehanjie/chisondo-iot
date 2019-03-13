package com.chisondo.iot.device.request;

import java.io.Serializable;

public class StartWork4DevReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;

    private String deviceID;

    private WorkMsg msg;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public WorkMsg getMsg() {
        return msg;
    }

    public void setMsg(WorkMsg msg) {
        this.msg = msg;
    }
}
