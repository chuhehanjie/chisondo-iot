package com.chisondo.iot.device.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

public class DeviceServerEncoder extends StringEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        System.out.println("encode msg = " + msg);
        super.encode(ctx, msg, out);
    }
}
