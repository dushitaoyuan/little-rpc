package com.taoyuanx.littlerpc.registry;

import java.util.Map;
import java.util.Set;

/**
 * @author 都市桃源
 * 2018年10月24日 上午11:03:46
 * service registry
 * key serviceName
 * value serviceUrl
*/
public abstract class ServiceRegistry {
	   /**
     * start
     */
    public abstract void start(Map<String, Object> param);

    /**
     * start
     */
    public abstract void stop();


    /**
     * registry service
     *
     * @param key       service key
     * @param value     service_url
     * @return
     */
    public abstract boolean registry(String key, RpcURL url);


    /**
     * remove service
     *
     * @param key
     * @param value
     * @return
     */
    public abstract boolean remove(String key,RpcURL url);


    /**
     * discovery service
     *
     * @param key   service key
     * @return      service address
     */
    public abstract Set<RpcURL> discovery(String key);
    
    
}
