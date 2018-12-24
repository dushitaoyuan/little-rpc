package com.taoyuanx.littlerpc.route.impl;

import java.util.List;
import java.util.Random;

import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.route.Route;


/**
 * 
 * 随机 Random
 */
public  class RandomRoute extends  Route{
	Random random=new Random();

	@Override
	public Invoker route(List<Invoker> invokers) {
		
		if(invokers==null||invokers.isEmpty()) {
			return null;
		}
		Invoker invoker =null;
		if(invokers.size()==1) {
			invoker= invokers.get(0);
		}
		int index = random.nextInt(invokers.size());
		invoker= invokers.get(index);
		if(invoker.canSend()) {
			return invoker;
		}else {
			invokers.remove(index);
			return route(invokers);
		}
	}
	
}