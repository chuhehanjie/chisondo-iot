package com.chisondo.iot.http.request;

import java.io.Serializable;

public class DeviceHttpReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private String action;
    private String deviceId;
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
