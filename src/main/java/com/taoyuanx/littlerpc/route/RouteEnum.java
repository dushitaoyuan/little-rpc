package com.taoyuanx.littlerpc.route;

import java.util.HashMap;
import java.util.Map;

import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.util.Util;

public enum RouteEnum {
	DirectRoute("com.taoyuanx.littlerpc.route.impl.DirectRoute"),
	RandomRoute("com.taoyuanx.littlerpc.route.impl.RandomRoute"),
	RoundRoute("com.taoyuanx.littlerpc.route.impl.RoundRoute"),
	RandomWeightRoute("com.taoyuanx.littlerpc.route.impl.WeightRandomRoute"),
	RoundWeightRoute("com.taoyuanx.littlerpc.route.impl.WeightRoundRoute");
	public String className;

	private RouteEnum(String className) {
		this.className = className;
	}
	private static final Map<String,Route> routes=new HashMap<>();
	public static Route route(String routeName) {
		try {
			if (Util.isEmpty(routeName)) {
				return route("RoundRoute");
			}
			String className=routeName;
			for(RouteEnum r:RouteEnum.values()) {
				if(r.name().equals(routeName)) {
					className=r.className;
				}
			}
			Route route=routes.get(routeName);
			if(route!=null) {
				return route;
			}
			route =  (Route) Class.forName(className).newInstance();
			route.setRouteName(routeName);
			routes.put(routeName, route);
			return route;
		} catch (Exception e) {
			throw new RpcException("实例Route[" + routeName + "] 失败", e);
		}

	}
	
	

	

}
