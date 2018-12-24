package com.taoyuanx.demo.service.impl;

import org.springframework.stereotype.Component;

import com.taoyuanx.demo.dto.User;
import com.taoyuanx.demo.service.DemoService;
import com.taoyuanx.littlerpc.anno.RpcService;

@RpcService(version="1",weight=100,token="123456")
@Component
public class DemoServiceImpl  implements DemoService{

	@Override
	public String sayHello(String name) {
		
		return "hello little-rpc "+name;
	}

	@Override
	public String sayHelloUser(User user) {
		return "hello little-rpc "+user.toString();
	}

}
