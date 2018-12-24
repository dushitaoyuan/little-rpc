package com.taoyuanx.littlerpc.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;

/**
 * @author 都市桃源
 * 2018年10月23日 下午3:17:35
 * impl fst
*/
public class FstSerializer implements Serializer {
    private static final FSTConfiguration configuration = FSTConfiguration
            .createDefaultConfiguration();
    @Override
    public <T> byte[] serialize(T obj) {
    	try {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			FSTObjectOutput objectOutput = configuration.getObjectOutput(out);
			objectOutput.writeObject(obj);
			objectOutput.flush();
			byte[] data = out.toByteArray();
			objectOutput.close();
			return data;
		} catch (Exception e) {
			throw new RpcException(e);
		}
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
    	try {
			FSTObjectInput objectInput = configuration.getObjectInput(new ByteArrayInputStream(bytes));
			Object readObject = objectInput.readObject();
			objectInput.close();
			return readObject;
		} catch (Exception e) {
			throw new RpcException(e);
		}
    }
    
   

}
