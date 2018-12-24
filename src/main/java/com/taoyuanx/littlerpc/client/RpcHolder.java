package com.taoyuanx.littlerpc.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.api.RpcResponse;
import com.taoyuanx.littlerpc.ex.RpcException;

public class RpcHolder {
	private RpcRequest rpcRequest;
	private RpcResponse rpcResponse;
	private volatile boolean isResp = false;
	public static final Long DEFAULT_TIME_OUT=3000L;
	private static final Map<String, RpcHolder> holderPool = new ConcurrentHashMap<>();

	public RpcHolder(RpcRequest rpcRequest) {
		super();
		this.rpcRequest = rpcRequest;
	}

	public static Integer size() {
		return holderPool.size();
	}

	public RpcRequest getRpcRequest() {
		return rpcRequest;
	}

	public void setRpcRequest(RpcRequest rpcRequest) {
		this.rpcRequest = rpcRequest;
	}

	public RpcResponse getRpcResponse() {
		try {
			return getRpcResponse(DEFAULT_TIME_OUT);
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

	public void setRpcResponse(RpcResponse rpcResponse) {
		synchronized (this) {
			this.rpcResponse = rpcResponse;
			this.notifyAll();
			isResp = true;
		}
	}

	public static void addHolder(String requestId, RpcHolder rpcHolder) {
		holderPool.put(requestId, rpcHolder);
	}

	public static void removeHolder(String requestId) {
		holderPool.remove(requestId);
	}

	public static RpcHolder getHolder(String requestId) {
		return holderPool.get(requestId);
	}
	
	public static Map<String, RpcHolder>  getPool() {
		return holderPool;
	}

	public RpcResponse getRpcResponse(Long timeOut) throws Exception {
		if (!isResp) {
			synchronized (this) {
				try {
					this.wait(timeOut);
				} catch (Exception e) {
					throw e;
				}

			}
		}
		if (!isResp) {
			throw new RpcException("rpcrequest:[" + rpcRequest + "] timeout");
		}
		return rpcResponse;
	}
}
