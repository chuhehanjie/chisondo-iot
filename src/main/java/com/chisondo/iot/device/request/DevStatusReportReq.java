package com.chisondo.iot.device.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备发送指令 -->> 设备TCP服务
 * 设备状态上报请求
 */
public class DevStatusReportReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 固定值: statuspush
     * 表示上报设备状态
     */
    private String action;
    /**
     * 1 - 心跳  2-电源按键 3-浓度按键 4-启动按键 5 - 保温按键 6-确认按键 7-沏茶自动结束（倒计时到0秒）8-缺水状态 9-缺茶状态
     */
    private int actionFlag;

    /**
     * 设备ID
     */
    private String deviceID;

    private DevStatusMsg msg;

    /**
     * tcp 验证通过时间
     */
    private Date tcpValTime;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getActionFlag() {
        return actionFlag;
    }

    public void setActionFlag(int actionFlag) {
        this.actionFlag = actionFlag;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public DevStatusMsg getMsg() {
        return msg;
    }

    public void setMsg(DevStatusMsg msg) {
        this.msg = msg;
    }

    public Date getTcpValTime() {
        return tcpValTime;
    }

    public void setTcpValTime(Date tcpValTime) {
        this.tcpValTime = tcpValTime;
    }
}
