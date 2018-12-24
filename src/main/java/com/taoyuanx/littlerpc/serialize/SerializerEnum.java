package com.taoyuanx.littlerpc.serialize;

import java.util.HashMap;
import java.util.Map;

import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.util.Util;

public enum SerializerEnum {
	FST("com.taoyuanx.littlerpc.serialize.impl.FstSerializer"),
	HESSIAN("com.taoyuanx.littlerpc.serialize.impl.HessianSerializer"),
	HESSIAN2("com.taoyuanx.littlerpc.serialize.impl.HessianSerializer"),
	JACKSON("com.taoyuanx.littlerpc.serialize.impl.JacksonSerializer"),
	JDK("com.taoyuanx.littlerpc.serialize.impl.JdkSerializer"),
	KRYO("com.taoyuanx.littlerpc.serialize.impl.KryoSerializer"),
	PROTOSTUFF("com.taoyuanx.littlerpc.serialize.impl.FstSerializer");
	public String className;
	private SerializerEnum( String className) {
		this.className = className;
	}
	private static final Map<String,Serializer> sers=new HashMap<>();
	public static Serializer serializer (String serializerName) {
		
		try {
			if(Util.isEmpty(serializerName)) {
				return  serializer("HESSIAN2");
			}
			
			String className=serializerName;
			for(SerializerEnum s:SerializerEnum.values()) {
				if(s.name().equals(serializerName)) {
					className=s.className;
				}
			}
			Serializer serializer=sers.get(serializerName);
			if(serializer!=null) {
				return serializer;
			}
			
			serializer=(Serializer)Class.forName(className).newInstance();
			sers.put(serializerName, serializer);
			return serializer;
		} catch (Exception e) {
			throw new RpcException("实例Serializer["+serializerName+"] 失败");
		}
		
	}
	
}
