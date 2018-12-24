package com.taoyuanx.demo.service.impl;

import org.springframework.stereotype.Component;

import com.taoyuanx.demo.service.MathService;
import com.taoyuanx.littlerpc.anno.RpcService;
@RpcService(version="1",weight=100,token="123456")
@Component
public class MathServiceImpl implements MathService {

	@Override
	public Integer add(Integer a, Integer b) {
		return a+b;
	}

	@Override
	public Integer sub(Integer a, Integer b) {
		return a-b;
	}

	@Override
	public Integer mul(Integer a, Integer b) {
		return a*b;
	}

	@Override
	public Integer divide(Integer a, Integer b) {
		return a/b;
	}

}
