package com.taoyuanx.littlerpc.client;

import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.serialize.Serializer;

public interface Client {
	void async(RpcRequest request) throws Exception;

	boolean isActive();
	
	void close();
	
	void addUsed();
	
	String getIpPort();
	
	Serializer getSerializer();
	
	
}
