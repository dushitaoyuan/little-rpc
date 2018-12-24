package com.taoyuanx.demo.dto;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3337436628907267549L;
	private String username;
	private Integer age;
	private String address;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User() {
		super();
	}
	public User(String username, Integer age, String address) {
		super();
		this.username = username;
		this.age = age;
		this.address = address;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", age=" + age + ", address=" + address + "]";
	}
	
	
	

}
