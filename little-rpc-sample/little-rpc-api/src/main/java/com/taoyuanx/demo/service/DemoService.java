package com.taoyuanx.demo.service;


import com.taoyuanx.demo.dto.User;

public interface DemoService {
	String sayHello(String name);
	String sayHelloUser(User user);
}
