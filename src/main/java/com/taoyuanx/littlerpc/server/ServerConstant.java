package com.taoyuanx.littlerpc.server;

public class ServerConstant {
	public static final Integer DEFAULT_PORT=8888;
	public static final Long DEFAULT_TIMEOUT=3000L;
	public static final Integer DEFAULT_WEIGHT=100;
	public static final String DEFAULT_SERIALIZER="HESSIAN2";
	
	public static final String HOST_PORT="hostport";
	public static final String PORT="port";
	public static final String HOST="host";
	
	
	
	public static final String VERSION_KEY="version";
	public static final String WEIGHT_KEY="weight";
	public static final String TOKEN_KEY="token";
	public static final String TIMEOUT_KEY="timeout";
	public static final String SER_KEY="ser";
	public static final String SERVICE_URL_SPLIT=",";
}
