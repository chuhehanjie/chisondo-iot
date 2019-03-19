package com.chisondo.iot.device.response;

import java.io.Serializable;

public class DeviceServerResp implements Serializable {
    private static final long serialVersionUID = 1L;

    private int retn;
    private String desc;
    private String action;
    private String deviceID;
    private DeviceServerMsg msg;

    public DeviceServerResp() {
    }

    public DeviceServerResp(int retn, String desc, String action, String deviceID) {
        this.retn = retn;
        this.desc = desc;
        this.action = action;
        this.deviceID = deviceID;
    }

    public int getRetn() {
        return retn;
    }

    public void setRetn(int retn) {
        this.retn = retn;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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

    public DeviceServerMsg getMsg() {
        return msg;
    }

    public void setMsg(DeviceServerMsg msg) {
        this.msg = msg;
    }
}
