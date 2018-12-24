package com.taoyuanx.littlerpc.client.invoker;

import com.taoyuanx.littlerpc.api.CallType;
import com.taoyuanx.littlerpc.api.RpcRequest;
import com.taoyuanx.littlerpc.client.Client;
import com.taoyuanx.littlerpc.client.RpcHolder;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.registry.RpcURL;

public class Invoker {
	private Client client;
	private RpcURL url;
	public Invoker(Client client, RpcURL url) {
		super();
		this.client = client;
		this.url = url;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public RpcURL getUrl() {
		return url;
	}
	public void setUrl(RpcURL url) {
		this.url = url;
	}
	public RpcHolder send(RpcRequest request) throws Exception{
		try {
			CallType type = CallType.valueOf(request.getCallType());
			if (type == CallType.ASYNC) {
				RpcHolder rpcHolder = new RpcHolder(request);
				RpcHolder.addHolder(request.getRequestId(), rpcHolder);
				client.async(request);
				return rpcHolder;
			}
			if(type==CallType.ONEWAY) {
				client.async(request);
				return null;
			}
			 throw new RpcException("callType not support");
		}catch (RpcException e) {
			throw e;
		}catch (Exception e) {
			throw new RpcException("rpc call failed",e);
		}
	}
	public boolean canSend() {
		return client.isActive();
	}
	

	
	
	
	
	
}
