/*package com.taoyuanx.demo.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taoyuanx.demo.dto.User;
import com.taoyuanx.demo.service.DemoService;
import com.taoyuanx.littlerpc.anno.RpcReference;

@Component
public class DemoController {
	@RpcReference(address="127.0.0.1:8888",serializer="FST",token="123456")
	@Autowired
	DemoService demoService;
	public void sayHello(String name) {
		System.out.println(demoService.sayHello(name));;
	}
	
	public void sayHello(User user) {
		System.out.println(demoService.sayHelloUser(user));
	}
}
*/