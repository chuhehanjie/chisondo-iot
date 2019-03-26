package com.chisondo.iot.device.handler;

import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.utils.IOTUtils;
import com.chisondo.iot.common.utils.RestTemplateUtils;
import com.chisondo.iot.device.request.DevStatusReportReq;
import com.chisondo.iot.device.request.StartWork4DevReq;
import com.chisondo.iot.device.request.WorkMsg;
import com.chisondo.iot.device.response.DeviceServerResp;
import com.chisondo.iot.device.server.DevTcpChannelManager;
import com.chisondo.iot.http.request.DeviceHttpReq;
import com.chisondo.iot.http.request.StartWorkingReq;
import com.chisondo.iot.http.server.DevHttpChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 设备服务 hanlder
 * @author ding.zhong
 */
@Slf4j
@Component
@Qualifier("deviceServerHandler")
@ChannelHandler.Sharable
public class DeviceServerHandler extends SimpleChannelInboundHandler<Object> { // (1)

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    @Autowired
    private StringRedisTemplate redisClient;

    /**
     * A thread-safe Set  Using ChannelGroup, you can categorize Channels into a meaningful group.
     * A closed Channel is automatically removed from the collection,
     */
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    @Qualifier("bipool")
    private ExecutorService executorService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel deviceChannel = ctx.channel();

        log.info("设备[{}]加入", deviceChannel.remoteAddress());
        // Broadcast a message to multiple Channels
        // channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入,总人数:" + channels.size() + "\n");

        channels.add(deviceChannel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel deviceChannel = ctx.channel();
        log.error("移除设备[{}]通道", deviceChannel.remoteAddress());

        String deviceId = DevTcpChannelManager.removeByChannel(deviceChannel);
        if (!StringUtils.isEmpty(deviceId)) {
            DevHttpChannelManager.removeByDeviceId(deviceId);
        }
        // TODO 从 redis 中删除

        // TODO 更新状态到 HTTP
    }


    /**
     * @param ctx
     * @param
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel deviceChannel = ctx.channel();

        log.info("读取设备[{}]发送的消息:{}", deviceChannel.remoteAddress(), msg);

        if (msg instanceof DeviceHttpReq) {
            DeviceHttpReq req = (DeviceHttpReq) msg;
            // 向设备发送请求
            deviceChannel.writeAndFlush(JSONObject.toJSONString(req));
//            deviceChannel.writeAndFlush(Unpooled.copiedBuffer(JSONObject.toJSONString(req) + "\n", CharsetUtil.UTF_8));
        } else if (msg instanceof DeviceServerResp) {
            // 接收设备发送的响应，并将响应发到 http server
            DeviceServerResp resp = (DeviceServerResp) msg;
            if (null == resp.getDeviceID()) {
                System.out.println("resp = " + JSONObject.toJSONString(resp));
                return;
            }
            Channel httpChannel = DevHttpChannelManager.getChannelByDeviceId(resp.getDeviceID());
            if (null != httpChannel) {
                ByteBuf buf = Unpooled.copiedBuffer(JSONObject.toJSONString(resp), CharsetUtil.UTF_8);
                FullHttpResponse response = IOTUtils.responseOK(HttpResponseStatus.OK, buf);
                httpChannel.writeAndFlush(response).addListener((obj) -> {
                    ChannelFuture future = (ChannelFuture) obj;
                    DevHttpChannelManager.removeHttpChannel(resp.getDeviceID(), future.channel());
                    future.channel().close();
                });
            } else {
                System.out.println("resp = " + JSONObject.toJSONString(resp));
            }

        } else if (msg instanceof DevStatusReportReq) {
            // 设备心跳上报请求
            DevStatusReportReq reportReq = (DevStatusReportReq) msg;
            log.info("接收设备[{}]心跳上报请求!", reportReq.getDeviceID());
            String deviceId = reportReq.getDeviceID();
            if (null == DevTcpChannelManager.getChannelByDevice(deviceId)) {
                DevTcpChannelManager.addDeviceChannel(deviceId, ctx.channel());
            }
            reportReq.setTcpValTime(new Date());
            // TODO 更新 redis 中设备状态
            this.redisClient.opsForValue().set(reportReq.getDeviceID(), JSONObject.toJSONString(reportReq), 60*20, TimeUnit.SECONDS);
            this.reportDevStatus2App(reportReq);
//            deviceChannel.writeAndFlush("{\"action\":200}\n");
        } else if (msg instanceof StartWorkingReq) {
            StartWorkingReq startWorkingReq = (StartWorkingReq) msg;
            /*下发沏茶，洗茶，烧水指令，设备收到指令后按对应参数开始工作。
            参数说明：
            soak（设定沏茶时间） 取值与waterlevel（设定出水量）有关，每一档出水量都会对应一个最小沏茶时间，如果传入的设定沏茶时间soak 小于 设定出水量的最小时间则以最小时间为准，屏幕以最小沏茶时间开始倒计时。
            如：设置出水量300ml，抽水+出水时间最少需要90秒（不浸泡），传入的设定沏茶时间soak为70秒，则以最小时间90秒为准，设备屏幕从90秒开始倒计时。
            {"action":"startwork","deviceID":"898398492","msg":{"temperature":100,"soak":120,"waterlevel":200}}
            响应：
            {"retn":200,"desc":"REQSUCCESS","action":"startworkok","deviceID":"898398492","msg":{"state":0,"stateinfo":"EXESUCCESS"}}*/

            StartWork4DevReq startWork4DevReq = new StartWork4DevReq();
            startWork4DevReq.setAction("");
            startWork4DevReq.setDeviceID(startWorkingReq.getDeviceId());
            WorkMsg workMsg = new WorkMsg();
            workMsg.setTemperature(startWorkingReq.getTemperature());
            workMsg.setSoak(startWorkingReq.getSoak());
            workMsg.setWaterlevel(startWorkingReq.getWaterlevel());
            startWork4DevReq.setMsg(workMsg);
            deviceChannel.writeAndFlush(JSONObject.toJSONString(startWork4DevReq));

        } else if (msg instanceof ByteBuf) {
            System.out.println("this is byteBuf msg.");
            deviceChannel.writeAndFlush(msg);
        }
    }

    private void reportDevStatus2App(DevStatusReportReq reportReq) {
        // 发送请求到 HTTP 应用，更新设备状态
        try {
            String result = this.restTemplateUtils.httpPostMediaTypeJson(this.restTemplateUtils.appHttpURL + "/api/rest/currentState", String.class, reportReq);
            log.error("result = {}", result);
        } catch (Exception e) {
            log.error("上报设备状态信息失败！", e);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception { // (5)
        log.info("channelActive channel = {}", ctx.channel().remoteAddress());
        /*final Channel incoming = ctx.channel();

        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        incoming.writeAndFlush(
                                "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                        incoming.writeAndFlush(
                                "Your session is protected by " +
                                        ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                        " cipher suite.\n");

                        channels.add(incoming);
                    }
                });
        log.debug("SimpleChatClient:" + incoming.remoteAddress() + "在线,总人数:" + channels.size() + "\n");*/
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        log.info("SimpleChatClient = " + incoming.remoteAddress() + " 掉线, 总人数 = " + channels.size() + "\n");
//        DeviceChannelManager.removeDeviceChannel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        log.debug("SimpleChatClient:" + incoming.remoteAddress() + "异常,总人数:" + channels.size() + "\n");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}