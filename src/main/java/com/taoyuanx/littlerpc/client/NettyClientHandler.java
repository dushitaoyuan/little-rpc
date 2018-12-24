package com.taoyuanx.littlerpc.client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.client.impl.NettyClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;


public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
	private static final Logger LOG = LoggerFactory.getLogger(NettyClientHandler.class);
	private NettyClient client;
	
 	public NettyClientHandler(NettyClient client) {
		super();
		this.client = client;
	}


	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("netty client caught exception", cause);
        ctx.close();
    }


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		RpcHolder holder = RpcHolder.getHolder(response.getRequestId());
		LOG.info("response "+response);
		if(holder!=null) {
			holder.setRpcResponse(response);	
		}
	}
	
	  @Override
	  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
	        // handle  client runing server down
	        final EventLoop eventLoop = ctx.channel().eventLoop();
	        eventLoop.schedule(new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		client.connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        }, NettyClient.TRY_CONNECT_SECOND, TimeUnit.SECONDS);
	        super.channelInactive(ctx);
	    }

}
