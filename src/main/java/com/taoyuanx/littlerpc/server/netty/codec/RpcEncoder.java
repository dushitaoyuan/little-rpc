package com.taoyuanx.littlerpc.server.netty.codec;

import com.taoyuanx.littlerpc.serialize.Serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends  MessageToByteEncoder<Object> {
	private Serializer serializer;
	
	private Class<?> clazz;
	
	public RpcEncoder(Serializer serializer,Class<?> clazz) {
		super();
		this.serializer = serializer;
		this.clazz=clazz;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Object ecnode, ByteBuf buf) throws Exception {
		if(clazz.isInstance(ecnode)){
			byte[] data = serializer.serialize(ecnode);
			buf.writeInt(data.length);
			buf.writeBytes(data);
		}
	}

	

}
