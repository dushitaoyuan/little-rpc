package com.taoyuanx.littlerpc.registry.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taoyuanx.littlerpc.client.invoker.InvokerFactory;
import com.taoyuanx.littlerpc.registry.RpcURL;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.registry.ZkClient;
import com.taoyuanx.littlerpc.util.Util;

/**
 * @author 都市桃源 2018年12月6日 下午4:13:52 
 * /littlerpc/service/url
 * example:
 * "/littlerpc/com.taoyuanx.demo.service.MathService/192.168.92.1%3A8888%2Fcom.taoyuanx.demo.service.MathService%3Fser%3D%26version%3D0%26weight%3D100"
 */
public class ZkServiceRegistry extends ServiceRegistry {
	private static final Logger LOG = LoggerFactory.getLogger(ZkServiceRegistry.class);
	private ZkClient zkClient = null;
	public static final String ZKADDRESS = "zkaddress", SESSIONTIMEOUT = "sessionTimeOut",
			BASEPATH = "/littlerpc",INVOKERFACTORY="invokerFactory";
	private volatile ConcurrentMap<String, Set<RpcURL>> discoveryData = new ConcurrentHashMap<String, Set<RpcURL>>();
	private volatile ConcurrentMap<String, RpcURL> registryData = new ConcurrentHashMap<String, RpcURL>();
	private InvokerFactory invokerFactory;
	@Override
	public void start(Map<String, Object> param) {
		String zkAddress = (String)param.get(ZKADDRESS);
		Integer sessionTimeOut = param.get(SESSIONTIMEOUT)==null ? 15000
				: (Integer)param.get(SESSIONTIMEOUT);
		invokerFactory = param.get(INVOKERFACTORY)==null?null:(InvokerFactory)param.get(INVOKERFACTORY);
		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.Expired) {
					zkClient.closeZk();
					zkClient.getZk();
				}
				EventType type = event.getType();
				if (type == Event.EventType.NodeCreated || type == Event.EventType.NodeDeleted||type == Event.EventType.NodeChildrenChanged) {
					String service = getService(event.getPath());
					if(discoveryData.containsKey(service)) {
						refreshData(service);
						if(invokerFactory!=null) {
							invokerFactory.refreshService(service, discovery(service));
						}
					}
				}

			}
		};
		zkClient = new ZkClient(zkAddress, sessionTimeOut, BASEPATH, watcher);
	}

	@Override
	public void stop() {
		zkClient.closeZk();
	}

	@Override
	public boolean registry(String key, RpcURL url) {
		registryData.put(key, url);
		String servicePath = BASEPATH.concat("/").concat(key);
		List<String> children = zkClient.getChildren(servicePath);
		String value = url.toUrlString();
		if (children == null || children.isEmpty()) {
			zkClient.makeServicePath(servicePath, value);
			return true;
		}
		String flag = RpcURL.encode(url.getHostPort());
		for (String child : children) {
			if (child.contains(flag)) {
				zkClient.deleteService(servicePath, child);
			}
		}
		zkClient.makeServicePath(servicePath, value);
		return true;
	}

	@Override
	public boolean remove(String key, RpcURL url) {
		String servicePath = BASEPATH.concat("/").concat(key);
		zkClient.deleteService(servicePath, url.toUrlString());
		return true;
	}

	@Override
	public Set<RpcURL> discovery(String key) {
		Set<RpcURL> set = discoveryData.get(key);
		if(set==null) {
			String servicePath = BASEPATH.concat("/").concat(key);
			List<String> urls = zkClient.getChildren(servicePath);
			if (null == urls || urls.isEmpty()) {
				set=Collections.emptySet();
			}else {
				set=new HashSet<>(urls.size());
				for (String url : urls) {
					set.add(RpcURL.valueOf(url));
				}
			}
			discoveryData.put(key, set);
		}
		return discoveryData.get(key);
	}

	private void refreshData(String key) {
		if(Util.isEmpty(key)) {
			return;
		}
		String servicePath = BASEPATH.concat("/").concat(key);
		List<String> urls = zkClient.getChildren(servicePath);
		if (null == urls || urls.isEmpty()) {
			discoveryData.put(key, Collections.emptySet());
			return;
		}
		Set<RpcURL> set = new HashSet<>(urls.size());
		for (String url : urls) {
			set.add(RpcURL.valueOf(url));
		}
		discoveryData.put(key, set);	
		LOG.debug("service {} not refresh",key);
	}
	/**
	 * get service from path
	 * @param path
	 * @return
	 */
	private static String getService(String path) {
		if(Util.isEmpty(path)) {
			return null;
		}
		int start=path.indexOf(BASEPATH)+BASEPATH.length()+1;
		int end=path.lastIndexOf("/");
		if(end==BASEPATH.length()) {
			end=path.length();
		}
		return path.substring(start,end);
	}
	public static void main(String[] args) {
		String p="/littlerpc/com.taoyuanx.demo.service.MathService";
		String path="/littlerpc/com.taoyuanx.demo.service.MathService/192.168.92.1%3A8888%2Fcom.taoyuanx.demo.service.MathService%3Fser%3D%26version%3D0%26weight%3D100";
		

	}

}