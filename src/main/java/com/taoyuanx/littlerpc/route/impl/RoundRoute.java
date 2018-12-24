package com.taoyuanx.littlerpc.route.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.route.Route;

/**
 * 轮询
 *
 */
public  class RoundRoute extends Route{
	
	private   AtomicInteger pos=new AtomicInteger(0);

	private    int round(Integer size) {
		Integer index=pos.getAndIncrement();
		if(index<size) {
			return index;
		}else {
			pos.set(0);
			pos.incrementAndGet();
			return 0;
		}
	}
	@Override
	public Invoker route(List<Invoker> invokers) {
		if(invokers.isEmpty()) {
			return null;
		}
		Invoker invoker =null;
		if(invokers.size()==1) {
			invoker= invokers.get(0);
		}
		int index =round(invokers.size());
		invoker= invokers.get(index);
		if(invoker.canSend()) {
			return invoker;
		}else {
			invokers.remove(index);
			return route(invokers);
		}
	}

}