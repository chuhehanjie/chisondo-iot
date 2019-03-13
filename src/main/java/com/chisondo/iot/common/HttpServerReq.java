package com.chisondo.iot.common;

import java.io.Serializable;

public class HttpServerReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
