package com.taoyuanx.littlerpc.client;

import com.taoyuanx.littlerpc.serialize.Serializer;

public interface ClientFactory {
	 
	  Client create(String address,Serializer serializer);
	  void close(String ipPort);
	  void close();
}
