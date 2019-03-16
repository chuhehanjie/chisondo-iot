package com.chisondo.iot.http.handler;


import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.Device;
import com.chisondo.iot.common.HttpServerReq;
import com.chisondo.iot.device.server.DeviceChannelManager;
import com.chisondo.iot.http.request.StartWorkingReq;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 接收由 http 发送的消息
        Channel channel = ctx.channel();
        if (request.method() == HttpMethod.GET) {

        } else if (request.method() == HttpMethod.POST) {
            String json = this.getJSONFromRequest(request);

        }
        if (true) {
            String data = "Hello World";
            ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
            FullHttpResponse response = this.responseOK(HttpResponseStatus.OK, buf);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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
//        }


    }

    private FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        if (content != null) {
            response.headers().set("Content-Type", "text/plain;charset=UTF-8");
            response.headers().set("Content_Length", response.content().readableBytes());
        }
        return response;
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

    /*
     * 获取GET方式传递的参数
     */
    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> params = new HashMap<String, Object>();
        // 处理get请求
        QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        return params;
    }

    /*
     * 获取POST方式传递的参数
     */
    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        if (fullHttpRequest.method() == HttpMethod.POST) {
            // 处理POST请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if (strContentType.contains("x-www-form-urlencoded")) {
                params  = getFormParams(fullHttpRequest);
            } else if (strContentType.contains("application/json")) {
                try {
                    String json = getJSONFromRequest(fullHttpRequest);
                    params = JSONObject.parseObject(json, Map.class);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            } else {
                return null;
            }
            return params;
        } else {
            return null;
        }
    }

    /*
     * 解析from表单数据（Content-Type = x-www-form-urlencoded）
     */
    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<String, Object>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }

        return params;
    }

    /*
     * 解析json数据（Content-Type = application/json）
     */
    private String getJSONFromRequest(FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<String, Object>();

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String json = new String(reqContent, "UTF-8");
        return json;
    }
}