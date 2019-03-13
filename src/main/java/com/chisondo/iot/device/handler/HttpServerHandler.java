package com.chisondo.iot.device.handler;


import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.Device;
import com.chisondo.iot.common.HttpServerReq;
import com.chisondo.iot.http.request.StartWorkingReq;
import com.chisondo.iot.device.server.DeviceChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


/**
 * HTTP服务 hanlder
 * @author ding.zhong
 */
@Slf4j
@Component
@Qualifier("httpServerHandler")
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> { // (1)

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel deviceChannel = ctx.channel();

        log.debug("设备[{}]加入", deviceChannel.remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
    }


    /**
     * @param ctx
     * @param
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 接收由 http 发送的消息

        // TODO 应该使用 decoder 解码为对象 后续实现
        HttpServerReq req = JSONObject.parseObject(msg.toString(), HttpServerReq.class);

        if (ObjectUtils.nullSafeEquals(req.getType(), "START_WORING")) {
            StartWorkingReq startWorkingReq = JSONObject.parseObject(req.getBody(), StartWorkingReq.class);
            Device device = DeviceChannelManager.getDeviceById(startWorkingReq.getDeviceId());
            Channel deviceChannel = DeviceChannelManager.getChannelByDevice(device);
            deviceChannel.writeAndFlush(startWorkingReq);
        }

        //json 处理使用mongo 的document
        Channel device = ctx.channel();

        // TODO 接收设备的请求
        //doRule(incoming, s);

//		for (Channel channel : channels) {
//            if (channel != incoming){
//                //channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
//            } else {
//            	channel.writeAndFlush("[you]" + s + "\n");
//            }
//        }


    }

    /**
     * 按规则处理指令
     *
     * @param s
     * @param incoming
     */
    private void doRule(Channel incoming, String s) {
    }



    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception { // (5)

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}