package com.taoyuanx.littlerpc.route;

import java.util.List;

import com.taoyuanx.littlerpc.client.invoker.Invoker;

/**
 * @author 都市桃源 2018年11月29日 下午1:37:53 负载算法
 */
public abstract class Route {
	protected String routeName;
	
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}


	public abstract Invoker route(List<Invoker> invokers);

}
