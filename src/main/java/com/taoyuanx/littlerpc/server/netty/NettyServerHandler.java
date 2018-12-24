package com.taoyuanx.littlerpc.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.server.RpcProviderHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOG = LoggerFactory.getLogger(NettyServerHandler.class);
    
    private RpcProviderHandler rpcProviderFactory;
    
	public NettyServerHandler(RpcProviderHandler rpcProviderFactory) {
		super();
		this.rpcProviderFactory = rpcProviderFactory;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
		LOG.info("send "+request);
		ctx.writeAndFlush(rpcProviderFactory.invoke(request));
	}
	
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		 LOG.error("netty server caught exception", cause);
		 ctx.close();
	 }



}
