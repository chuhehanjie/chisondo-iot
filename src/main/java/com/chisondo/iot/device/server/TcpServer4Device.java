package com.chisondo.iot.device.server;

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
 * 设备 TCP 服务
 * @author ding.zhong
 * @since Mar 11.2019
 */
@Slf4j
@Component
public class TcpServer4Device {

    private volatile boolean closed;

    /**
     * 用于与设备 TCP 通信的 bootstrap
     */
    @Autowired
    @Qualifier("deviceBootstrap")
    private ServerBootstrap deviceBootstrap;

    private Channel serverChannel;

//    @Value("deviceTcpPort")
    private int deviceTcpPort = 1658;

    /**
     * closeFuture().sync() 阻塞
     *
     * @throws Exception
     */
    @PostConstruct
    public void start() throws Exception {
//        new Thread(() -> {
//
//        }).zstart();
        this.closed = false;
        // this.serverChannel =  this.deviceBootstrap.bind(new InetSocketAddress(this.deviceTcpPort)).sync().channel().closeFuture().sync().channel();
        this.doBind();
    }

    @PreDestroy
    public void stop() throws Exception {
        this.closed = true;
        this.serverChannel.close();
        this.serverChannel.parent().close();
    }

    private void doBind() {
        log.info("start doBind");
        if (this.closed) {
            return;
        }
        this.deviceBootstrap.bind(new InetSocketAddress(this.deviceTcpPort)).addListener((f) -> {
            ChannelFuture future = (ChannelFuture) f;
            if (future.isSuccess()) {
                log.info("bind tcp port {} successfully.", deviceTcpPort);
            } else {
                log.error("bind tcp port {} failed.", deviceTcpPort);
                future.channel().eventLoop().schedule(() -> doBind(), 3, TimeUnit.SECONDS);
            }
        });
        /*new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("bind port {} successfully.", deviceTcpPort);
                } else {
                    log.error("bind port {} failed.", deviceTcpPort);
                    future.channel().eventLoop().schedule(() -> doBind(), 3, TimeUnit.SECONDS);
                }
            }
        }*/
    }
}
