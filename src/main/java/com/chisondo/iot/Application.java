package com.chisondo.iot;

import com.supermy.im.netty.ChannelRepository;
import com.supermy.im.netty.handler.ImChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Spring Java Configuration and Bootstrap
 * <p/>
 * 通过注解将配置与代码整合到一起,ServerBootstrap初始化
 */
@SpringBootApplication
@PropertySource(value = "classpath:/properties/local/nettyserver.properties")
public class Application {

    @Configuration
    @Profile("production")
    @PropertySource("classpath:/properties/production/nettyserver.properties")
    static class Production {
    }

    @Configuration
    @Profile("local")
    @PropertySource({"classpath:/properties/local/nettyserver.properties"})
    static class Local {
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Value("${redis.addres}")
    private String redisAddres;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${boss.thread.count}")
    private int bossCount;

    @Value("${worker.thread.count}")
    private int workerCount;

    @Value("${so.keepalive}")
    private boolean keepAlive;

    @Value("${so.backlog}")
    private int backlog;

    @Value("${msg.length}")
    private int msglength;

    @Value("${bi.pool}")
    private int bipool;

    @Autowired
    org.springframework.core.env.Environment env;

    @SuppressWarnings("unchecked")
    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup()) //设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.ERROR))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // TODO
                    }
                });

        /**
         * 参数设置
         */
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }

        return b;
    }

    /*@Autowired
    @Qualifier("imChannelInitializer")
    private ImChannelInitializer imChannelInitializer;*/


    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backlog);
        return options;
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }

    /*@Bean(name = "channelRepository")
    public ChannelRepository channelRepository() {
        return new ChannelRepository();
    }*/

    @Bean(name = "msglength")
    public Integer msglength() {
        return new Integer(msglength);
    }


    @Bean(name = "bipool")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(bipool);
    }

    @Bean(name = "jedis")
    @Deprecated
    public Jedis jedis() {
        return new Jedis(this.redisAddres, this.redisPort);
    }


    @Bean(name = "jedispool")
    @Deprecated
    public JedisPool jedispool() {
        JedisPool  pool = new JedisPool(new JedisPoolConfig(), this.redisAddres, this.redisPort);
        return pool;
    }

//    @Autowired
//    @Qualifier("mymongo")
//    private MongoRepository mongo;







}