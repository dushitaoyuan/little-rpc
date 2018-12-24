package com.taoyuanx.littlerpc.server.netty;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.taoyuanx.littlerpc.anno.RpcService;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.serialize.Serializer;
import com.taoyuanx.littlerpc.serialize.SerializerEnum;
import com.taoyuanx.littlerpc.server.RpcProviderHandler;
import com.taoyuanx.littlerpc.server.Server;
import com.taoyuanx.littlerpc.server.ServerConstant;
import com.taoyuanx.littlerpc.server.ServiceBean;
import com.taoyuanx.littlerpc.util.IpUtil;
import com.taoyuanx.littlerpc.util.Util;

public class RpcSpringProviderHandler extends RpcProviderHandler
		implements ApplicationContextAware, InitializingBean, DisposableBean {

	private String serializerName;

	private String ip;
	private String netPrefix;
	private Integer port ;
	
	private Class<? extends ServiceRegistry> serviceRegistryClass; // class.forname
	private Map<String,Object> serviceRegistryParam;

	public void setSerializerName(String serializerName) {
		this.serializerName = serializerName;
	}


	public void setNetPrefix(String netPrefix) {
		this.netPrefix = netPrefix;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setServiceRegistryClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
		this.serviceRegistryClass = serviceRegistryClass;
	}

	public void setServiceRegistryParam(Map<String, Object> serviceRegistryParam) {
		this.serviceRegistryParam = serviceRegistryParam;
	}

	private void init() {
		try {
			// 获取本机ip:可由由用户指定ip ,也可获取指定网段内的ip
			String realIp = ip;
			if (Util.isEmpty(realIp)) {
				if (Util.isNotEmpty(netPrefix)) {
					realIp = IpUtil.getNetAddress(netPrefix);
				} else {
					realIp = IpUtil.getNetAddress();
				}
			}
			ip=realIp;
			if (Util.isEmpty(serializerName)) {
				serializerName=ServerConstant.DEFAULT_SERIALIZER;
			}
			if(port==null) {
				port=ServerConstant.DEFAULT_PORT;
			}
			Serializer serializer = SerializerEnum.serializer(serializerName);
			NettyServerConfig nettyServerConfig = new NettyServerConfig();
			nettyServerConfig.setHost(realIp);
			nettyServerConfig.setSerializer(serializer);
			nettyServerConfig.setTimeOut(ServerConstant.DEFAULT_TIMEOUT);
			nettyServerConfig.setPort(port);
			
			nettyServerConfig.setSerializerName(serializerName);
			Server server = new NettyServer(nettyServerConfig);
			super.initConfig(server, serviceRegistryClass, serviceRegistryParam);
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		init();
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
			serviceBeanMap.forEach((String k, Object target) -> {
				if (target.getClass().getInterfaces().length == 0) {
					throw new RpcException(Util.log4jFormat("service[{}] must inherit a interface", target));
				}
				super.addService(buildServiceBean(target));
			});
		}
	}

	public ServiceBean buildServiceBean(Object target) {
		RpcService rpcService = target.getClass().getAnnotation(RpcService.class);
		String service = target.getClass().getInterfaces()[0].getName();
		Map<String, String> parameters = new HashMap<>();
		String version = rpcService.version();
		String token = rpcService.token();
		int weight = rpcService.weight();
		parameters.put(ServerConstant.WEIGHT_KEY, String.valueOf(weight));
		if (Util.isNotEmpty(token)) {
			parameters.put(ServerConstant.TOKEN_KEY, token);
		}
		if (Util.isNotEmpty(version)) {
			parameters.put(ServerConstant.VERSION_KEY, version);
		}
		ServiceBean serviceBean = new ServiceBean(target, version, token, weight, service, serializerName);
		return serviceBean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.start();
	}

	@Override
	public void destroy() throws Exception {
		super.stop();
	}

}
