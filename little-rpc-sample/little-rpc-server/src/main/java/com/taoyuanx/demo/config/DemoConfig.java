package com.taoyuanx.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.taoyuanx.littlerpc.registry.impl.ZkServiceRegistry;
import com.taoyuanx.littlerpc.server.netty.RpcSpringProviderHandler;

@Configuration
public class DemoConfig {
	@Bean(destroyMethod="stop")
	public RpcSpringProviderHandler config() throws Exception {
		//zk registry
		RpcSpringProviderHandler rpcSpringProviderHandler=new RpcSpringProviderHandler();
		Map<String, Object> param=new HashMap<>();
		param.put(ZkServiceRegistry.SESSIONTIMEOUT, 10000);
		param.put(ZkServiceRegistry.ZKADDRESS, "192.168.91.201:2181");
		rpcSpringProviderHandler.setServiceRegistryClass(ZkServiceRegistry.class);
		rpcSpringProviderHandler.setServiceRegistryParam(param);
		//no registry
		/*RpcSpringProviderHandler rpcSpringProviderHandler=new RpcSpringProviderHandler();
		rpcSpringProviderHandler.setSerializerName(String.valueOf(SerializerEnum.FST));
		rpcSpringProviderHandler.setNetPrefix("192.168.10");*/
		return rpcSpringProviderHandler;
	}
}
