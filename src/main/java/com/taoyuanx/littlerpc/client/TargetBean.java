package com.taoyuanx.littlerpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.taoyuanx.littlerpc.api.CallType;
import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.client.invoker.InvokerFactory;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.route.Route;
import com.taoyuanx.littlerpc.route.RouteEnum;

public class TargetBean {
	private  Class<?> service;
	private String version;

	private Long timeout;
	private CallType callType;
	private Route route;
	private String token;
	
	private InvokerFactory invokerFactory;
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Class<?> getService() {
		return service;
	}
	public void setService(Class<?> service) {
		this.service = service;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public CallType getCallType() {
		return callType;
	}
	public void setCallType(CallType callType) {
		this.callType = callType;
	}
	
	
	
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
	}
	private String serviceName=null;
	public TargetBean( Class<?> service, String version, Long timeout,
			CallType callType,InvokerFactory invokerFactory,String routeName,String token ) {
		super();
		this.service = service;
		this.version = version;
		this.timeout = timeout;
		this.callType = callType;
		this.invokerFactory=invokerFactory;
		this.serviceName=service.getName();
		this.route=RouteEnum.route(routeName);
		this.token=token;
	}
	
	
	
	public Object getTarget(){
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { service },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						String className = method.getDeclaringClass().getName();
						// request
						RpcRequest request = new RpcRequest();
						request.setCallType(callType.name());
						String requestId=String.valueOf(System.currentTimeMillis());
						request.setRequestId(requestId);
						request.setClassName(className);
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);
						request.setVersion(version);
						request.setToken(token);
						Invoker invoker=route.route(invokerFactory.getInvokers(serviceName));
						if(!invoker.canSend()) {
							throw new RpcException("rpc call failed, ["+invoker.getClient().getIpPort()+"] server downed");
						}
						if (CallType.ASYNC == callType) {
							RpcHolder async = invoker.send(request);
							RpcResponse rpcResponse = async.getRpcResponse();
							RpcHolder.removeHolder(requestId);
							if(rpcResponse.hasError()) {
								throw new RpcException(rpcResponse.getError());
							}else {
								return rpcResponse.getResult();
							}
						} else if (CallType.ONEWAY == callType) {
							 invoker.send(request);
							 return null;
						} else if (CallType.CALLBACK == callType) {
							
						} 
						return null;
					}
				});
	}
	
	
	
	
	
}
