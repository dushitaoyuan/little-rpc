package com.taoyuanx.demo.client.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taoyuanx.demo.client.controller.DemoController2;

public class MainClient {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext app=new ClassPathXmlApplicationContext("applicationContext.xml");
		DemoController2 demoController2 = app.getBean(DemoController2.class);
		for(int i=0;i<100;i++) {
			try {
				System.out.println("add 结果:"+demoController2.add(0, i));
				Thread.sleep(1000L);
			} catch (Exception e) {
				System.out.println("错误:");
				e.printStackTrace();
			}
			
	
		}
		
		app.destroy();
	
	}
}
