package com.chisondo.iot.device.request;

public class DevStatusMsg {
    /**
     * 工作状态
     * 0 -空闲 1-沏茶中 2-洗茶中 3-烧水中
     */
    private int workstatus;
    /**
     * 温度状态
     * 0-未保温 1-保温中
     */
    private int warmstatus;
    /**
     * 浓度状态
     * 1-浓 2-中 3淡
     */
    private int taststatus;
    /**
     * 温度值
     * 需大于60度小于100度
     */
    private int temperature;
    /**
     * 沏茶时间
     * 0 - 不浸泡，1~600  沏茶时间(单位:秒)
     */
    private int soak;
    /**
     * 出水量
     * 取值为：150 200 250 300 350 400 450 550  8个档次 (单位：毫升ml)
     */
    private int waterlevel;
    /**
     * 	面板剩余时间
     */
    private String remaintime;
    /**
     * 异常状态
     * 0-正常 1-缺水 2-缺茶 3-缺水缺茶 4-未连接Wi-Fi
     */
    private int errorstatus;
    /**
     * 实时温度	60-100
     * 当前发热管的温度
     */
    private int nowwarm;

    public int getWorkstatus() {
        return workstatus;
    }

    public void setWorkstatus(int workstatus) {
        this.workstatus = workstatus;
    }

    public int getWarmstatus() {
        return warmstatus;
    }

    public void setWarmstatus(int warmstatus) {
        this.warmstatus = warmstatus;
    }

    public int getTaststatus() {
        return taststatus;
    }

    public void setTaststatus(int taststatus) {
        this.taststatus = taststatus;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getSoak() {
        return soak;
    }

    public void setSoak(int soak) {
        this.soak = soak;
    }

    public int getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(int waterlevel) {
        this.waterlevel = waterlevel;
    }

    public String getRemaintime() {
        return remaintime;
    }

    public void setRemaintime(String remaintime) {
        this.remaintime = remaintime;
    }

    public int getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(int errorstatus) {
        this.errorstatus = errorstatus;
    }

    public int getNowwarm() {
        return nowwarm;
    }

    public void setNowwarm(int nowwarm) {
        this.nowwarm = nowwarm;
    }
}
