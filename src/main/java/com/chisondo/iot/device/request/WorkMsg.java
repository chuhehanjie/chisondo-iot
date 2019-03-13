package com.chisondo.iot.device.request;

import java.io.Serializable;

public class WorkMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer temperature;

    private Integer soak;

    private Integer waterlevel;

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
}
