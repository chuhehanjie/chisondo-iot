package com.chisondo.iot.gate.server.handler;

import java.net.InetSocketAddress;

import com.chisondo.iot.gate.base.cache.ClientChannelCache;
import com.chisondo.iot.gate.base.chachequeue.CacheQueue;
import com.chisondo.iot.gate.base.domain.ChannelData;
import com.chisondo.iot.gate.util.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
/**
 * 妯℃嫙缁堢  鍙戦�� 涓婅 鎶ユ枃
 * @author ding.zhong
 *
 */
public class SocketInHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		Channel channel = ctx.channel();
		InetSocketAddress insocket = (InetSocketAddress)channel.remoteAddress();
		String ipAddress = StringUtils.formatIpAddress(insocket.getHostName(), String.valueOf(insocket.getPort()));
		String clientIpAddress = ipAddress;// ctx.channel().remoteAddress().toString().replaceAll("\\/", "");// clientIpAddress = "127.0.0.1:53956"
		Integer count = CacheQueue.ipCountRelationCache.get(clientIpAddress);
		if(count != null && count.intValue() < 10000){
			CacheQueue.ipCountRelationCache.put(clientIpAddress, count+1);
		}else{
			CacheQueue.ipCountRelationCache.put(clientIpAddress, 1);
		};
		ClientChannelCache.set(clientIpAddress, channel);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		/**
		 * 下线时删除缓存中对应的client的缓存
		 */
		Channel channel = ctx.channel();
		InetSocketAddress insocket = (InetSocketAddress)channel.remoteAddress();
		String ipAddress = StringUtils.formatIpAddress(insocket.getHostName(), String.valueOf(insocket.getPort()));
		String clientIpAddress = ipAddress;
		ClientChannelCache.removeOne(clientIpAddress);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try{
			ChannelData channelData = (ChannelData)msg;
//			SocketData data = channelData.getSocketData();
//			String str=  StringUtils.encodeHex(data.getLenArea())+StringUtils.encodeHex(data.getContent());
//			System.out.println("Server接收到数据"+str);
			CacheQueue.up2MasterQueue.put(channelData);
			
		}finally{
			ReferenceCountUtil.release(msg);
		}
		
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		/**
		 * 发生异常时删除缓存中对应的client的缓存
		 */
		Channel channel = ctx.channel();
		InetSocketAddress insocket = (InetSocketAddress)channel.remoteAddress();
		String ipAddress = StringUtils.formatIpAddress(insocket.getHostName(), String.valueOf(insocket.getPort()));
		String clientIpAddress = ipAddress;
		ClientChannelCache.removeOne(clientIpAddress);
		cause.printStackTrace();
	}
	/**
	 * 心跳检测触发的事件通过本方法捕获
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if(state == state.READER_IDLE ){
				ChannelFuture fcutrue =  ctx.close();
				fcutrue.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						Channel channel = future.channel();
						InetSocketAddress insocket = (InetSocketAddress)channel.remoteAddress();
						String ipAddress = StringUtils.formatIpAddress(insocket.getHostName(), String.valueOf(insocket.getPort()));
						String key = ipAddress;
						System.out.println(key+"心跳超时下线成功....");
//						ClientChannelCache.removeOne(key);
					}
				});
			}
		}else{  
			super.userEventTriggered(ctx, evt);
		}
		
	}
	
	

}
