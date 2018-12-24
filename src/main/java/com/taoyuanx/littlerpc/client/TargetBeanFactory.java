package com.taoyuanx.littlerpc.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.taoyuanx.littlerpc.api.CallType;
import com.taoyuanx.littlerpc.client.impl.NettyClientFactory;
import com.taoyuanx.littlerpc.client.invoker.InvokerFactory;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.registry.RpcURL;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.route.RouteEnum;
import com.taoyuanx.littlerpc.server.ServerConstant;
import com.taoyuanx.littlerpc.util.IpUtil;
import com.taoyuanx.littlerpc.util.Util;

public class TargetBeanFactory implements FactoryBean<Object>, InitializingBean,DisposableBean {
	private String service;
	private String version;
	private String route;
	private Long timeout;
	private String token;
	


	// array , spilit
	private String weight;
	private String serializer;
	private String address;
	
	@Autowired(required=false)
	ServiceRegistry serviceRegistry;
	
	private TargetBean targetBean=null;
	private Class<?> type;
	
	private   static InvokerFactory invokerFactory;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(invokerFactory==null) {
			invokerFactory = InvokerFactory.getInstance();
			invokerFactory.setClientFactory(NettyClientFactory.getInstance());
		}
		if(type==null) {
			type=Class.forName(service);
		}
		if(Util.isEmpty(address)) {
			if(serviceRegistry==null) {
				throw new RpcException("if address is null,serviceRegistry is necessary");
			}
			Set<RpcURL> urls = serviceRegistry.discovery(service);
			if(urls==null) {
				throw new RpcException("service "+service+" provider not found");
			}
			Set<RpcURL> discovery = serviceRegistry.discovery(service);
			invokerFactory.create(service, discovery);
			if(discovery.size()>1) {
				route=String.valueOf(RouteEnum.RoundWeightRoute);
			}else {
				route=String.valueOf(RouteEnum.DirectRoute);
			}
			targetBean=new TargetBean(type, version, timeout, CallType.ASYNC, invokerFactory, route,token);
		}else {
			Set<RpcURL> buildRpcURL = buildRpcURL(service);
			invokerFactory.create(service, buildRpcURL);
			if(timeout==null) {
				timeout=ServerConstant.DEFAULT_TIMEOUT;
			}
			if(buildRpcURL.size()>1) {
				route=String.valueOf(RouteEnum.RoundWeightRoute);
			}else {
				route=String.valueOf(RouteEnum.DirectRoute);
			}
			targetBean=new TargetBean(type, version, timeout, CallType.ASYNC, invokerFactory, route,token);
		}
		
		
	
	}

	@Override
	public Object getObject() throws Exception {
		return targetBean.getTarget();
	}

	@Override
	public Class<?> getObjectType() {
		return type;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSerializer() {
		return serializer;
	}

	public void setSerializer(String serializer) {
		this.serializer = serializer;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void destroy() throws Exception {
		invokerFactory.close();
	}

	public Set<RpcURL> buildRpcURL(String service) {
		String[] addresses = address.split(",");
		if(addresses==null||addresses.length==0) {
			return null;
		}
	 	Set<RpcURL> urls=new HashSet<>();
		String timeOut=String.valueOf(timeout);
		String[] weights = Util.isEmpty(weight)? null:weight.split(",");
		String[] serializers = Util.isEmpty(serializer)? null:serializer.split(",");
		
		for(int i=0,len=addresses.length;i<len;i++) {
			String addr=addresses[i];
			String ip = IpUtil.getIp(addr);
			int port=IpUtil.getPort(addr);
			Map<String, String> parameters=new HashMap<>();
			parameters.put(ServerConstant.TIMEOUT_KEY, timeOut);
			if(serializers!=null&&serializers.length>0) {
				if(i<serializers.length) {
					parameters.put(ServerConstant.SER_KEY, serializers[i]);
				}else {
					parameters.put(ServerConstant.SER_KEY, ServerConstant.DEFAULT_SERIALIZER);
				}
			}
			if(Util.isNotEmpty(version)) {
				parameters.put(ServerConstant.VERSION_KEY, version);
			}
			if(Util.isNotEmpty(token)) {
				parameters.put(ServerConstant.TOKEN_KEY, token);
			}
			if(weights!=null&&weights.length>0) {
				if(i<weights.length) {
					parameters.put(ServerConstant.WEIGHT_KEY, String.valueOf(weights[i]));
				}else {
					parameters.put(ServerConstant.WEIGHT_KEY, String.valueOf(ServerConstant.DEFAULT_WEIGHT));
				}
			}
			urls.add(new RpcURL(ip, port, service, parameters));
    	}
		return urls;
		
	}
	
	
}
