package com.taoyuanx.littlerpc.server.netty.codec;

import java.util.List;

import com.taoyuanx.littlerpc.serialize.Serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDcoder extends ByteToMessageDecoder {
	private Serializer serializer;
	private   Class<?> clazz;
	
	public RpcDcoder(Serializer serializer,Class<?> clazz) {
		super();
		this.serializer = serializer;
		this.clazz=clazz;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
		   if (buf.readableBytes() < 4) {
	            return;
	        }
		    buf.markReaderIndex();
	        int dataLength = buf.readInt();
	        if (dataLength < 0) {
	            ctx.close();
	        }
	        if (buf.readableBytes() < dataLength) {
	        	buf.resetReaderIndex();
	            return;
	        }
	        byte[] data = new byte[dataLength];
	        buf.readBytes(data);
	        Object obj = serializer.deserialize(data, clazz);
	        list.add(obj);
	}

}
