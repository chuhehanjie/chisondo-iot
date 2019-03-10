package com.chisondo.iot.device.server;

import io.netty.bootstrap.ServerBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 设备 TCP 服务
 * @author ding.zhong
 * @since Mar 11.2019
 */
public class TcpServer4Device {

    @Autowired
    @Qualifier("deviceBootstrap")
    private ServerBootstrap deviceBootstrap;

    @Autowired
    @Qualifier("innerSrvBootstrap")
    private ServerBootstrap innerSrvBootstrap;
}
