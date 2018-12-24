package com.taoyuanx.littlerpc.server;

public class ServiceBean {
	private Object target;
	private String version;
	private String token;
	private Integer weight;
	private String service;
	private String serializer;
	
	public ServiceBean(Object target, String version, String token, Integer weight, String service,String serializer) {
		super();
		this.target = target;
		this.version = version;
		this.token = token;
		this.weight = weight;
		this.service = service;
		this.serializer=serializer;
	}
	
	
	public String getSerializer() {
		return serializer;
	}


	public void setSerializer(String serializer) {
		this.serializer = serializer;
	}


	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	


	
}
