package com.chisondo.iot.http.handler;


import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.utils.IOTUtils;
import com.chisondo.iot.device.response.DeviceServerResp;
import com.chisondo.iot.device.server.DevTcpChannelManager;
import com.chisondo.iot.http.request.DeviceHttpReq;
import com.chisondo.iot.http.server.DevHttpChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.buffer.Unpooled.copiedBuffer;


/**
 * HTTP服务 hanlder
 * @author ding.zhong
 */
@Slf4j
@Component
@Qualifier("httpServerHandler")
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // (1)

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel deviceChannel = ctx.channel();

        log.info("APP 请求地址:{}", deviceChannel.remoteAddress());
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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // TODO http 路由处理 https://blog.csdn.net/joeyon1985/article/details/53586004
        // 接收由 http 发送的消息
        Channel httpChannel = ctx.channel();
        if (request.method() == HttpMethod.GET) {

        } else if (request.method() == HttpMethod.POST) {
            String json = IOTUtils.getJSONFromRequest(request);
            DeviceHttpReq req = JSONObject.parseObject(json, DeviceHttpReq.class);
            String deviceId = req.getDeviceId();
            Channel deviceChannel = DevTcpChannelManager.getChannelByDevice(req.getDeviceId());
            if (null == deviceChannel) {
                DeviceServerResp devServerResp = new DeviceServerResp(200, "error", "test", deviceId);
                String data = JSONObject.toJSONString(devServerResp);
                ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
                FullHttpResponse response = IOTUtils.responseOK(HttpResponseStatus.OK, buf);
                httpChannel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
            DevHttpChannelManager.addHttpChannel(deviceId, httpChannel);
//            deviceChannel.writeAndFlush(Unpooled.copiedBuffer(req + "\n", CharsetUtil.UTF_8));
            deviceChannel.writeAndFlush(JSONObject.toJSONString(req) + "\n");
        }
        /*if (true) {
            String data = "Hello World";
            ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
            FullHttpResponse response = this.responseOK(HttpResponseStatus.OK, buf);
            channel.writeAndFlush(response).addListener((obj) -> {
                DevHttpChannelManager.removeHttpChannel(deviceId, future.channel());
                ChannelFuture future = (ChannelFuture) obj;
                future.channel().close();
            });
            return;
        }

        // TODO 应该使用 decoder 解码为对象 后续实现
        HttpServerReq req = null; // JSONObject.parseObject(msg.toString(), HttpServerReq.class);

        if (ObjectUtils.nullSafeEquals(req.getType(), "START_WORING")) {
            StartWorkingReq startWorkingReq = JSONObject.parseObject(req.getBody(), StartWorkingReq.class);
            Device device = DeviceChannelManager.getDeviceById(startWorkingReq.getDeviceId());
            Channel deviceChannel = DeviceChannelManager.getChannelByDevice(device);
            deviceChannel.writeAndFlush(startWorkingReq);
        }

        //json 处理使用mongo 的document

        // TODO 接收设备的请求
        //doRule(incoming, s);

//		for (Channel channel : channels) {
//            if (channel != incoming){
//                //channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
//            } else {
//            	channel.writeAndFlush("[you]" + s + "\n");
//            }
//        }*/


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