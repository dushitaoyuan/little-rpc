package com.taoyuanx.littlerpc.registry;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkClient {
	private static final Logger LOG=LoggerFactory.getLogger(ZkClient.class);
	private String zkAddress;
	private Integer sessionTimeOut;
	private ZooKeeper zooKeeper;
	private ReentrantLock lock = new ReentrantLock(true);
	private Watcher watcher;
	
	private String basePath;
	
	public ZkClient(String zkAddress, Integer sessionTimeOut,String basePath,Watcher watcher) {
		this.zkAddress = zkAddress;
		this.sessionTimeOut = sessionTimeOut;
		this.basePath=basePath;
		this.watcher=watcher;
		getZk();
		try {
			if(zooKeeper.exists(basePath, false)==null) {
				zooKeeper.create(basePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,  CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			throw new RuntimeException("init zk failed",e);
		}
	}


	public ZooKeeper getZk() {
		try {
			if(zooKeeper!=null) {
				return zooKeeper;
			}
			lock.lock();
			if(zooKeeper==null) {
				zooKeeper=new ZooKeeper(zkAddress, sessionTimeOut, watcher);
			}
			return zooKeeper;
		} catch (Exception e) {
			LOG.error("connect  zk {} failed",zkAddress);
			throw new RuntimeException("zk connect failed");
		}finally {
			if(lock.isLocked()) {
				lock.unlock();
			}
			
		}
	}
	
	public void closeZk() {
		try {
			if(zooKeeper!=null) {
				zooKeeper.close();
			}
		} catch (Exception e) {
			LOG.error("close  zk {} failed",zkAddress);
		}
	}
	public void makeServicePath(String service,String value) {
		try {
			ZooKeeper zk = getZk();
			Stat serviceFlag = zk.exists(service,true);
			if(serviceFlag==null) {
				zk.create(service, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			value=service.concat("/").concat(value);
			Stat childFlag = zk.exists(value,true);
			if(childFlag==null) {
				zk.create(value,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}
		} catch (Exception e) {
			LOG.error("create service {} path failed,",service,e);
		}
	}
	
	public void deleteService(String service,String value) {
		try {
			ZooKeeper zk = getZk();
			value=service.concat("/").concat(value);
			Stat stat = zk.exists(value,true);
			if(stat!=null) {
				zk.delete(value, stat.getVersion());
			}
		} catch (Exception e) {
			LOG.error("delete  service {} path failed,",service,e);
		}
	}
	
	public List<String> getChildren(String path){
		try {
			ZooKeeper zk = getZk();
			List<String> children = zk.getChildren(path, false);
			return children;
		} catch (Exception e) {
			LOG.error("get  service {} childre failed,",path,e);
			return null;
		}
	}
	public void setData(String path,String data){
		try {
			ZooKeeper zk = getZk();
			Stat exists = zk.exists(path, true);
			if(exists!=null) {
				zk.setData(path, data.getBytes(), exists.getAversion());
			}
			
		} catch (Exception e) {
			LOG.error("set  data for path {} failed,",path,e);
		}
	}
	static ZkClient zkClient=null;
	public static void main(String[] args) throws Exception {
		Watcher watcher=new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.Expired) {
					zkClient.closeZk();
					zkClient.getZk();
				}
				// NodeCreated|NodeDeleted
				if(event.getType()== Event.EventType.NodeCreated) {
					System.out.println("type "+event.getType()+"\t path "+event.getPath());
				}
				if(event.getType()== Event.EventType.NodeDataChanged) {
					System.out.println("type "+event.getType()+"\t path "+event.getPath());
				}
				if(event.getType()== Event.EventType.NodeDeleted) {
					System.out.println("type "+event.getType()+"\t path "+event.getPath());
				}
				if(event.getType()== Event.EventType.NodeChildrenChanged) {
					System.out.println("type "+event.getType()+"\t path "+event.getPath());
				}
				
				
			}
		};
		String basePath="/littlerpc";
		zkClient=new ZkClient("192.168.91.201", 15000, basePath, watcher);
		String service=basePath.concat("/").concat("com.taoyuanx.demo.service.DemoService");
		zkClient.makeServicePath(service,"1231213:1222");
		/*zkClient.makeServicePath(service,"192.168.30.101:1222");
		zkClient.deleteService(service, "192.168.30.100:1222");
		zkClient.makeServicePath(service,"192.168.30.102:1222");
		zkClient.setData(service.concat("/").concat("192.168.30.102:1222"), "123");
		System.out.println(Arrays.toString(zkClient.getChildren(service).toArray()));
	    Thread.sleep(10000000);*/
	
		
	}
	
}
