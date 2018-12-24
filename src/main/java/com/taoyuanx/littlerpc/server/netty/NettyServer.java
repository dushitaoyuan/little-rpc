package com.taoyuanx.littlerpc.server.netty;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.serialize.SerializerEnum;
import com.taoyuanx.littlerpc.server.RpcProviderHandler;
import com.taoyuanx.littlerpc.server.Server;
import com.taoyuanx.littlerpc.server.ServerConstant;
import com.taoyuanx.littlerpc.server.netty.codec.RpcDcoder;
import com.taoyuanx.littlerpc.server.netty.codec.RpcEncoder;
import com.taoyuanx.littlerpc.util.IpUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer implements Server {

	private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	private NettyServerConfig nettyServerConfig;
	private Map<String,Object> serverContext=new HashMap<>();

	public NettyServer(NettyServerConfig nettyServerConfig) {
		this.nettyServerConfig = nettyServerConfig;
	}

	public NettyServer() {
			nettyServerConfig = new NettyServerConfig();
			nettyServerConfig.setPort(ServerConstant.DEFAULT_PORT);
			nettyServerConfig.setHost(IpUtil.getNetAddress());
			nettyServerConfig.setSerializer(SerializerEnum.serializer(null));
	}

	private Thread thread;



	@Override
	public void start(Runnable startCallBack,RpcProviderHandler rpcProviderHandler) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Integer bossSize = null;
				if (nettyServerConfig.getBossSize() == null) {
					bossSize = Runtime.getRuntime().availableProcessors() * 2;
				} else {
					bossSize = nettyServerConfig.getBossSize();
				}
				EventLoopGroup bossGroup = new NioEventLoopGroup(bossSize);
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline()
							.addLast(new RpcEncoder(nettyServerConfig.getSerializer(),RpcResponse.class))
							.addLast(new RpcDcoder(nettyServerConfig.getSerializer(),RpcRequest.class))
							.addLast(new NettyServerHandler(rpcProviderHandler));
						}
					}).option(ChannelOption.SO_SNDBUF, 32 * 1024)
					  .option(ChannelOption.SO_RCVBUF, 32 * 1024)
					  .option(ChannelOption.SO_TIMEOUT, 100)
					  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,nettyServerConfig.getTimeOut().intValue())
					  .option(ChannelOption.SO_BACKLOG, 128)
					  .option(ChannelOption.TCP_NODELAY, true)
					  .option(ChannelOption.SO_REUSEADDR, true)
					  .childOption(ChannelOption.SO_KEEPALIVE, true);
					String ip=nettyServerConfig.getHost();
					serverContext.put(ServerConstant.HOST_PORT, IpUtil.getIpPort(ip, nettyServerConfig.getPort()));
					serverContext.put(ServerConstant.PORT,nettyServerConfig.getPort());
					serverContext.put(ServerConstant.HOST, ip);
					serverContext.put(ServerConstant.TIMEOUT_KEY, nettyServerConfig.getTimeOut());
					serverContext.put(ServerConstant.SER_KEY, nettyServerConfig.getSerializerName());
					if(null!=startCallBack){
						startCallBack.run();
					}
					LOG.info("NettyServer start port:{},ip:{}",nettyServerConfig.getPort(),ip);
					ChannelFuture future = bootstrap.bind(nettyServerConfig.getPort()).sync();
					future.channel().closeFuture().sync().channel();
					
				} catch (Exception e) {
					LOG.error("netty server start error:[{}]",e);
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void stop(Runnable stopCallBack) {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
			LOG.info("NettyServer stop");
		}
		if(null!=stopCallBack){
			stopCallBack.run();
		}
	}

	public Map<String, Object> getServerContext() {
		return serverContext;
	}

	public <T> T getServerContext(String key) {
		return (T) serverContext.get(key);
	}
	
	


	

}
