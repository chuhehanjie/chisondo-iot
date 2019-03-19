package test.moniTerminal;

import com.alibaba.fastjson.JSONObject;
import com.chisondo.iot.common.utils.IOTUtils;
import com.chisondo.iot.http.request.DeviceHttpReq;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import test.CountHelper;


/**
 * 客户端处理服务端返回信息的处理器
 * @author ding.zhong
 * @date 2017年12月29日 
 * @version V1.0
 */
@Slf4j
public class moniTerminalHandler  extends SimpleChannelInboundHandler<String>{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("模拟终端与网关通道建立。。。。。。");
		System.out.println("模拟终端本地ip：" + ctx.channel().localAddress());
		String data = "{\"action\":\"statuspush\",\"actionFlag\":1,\"deviceID\":\"7788520\",\"msg\":{\"errorstatus\":0,\"nowwarm\":65,\"remaintime\":\"1234\",\"soak\":100,\"taststatus\":2,\"temperature\":70,\"warmstatus\":0,\"waterlevel\":150,\"workstatus\":0}}\\n";
//		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!\n", CharsetUtil.UTF_8));
		ctx.writeAndFlush(Unpooled.copiedBuffer("{\"name\":\"chris\"}\n", CharsetUtil.UTF_8));
        System.out.println("客户端 active 了");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		System.err.println("出现异常。。。");
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// TODO Auto-generated method stub
		/*
		ByteBuf recieveMsg = msg;
		String code = ByteBufUtil.hexDump(recieveMsg).toUpperCase();//将bytebuf中的可读字节 转换成16进制数字符串
		
		System.out.println("msg = " + msg + "接收总数：" + CountHelper.clientRecieveCount.addAndGet(1) + " ;模拟终端收到数据w：" + code);*/
		Channel channel = ctx.channel();
//		String txt = IOTUtils.convertByteBufToString(msg);
		String txt = msg;
		log.info("端设备 [{}] 收到消息 = [{}]", channel.remoteAddress(), txt);
		try {
			DeviceHttpReq req = JSONObject.parseObject(txt, DeviceHttpReq.class);
			if (null != req.getDeviceId() && null != req.getAction()) {
				String data = "{\"retn\":200,\"desc\":\"REQSUCCESS\",\"action\":\"cancelwarmok\",\"deviceID\":\"7788520\",\"msg\":{\"state\":0,\"stateinfo\":\"EXESUCCESS\"}}";
				channel.writeAndFlush(Unpooled.copiedBuffer(data + "\n", CharsetUtil.UTF_8));
				log.info("设备发送消息给设备 TCP 服务");
			} else {
				System.out.println("no avalable msg found");
			}
		} catch (Exception e) {
			log.error("发生异常了！", e);
			channel.writeAndFlush(Unpooled.copiedBuffer("{\"retn\":500,\"desc\":\"error\"}\n", CharsetUtil.UTF_8));
		}

	}
	
}
