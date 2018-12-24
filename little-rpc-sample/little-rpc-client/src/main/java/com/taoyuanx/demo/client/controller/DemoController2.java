package com.taoyuanx.demo.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taoyuanx.demo.service.MathService;
import com.taoyuanx.littlerpc.anno.RpcReference;

@Component
public class DemoController2 {
	@RpcReference(version="1", route="RoundRoute",timeout=1000)
	MathService mathService;
	public Integer add(Integer a, Integer b) {
		return mathService.add(a, b);
	}

	public Integer sub(Integer a, Integer b) {
		return mathService.sub(a, b);
	}

	public Integer mul(Integer a, Integer b) {
		return mathService.sub(a, b);
	}

	public Integer divide(Integer a, Integer b) {
		return mathService.divide(a, b);
	}

}
