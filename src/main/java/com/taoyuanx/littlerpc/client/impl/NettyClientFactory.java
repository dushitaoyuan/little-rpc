package com.taoyuanx.littlerpc.client.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcException;
import com.taoyuanx.littlerpc.client.Client;
import com.taoyuanx.littlerpc.client.ClientFactory;
import com.taoyuanx.littlerpc.serialize.Serializer;

public class NettyClientFactory  implements ClientFactory{
	private static final Logger LOG=LoggerFactory.getLogger(NettyClientFactory.class);
	private   volatile Map<String,Client> clientHost=new ConcurrentHashMap<>();
	private NettyClientFactory() {
		super();
	}
	private static  NettyClientFactory INSTANCE=null;
	public static NettyClientFactory getInstance() {
		if(INSTANCE==null) {
			INSTANCE=new NettyClientFactory();
		}
		return INSTANCE;
	}

	@Override
	public void close() {
		if (clientHost == null || clientHost.isEmpty()) {
			return;
		}
		for (String ipPort : clientHost.keySet()) {
			clientHost.get(ipPort).close();
		}
		clientHost = null;
	}


	
	private  Client doCreateForAddress(String ipPort,Serializer serializer) {
		try {
			Client client=clientHost.get(ipPort);
			if(client==null) {
				client=new NettyClient(ipPort,serializer);
				clientHost.put(client.getIpPort(), client);
				return client;
			}
			client.addUsed();
			return client;
		} catch (Exception e) {
			LOG.error("netty client {} connect failed ",ipPort,e);
			throw new RpcException("create client "+ipPort+" failed "+e.getMessage());
		}
	}

	@Override
	public Client create(String address, Serializer serializer) {
		return doCreateForAddress(address, serializer);
	}

	@Override
	public void close(String ipPort) {
		Client client = clientHost.get(ipPort);
		if(client!=null) {
			clientHost.remove(ipPort);
			client.close();
		}
		
	}
	
	
	
}
