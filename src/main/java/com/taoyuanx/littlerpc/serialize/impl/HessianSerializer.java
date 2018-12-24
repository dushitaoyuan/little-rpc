package com.taoyuanx.littlerpc.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;

/**
 * @author 都市桃源 2018年10月23日 下午3:11:47 impl:Hessian1
 */
public class HessianSerializer implements Serializer {

	@Override
	public <T> byte[] serialize(T obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		HessianOutput ho = new HessianOutput(os);
		try {
			ho.writeObject(obj);
			ho.flush();
			byte[] result = os.toByteArray();
			ho.close();
			return result;
		} catch (Exception e) {
			throw new RpcException(e);
		}

	}

	@Override
	public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		HessianInput hi = new HessianInput(is);
		try {
			Object readObject = hi.readObject();
			hi.close();
			return readObject;
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

}
