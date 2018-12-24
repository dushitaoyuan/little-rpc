package com.taoyuanx.demo.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainServer {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext app=new ClassPathXmlApplicationContext("applicationContext.xml");
		app.start();
		while(true) {
			Thread.sleep(1000);
		}
	}
}
