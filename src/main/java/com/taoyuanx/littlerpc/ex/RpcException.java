package com.taoyuanx.littlerpc.ex;

public class RpcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6589696602058307519L;

	public RpcException() {
		super();
	}

	public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(String message) {
		super(message);
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

}
