package com.taoyuanx.littlerpc.api;

import java.io.Serializable;

public class RpcResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String requestId;
	private Throwable error;
	private Object result;

	public RpcResponse() {
		super();
	}

	public boolean hasError() {
		return error != null;
	}

	

	public RpcResponse(Object result) {
		this.result = result;
	}

	public RpcResponse(Throwable error) {
		this.error = error;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	

	@Override
	public String toString() {
		return "NettyResponse [requestId=" + requestId + ", error=" + error + ", result=" + result + "]";
	}

}
