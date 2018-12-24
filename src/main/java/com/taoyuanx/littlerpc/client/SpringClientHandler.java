package com.taoyuanx.littlerpc.client;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import com.taoyuanx.littlerpc.anno.RpcReference;
import com.taoyuanx.littlerpc.client.invoker.InvokerFactory;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.registry.RpcURL;
import com.taoyuanx.littlerpc.registry.ServiceRegistry;
import com.taoyuanx.littlerpc.server.ServerConstant;

public class SpringClientHandler extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean,DisposableBean,BeanFactoryAware {
	
	private String hostPort;
	private ServiceRegistry registry;
	private Map<String,Object> registryParam;
	private InvokerFactory invokerFactory;
	
	

	public void setInvokerFactory(InvokerFactory invokerFactory) {
		this.invokerFactory = invokerFactory;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

	public Map<String, Object> getRegistryParam() {
		return registryParam;
	}

	public void setRegistryParam(Map<String, Object> registryParam) {
		this.registryParam = registryParam;
	}
	public void setRegistry(ServiceRegistry registry) {
		this.registry = registry;
	}


	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(RpcReference.class)) {
                    Class<?> service = field.getType();
                    if (!service.isInterface()) {
                        throw new RpcException("RpcReference  must be interface ");
                    }
                    String serviceKey=service.getName();
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    String token=null;
                    //get token from server
                    Set<RpcURL> discovery = registry.discovery(serviceKey);
                    for(RpcURL u:discovery) {
                    	token=u.getParameter(ServerConstant.TOKEN_KEY);
                    	break;
                    }
                    invokerFactory.create(serviceKey,discovery);
                    TargetBean referenceBean = new TargetBean(service, 
                    		rpcReference.version(), 
                    		rpcReference.timeout(), 
                    		rpcReference.callType(), 
                    		invokerFactory, 
                    		rpcReference.route(),token);
                    Object serviceProxy = referenceBean.getTarget();
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);

                }
            }
        });
		return super.postProcessAfterInstantiation(bean, beanName);
	}


	@Override
	public void destroy() throws Exception {
		if(registry!=null) {
			registry.stop();
		}
		invokerFactory.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(registry!=null) {
			registry.start(registryParam);
			return;
		}
		throw new RpcException("registry not config");

	}
	private BeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=beanFactory;
	}

}
