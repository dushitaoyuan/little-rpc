package com.taoyuanx.demo.service.impl;

import org.springframework.stereotype.Component;

import com.taoyuanx.demo.dto.User;
import com.taoyuanx.demo.service.DemoService;
import com.taoyuanx.littlerpc.anno.RpcService;

@RpcService(version="2")
@Component
public class DemoServiceImpl2  implements DemoService{

	@Override
	public String sayHello(String name) {
		
		return "hello little-rpc v2 "+name;
	}

	@Override
	public String sayHelloUser(User user) {
		return "hello little-rpc v2"+user.toString();
	}

}
