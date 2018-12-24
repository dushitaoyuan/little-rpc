package com.taoyuanx.littlerpc.route.impl;

import java.util.List;
import java.util.Random;

import com.taoyuanx.littlerpc.client.invoker.Invoker;
import com.taoyuanx.littlerpc.route.Route;

/**
 * 
 * 加权随机 WeightRandom
 */
public  class WeightRandomRoute extends  Route{
	Random random=new Random();
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
	            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
	            int offset = random.nextInt(totalWeight);
	            // Return a invoker based on the random value.
	            for (int i = 0; i < length; i++) {
	                offset -= getWeight(invokers.get(i));
	                if (offset < 0) {
	                    return invokers.get(i);
	                }
	            }
	        }
	        // If all invokers have the same weight value or totalWeight=0, return evenly.
	        return invokers.get(random.nextInt(length));
	}

	private int getWeight(Invoker invoker) {
		return invoker.getUrl().getWeight();
	}
	
}