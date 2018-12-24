package com.taoyuanx.littlerpc.route.impl;

import java.util.List;

import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.route.Route;


/**
 * 
 * 直连 direct
 */
public  class DirectRoute extends  Route{
	@Override
	public Invoker route(List<Invoker> invokers) {
		return invokers.get(0);
	}
	

}