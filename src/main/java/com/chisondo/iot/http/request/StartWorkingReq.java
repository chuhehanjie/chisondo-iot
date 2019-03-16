package com.chisondo.iot.http.request;

import java.io.Serializable;

/**
 * http server -->> 设备 server -->> 沏茶器
 * 启动或预约泡茶请求
 * 由 http 发送过来
 */
public class StartWorkingReq implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 手机号码
     * 设备绑定的手机号码
     */
    private String phoneNum;
    /**
     * 启动时间(非必填)
     * 大于当前时间24小时内,YYYY-MM-DD HH:MM24:SS，当没有该参数时，表示立即启动
     */
    private String startTime;
    /**
     * 设定温度
     * 60~100,0-停止加热
     */
    private Integer temperature;
    /**
     * 设定时间
     * （根据出汤量不同时间的最小值不同）	0~600秒,0 - 不浸泡，1~600  浸泡时间(单位:秒)
     */
    private Integer soak;
    /**
     * 出水量
     * （分8个档次）150 200 250 300   350 400  450 550 (单位：毫升ml)
     */
    private Integer waterlevel;
    /**
     * 茶类ID
     */
    private Integer teaSortId;
    /**
     * 茶类名称
     */
    private String teaSortName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getSoak() {
        return soak;
    }

    public void setSoak(Integer soak) {
        this.soak = soak;
    }

    public Integer getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(Integer waterlevel) {
        this.waterlevel = waterlevel;
    }

    public Integer getTeaSortId() {
        return teaSortId;
    }

    public void setTeaSortId(Integer teaSortId) {
        this.teaSortId = teaSortId;
    }

    public String getTeaSortName() {
        return teaSortName;
    }

    public void setTeaSortName(String teaSortName) {
        this.teaSortName = teaSortName;
    }
}
