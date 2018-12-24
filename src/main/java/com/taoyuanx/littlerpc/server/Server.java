package com.taoyuanx.littlerpc.server;

public interface Server {
	void start(Runnable startCallBack, RpcProviderHandler rpcProviderHandler);
	void stop(Runnable stopCallBack);
	
	<T> T getServerContext(String key);
	
}
