package com.taoyuanx.demo.client.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.taoyuanx.littlerpc.client.SpringClientHandler;
import com.taoyuanx.littlerpc.client.impl.NettyClientFactory;
import com.taoyuanx.littlerpc.client.invoker.InvokerFactory;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.registry.impl.ZkServiceRegistry;

@Configuration
public class DemoClientConfig {
	@Bean
	public SpringClientHandler config() throws Exception {
		SpringClientHandler clientHandler = new SpringClientHandler();
		InvokerFactory invokerFactory = InvokerFactory.getInstance();
		invokerFactory.setClientFactory(NettyClientFactory.getInstance());
		clientHandler.setInvokerFactory(invokerFactory);
		//设置 registry
		ServiceRegistry registry=new ZkServiceRegistry();
		Map<String, Object> param=new HashMap<>();
		param.put(ZkServiceRegistry.SESSIONTIMEOUT, 10000);
		param.put(ZkServiceRegistry.ZKADDRESS, "192.168.91.201:2181");
		param.put(ZkServiceRegistry.INVOKERFACTORY, invokerFactory);
		clientHandler.setRegistry(registry);
		clientHandler.setRegistryParam(param);
		return clientHandler;
	}
}
