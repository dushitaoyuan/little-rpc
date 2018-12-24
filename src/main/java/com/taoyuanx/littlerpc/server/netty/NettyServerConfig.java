package com.taoyuanx.littlerpc.server.netty;

import com.taoyuanx.littlerpc.serialize.Serializer;

public class NettyServerConfig  {
	private Integer port;
	private String host;
	private Long timeOut;
	
	private Integer bossSize;
	private Serializer serializer;
	
	private String serializerName;
	
	
	
	
	public String getSerializerName() {
		return serializerName;
	}
	public void setSerializerName(String serializerName) {
		this.serializerName = serializerName;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public Long getTimeOut() {
		return timeOut;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}
	public Integer getBossSize() {
		return bossSize;
	}
	public void setBossSize(Integer bossSize) {
		this.bossSize = bossSize;
	}
	public Serializer getSerializer() {
		return serializer;
	}
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
