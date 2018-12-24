package com.taoyuanx.littlerpc.client.invoker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.taoyuanx.littlerpc.client.ClientFactory;
import com.taoyuanx.littlerpc.registry.RpcURL;
import com.taoyuanx.littlerpc.serialize.SerializerEnum;
import com.taoyuanx.littlerpc.server.ServerConstant;

public class InvokerFactory {
	

	private ClientFactory clientFactory;
	
	
	
	private static  InvokerFactory INSTANCE=null;
	public static InvokerFactory getInstance() {
		if(INSTANCE==null) {
			INSTANCE=new InvokerFactory();
		}
		return INSTANCE;
	}
	

	private InvokerFactory() {
		super();
	}
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private   volatile Map<String,List<Invoker>> invokerService=new ConcurrentHashMap<>();
	public List<Invoker> create(String service,Set<RpcURL> urls){
	
		for(RpcURL u:urls) {
			doCreateInvoker(service,u);
		}
		return invokerService.get(service);
	}
	
	
	public void refreshService(String service,Set<RpcURL> urls) {
		if(urls==null||urls.isEmpty()) {
			return;
		}
		Set<String> ipPorts=new HashSet<>();
		String ipPort=null;
		//add
		for(RpcURL u:urls) {
			ipPort=u.getHostPort();
			ipPorts.add(ipPort);
			doCreateInvoker(service,u);
		}
		//reomve invoker;
		List<Invoker> list = invokerService.get(service);
		Iterator<Invoker> it = list.iterator();
		while(it.hasNext()) {
			Invoker invoker=it.next();
			ipPort=invoker.getClient().getIpPort();
			if(!ipPorts.contains(ipPort)) {
				it.remove();
				close(service,ipPort);
			}
		}
	}
	public void close() {
		clientFactory.close();
	}
	
	private void close(String service,String ipPort) {
		List<Invoker> list = invokerService.get(service);
		int index = invokerContains(ipPort, list);
		if(index!=-1) {
			Invoker remove = list.remove(index);
			remove.getClient().close();
		}
	}
	
	
	public List<Invoker> getInvokers(String service){
		return invokerService.get(service);
	}
	
	private void doCreateInvoker(String service,RpcURL url) {
		List<Invoker> list = invokerService.get(service);
		String ipPort=url.getHostPort();
		if(list==null) {
			list=new CopyOnWriteArrayList<Invoker>();
			list.add(new Invoker(clientFactory.create(ipPort, 
					SerializerEnum.serializer(url.getParameter(ServerConstant.SER_KEY))), url));
			invokerService.put(service, list);
		}else {
			if(invokerContains(ipPort, list)!=-1) {
				list.add(new Invoker(clientFactory.create(ipPort, 
						SerializerEnum.serializer(url.getParameter(ServerConstant.SER_KEY))), url));
			}
		}
	}
	
	private int invokerContains(String ipPort,List<Invoker> list) {
		if(null==list||list.isEmpty()) {
			return -1;
		}
		for(int i=0,len=list.size();i<len;i++) {
			if(list.get(i).getClient().getIpPort().equals(ipPort)) {
				return i;
			}
		}
		return -1;
	
	}
	
}
