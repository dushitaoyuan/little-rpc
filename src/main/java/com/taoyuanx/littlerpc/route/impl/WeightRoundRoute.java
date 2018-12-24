package com.taoyuanx.littlerpc.route.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.route.Route;

/**
 * 加权轮询 WeightRound
 *
 */
public  class WeightRoundRoute extends Route{
	private   AtomicInteger pos=new AtomicInteger(0);

	@Override
	public Invoker route(List<Invoker> invokers) {
		int length = invokers.size(); // Number of invokers
       	int totalWeight = 0; // The sum of weights
        boolean sameWeight = true; // Every invoker has the same weight?
        for (int i = 0; i < length; i++) {
            int weight = getWeight(invokers.get(i));
            totalWeight += weight; // Sum
            if (sameWeight && i > 0
                    && weight != getWeight(invokers.get(i - 1))) {
                sameWeight = false;
            }
        }
       
		if (totalWeight > 0 && !sameWeight) {
			// calc all weight and round
            int offset =round(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                offset -= getWeight(invokers.get(i));
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(round(length));
	}
	
	private int getWeight(Invoker invoker) {
		return invoker.getUrl().getWeight();
	}
	
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

	

}