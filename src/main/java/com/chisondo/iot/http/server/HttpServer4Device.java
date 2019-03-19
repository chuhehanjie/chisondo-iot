package com.chisondo.iot.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 设备 http 服务
 * 用于 HTTP 与设备通信
 * @author ding.zhong
 * @since Mar 11.2019
 */
@Slf4j
@Component
public class HttpServer4Device {

    private volatile boolean closed;

    /**
     * 内部服务 TCP 通信的 bootstrap
     */
    @Autowired
    @Qualifier("innerSrvBootstrap")
    private ServerBootstrap innerSrvBootstrap;

    private Channel serverChannel;

//    @Value("netty.deviceHttpPort")
    private String deviceHttpPort = "8188";

    /**
     * closeFuture().sync() 阻塞
     *
     * @throws Exception
     */
    @PostConstruct
    public void start() throws Exception {
        this.closed = false;
//        this.serverChannel =  this.innerSrvBootstrap.bind(new InetSocketAddress(Integer.valueOf(this.deviceHttpPort))).sync().channel().closeFuture().sync().channel();
        this.doBind();
    }

    @PreDestroy
    public void stop() throws Exception {
        this.closed = true;
        this.serverChannel.close();
        this.serverChannel.parent().close();
    }

    private void doBind() {
        if (this.closed) {
            return;
        }
        this.innerSrvBootstrap.bind(new InetSocketAddress(Integer.valueOf(this.deviceHttpPort))).addListener((f) ->{
            ChannelFuture future = (ChannelFuture) f;
            if (future.isSuccess()) {
                log.info("bind http port {} successfully.", deviceHttpPort);
            } else {
                log.error("bind http port {} failed.", deviceHttpPort);
                future.channel().eventLoop().schedule(() -> doBind(), 3, TimeUnit.SECONDS);
            }
        });
    }
}
