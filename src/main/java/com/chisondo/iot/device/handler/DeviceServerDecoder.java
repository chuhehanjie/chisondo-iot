package com.chisondo.iot.device.handler;

import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.Device;
import com.chisondo.iot.device.request.DevStatusReportReq;
import com.chisondo.iot.device.server.DeviceChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

public class DeviceServerDecoder extends StringDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String json = this.convertByteBufToString(msg);
        System.out.println("decoder msg = " + json);
        if (this.isDevStatusReportReq(json)) {
            DevStatusReportReq reportReq = JSONObject.parseObject(json, DevStatusReportReq.class);
            out.add(reportReq);
        }
    }

    private String convertByteBufToString(ByteBuf buf) {
        String str;
        if(buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    /**
     * 是否设备状态上报请求
     * @param json
     * @return
     */
    private boolean isDevStatusReportReq(String json) {
        return json.contains("\"action\":\"statuspush\"");
    }
}
