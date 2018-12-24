package com.taoyuanx.littlerpc.server;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.api.CallType;
import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.registry.RpcURL;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.serialize.Serializer;
import com.taoyuanx.littlerpc.util.Util;


public class RpcProviderHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RpcProviderHandler.class);


	private Serializer serializer;
	
	private Class<? extends ServiceRegistry> serviceRegistryClass;
	private Map<String, Object> serviceRegistryParam;


	public RpcProviderHandler() {
	}
	public void initConfig(Server server,
						   Class<? extends ServiceRegistry> serviceRegistryClass,
						  Map<String, Object> serviceRegistryParam) {
		this.server=server;
		this.serviceRegistryClass = serviceRegistryClass;
		this.serviceRegistryParam = serviceRegistryParam;
	}


	public Serializer getSerializer() {
		return serializer;
	}




	// ---------------------- start / stop ----------------------

	private Server server;
	private ServiceRegistry serviceRegistry;

	public void start() throws Exception {
		Runnable startCallBack=new Runnable() {
			
			@Override
			public void run() {
				try {
					if (serviceRegistryClass != null) {
						serviceRegistry = serviceRegistryClass.newInstance();
						serviceRegistry.start(serviceRegistryParam);
						if (serviceData.size() > 0) {
			
							
							String host = server.getServerContext(ServerConstant.HOST).toString();
							Integer port = server.getServerContext(ServerConstant.PORT);
							String serializerName = server.getServerContext(ServerConstant.SER_KEY);
							for (String service :serviceData.keySet()) {
								serviceRegistry.registry(service,servicesToUrl(service, host, port, serializerName));
							}
						}
					}
				} catch (Exception e) {
					throw new RpcException(e);
				}
				
			}
		};
		server.start(startCallBack,this);
	}

	public void  stop() throws Exception {
		Runnable stopCallBack=new Runnable() {
			
			@Override
			public void run() {
				if (serviceRegistry != null) {
					if (serviceData.size() > 0) {
						
						String host = server.getServerContext(ServerConstant.HOST).toString();
						Integer port = server.getServerContext(ServerConstant.PORT);
						String serializerName = server.getServerContext(ServerConstant.SER_KEY);
						for (String service :serviceData.keySet()) {
							serviceRegistry.remove(service,servicesToUrl(service, host, port, serializerName));
						}
					}
					serviceRegistry.stop();
				}
			}
		};
		server.stop(stopCallBack);
	}



	/**
	 * local rpc service map 
	 * 
	 * key service 
	 * value key:version value:serviceBean
	 */
	private Map<String, Map<String,ServiceBean>> serviceData = new HashMap<String, Map<String,ServiceBean>>();



	/**
	 * invoke service
	 *
	 * @param xxlRpcRequest
	 * @return
	 */
	public RpcResponse invoke(RpcRequest rpcRequest) {
		try {
			String service =rpcRequest.getClassName();
			ServiceBean serviceBean = getServiceBeanTarget(service, rpcRequest.getVersion());
			if (serviceBean == null) {
				RpcResponse rpcResponse = new RpcResponse();
				rpcResponse.setRequestId(rpcRequest.getRequestId());
				rpcResponse.setError(new RpcException("The ServiceBean["+ service +"] not found."));
				return rpcResponse;
			}
			//check token
			String serverToken=serviceBean.getToken();
			if(serverToken!=null&&serverToken.length()>0&&!serverToken.equals(rpcRequest.getToken())) {
				RpcResponse rpcResponse = new RpcResponse();
				rpcResponse.setRequestId(rpcRequest.getRequestId());
				rpcResponse.setError(new RpcException("token not match"));
				return rpcResponse;
			}
		
			
			Object target=serviceBean.getTarget();
			Class<?> serviceClass = target.getClass();
			String methodName = rpcRequest.getMethodName();
			Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
			Object[] parameters = rpcRequest.getParameters();
			CallType callType = CallType.valueOf(rpcRequest.getCallType());
			if(callType==null) {
				throw new RpcException("callType not support");
			}
			if(callType==CallType.ASYNC) {
				Method method = serviceClass.getMethod(methodName, parameterTypes);
				method.setAccessible(true);
				Object result = method.invoke(target, parameters);
				RpcResponse rpcResponse = new RpcResponse();
				rpcResponse.setRequestId(rpcRequest.getRequestId());
				rpcResponse.setResult(result);
				LOG.debug("[{}]request,params:[{}],result:[{}]",rpcRequest.getRequestId(),parameters,result);
				return rpcResponse;
			}
			if(callType==CallType.ONEWAY) {
				Method method = serviceClass.getMethod(methodName, parameterTypes);
				method.setAccessible(true);
				method.invoke(serviceBean, parameters);
				return null;
			}
			throw new RpcException("callType not support");
		} catch (Throwable t) {
			LOG.error("rpcprovider invokeService error.", t);
			RpcResponse rpcResponse = new RpcResponse();
			rpcResponse.setRequestId(rpcRequest.getRequestId());
			rpcResponse.setError(t);
			return rpcResponse;
		}
	
	}
	
	
	/**
	 * add service
	 * @param serviceBean
	 * sort by version
	 */
	
	public static final String NULL_VERSION="0";
	public void addService( ServiceBean serviceBean){
		String service=serviceBean.getService();
		String version=Util.isEmpty(serviceBean.getVersion())? NULL_VERSION:serviceBean.getVersion();
		if(serviceData.containsKey(service)){
			Map<String, ServiceBean> serviceMap = serviceData.get(service);
			if(!serviceMap.containsKey(version)) {
				serviceMap.put(version, serviceBean);
			}else {
				LOG.error("service success. serviceKey = {}, {} version same", service, serviceBean.getTarget().getClass());
			}
		}else{
			Map<String, ServiceBean> serviceMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
            	if(key1.equals(key2)) {
            		return 0;
            	}
            	return Integer.parseInt(key2)-Integer.parseInt(key1);
            }
            
        });
			serviceMap.put(version, serviceBean);
			serviceData.put(service,serviceMap);
		}
		LOG.info("add service success. serviceKey = {}, serviceBean = {}", service, serviceBean.getClass());
	}
	
	public ServiceBean getServiceBeanTarget(String service,String version){
		 Map<String, ServiceBean> serviceMap = serviceData.get(service);
		if(null==serviceMap||serviceMap.isEmpty()){
			return null;
		}
		//just one service return first;
		if(serviceMap.size()==1){
			return getFirst(serviceMap);
		}
		//have version, and versoin  not find  return highest
		if(Util.isNotEmpty(version)) {
			ServiceBean serviceBean = serviceMap.get(version);
			if(serviceBean==null) {
				return getFirst(serviceMap);
			}
			return serviceBean;
			
		}else {
			//version is empty return empty version or highest version
			ServiceBean serviceBean = serviceMap.get(NULL_VERSION);
			if(null==serviceBean) {
				return getFirst(serviceMap);
			}
			return serviceBean;
		}
	}
	
	private ServiceBean getFirst(Map<String, ServiceBean> serviceMap) {
		for(String key:serviceMap.keySet()) {
			return serviceMap.get(key);
		}
		return null;
	}
	//convert service to serviceUrl to regist
	public RpcURL servicesToUrl(String service,String host,Integer port,String serializer){
		Map<String, ServiceBean> serviceMap = serviceData.get(service);
		if(null==serviceMap){
			return null;
		}
		Integer weight=ServerConstant.DEFAULT_WEIGHT;
		//multi version use first weight,token
		String token="";
		for(String key:serviceMap.keySet()) {
			ServiceBean serviceBean = serviceMap.get(key);
			weight=serviceBean.getWeight();
			token=serviceBean.getToken();
			break;
		}
		String version=Util.join(",", serviceMap.keySet().toArray());
		Map<String, String> parameters=new HashMap<>();
		parameters.put(ServerConstant.VERSION_KEY, version);
		parameters.put(ServerConstant.WEIGHT_KEY, String.valueOf(weight));
		parameters.put(ServerConstant.SER_KEY, serializer);
		parameters.put(ServerConstant.TOKEN_KEY, token);
		return new RpcURL(host, port, service, parameters);
		
	}


}
