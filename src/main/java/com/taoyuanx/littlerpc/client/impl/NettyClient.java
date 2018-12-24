package com.taoyuanx.littlerpc.client.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.client.Client;
import com.taoyuanx.littlerpc.client.NettyClientHandler;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;
import com.taoyuanx.littlerpc.server.netty.codec.RpcDcoder;
import com.taoyuanx.littlerpc.server.netty.codec.RpcEncoder;
import com.taoyuanx.littlerpc.util.IpUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient implements Client {
	private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);
	private Channel channel;
	private String ipPort;
	private Serializer serializer;
	private Bootstrap bootstrap;
	private EventLoopGroup group;
	// try to connect server seconds
	public static final Integer TRY_CONNECT_SECOND = 5;
	private ReentrantLock closeLock = new ReentrantLock();
	private volatile boolean isClosed = false;
	private volatile Integer count = 1;

	public NettyClient(String ipPort, Serializer serializer) {
		this.ipPort = ipPort;
		this.serializer = serializer;
		init();
		connect();
	}

	public void init() {
		bootstrap = new Bootstrap();
		group = new NioEventLoopGroup();
		NettyClient client = this;
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel channel) throws Exception {
				channel.pipeline().addLast(new RpcEncoder(serializer, RpcRequest.class))
						.addLast(new RpcDcoder(serializer, RpcResponse.class)).addLast(new NettyClientHandler(client));
			}
		}).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.SO_KEEPALIVE, true);
	}

	public void connect() {
		try {
			String ip = IpUtil.getIp(ipPort);
			Integer port = IpUtil.getPort(ipPort);
			ChannelFuture future = bootstrap.connect(ip, port);
			if (channel != null && channel.isActive()) {
				return;
			}
			// auto reconnect
			future.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture futureListener) throws Exception {
					if (futureListener.isSuccess()) {
						channel = futureListener.channel();
						LOG.info("netty client connect [{}] success", ipPort);
					} else {
						LOG.info("netty client connect [{}] failed try 10s later", ipPort);
						futureListener.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								try {
									connect();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, TRY_CONNECT_SECOND, TimeUnit.SECONDS);
					}
				}
			});

			this.channel = future.channel();
		} catch (Exception e) {
			LOG.error("netty client connect [{}] failed", ipPort, e);
			throw new RpcException(e);
		}
	}

	@Override
	public void async(RpcRequest rpcRequest) throws Exception {
		this.channel.writeAndFlush(rpcRequest).sync();
	}

	public void close() {
		try {
			if (isClosed) {
				return;
			}
			//if this client not used  close really  
			if (count > 1) {
				count(false);
				return;
			}
			closeLock.lock();
			if (channel != null) {
				if (this.channel.isOpen()) {
					this.channel.close();
				}
			}
			if (group != null && !group.isShutdown()) {
				group.shutdownGracefully();
			}
			isClosed = true;
		} catch (Exception e) {
			LOG.error("close netty client {} factpry failed", ipPort, e);
		} finally {
			closeLock.unlock();
		}

	}

	public boolean isActive() {
		if (isClosed) {
			return true;
		}
		if (channel != null) {
			return channel.isActive();
		}
		return true;
	}

	@Override
	public String getIpPort() {
		return ipPort;
	}

	@Override
	public Serializer getSerializer() {
		return serializer;
	}

	@Override
	public void addUsed() {
		count(true);
	}

	private void count(boolean add) {
		if (isClosed) {
			return;
		}
		closeLock.lock();
		if(add) {
			count++;	
		}else {
			count--;
		}
		closeLock.unlock();

	}

}
