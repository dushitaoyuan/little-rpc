package com.taoyuanx.littlerpc.api;

import java.io.Serializable;
import java.util.Arrays;


public class RpcRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2817451561113311076L;
	private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String callType;
    private String version;
   
    private String token;

	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public RpcRequest() {
		super();
	}


	public String getRequestId() {
		return requestId;
	}


	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}


	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}


	public Object[] getParameters() {
		return parameters;
	}


	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}


	public String getCallType() {
		return callType;
	}


	public void setCallType(String callType) {
		this.callType = callType;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	@Override
	public String toString() {
		return "RpcRequest [requestId=" + requestId + ", className=" + className + ", methodName=" + methodName
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) + ", parameters=" + Arrays.toString(parameters)
				+ "]";
	}
	

	
}
