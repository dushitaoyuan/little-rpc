package com.taoyuanx.littlerpc.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;

/**
 * @author 都市桃源
 * 2018年10月23日 下午3:11:31
 * impl:Hessian2
*/
public class Hessian2Serializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        Hessian2Output ho=new Hessian2Output(os);
        try {
            ho.writeObject(obj);
			ho.flush();
			byte[] result=os.toByteArray();
			ho.close();
            return result;
        } catch (Exception e) {
            throw new RpcException(e);
        }
        
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
        	Object readObject = hi.readObject();
        	hi.close();
            return readObject;
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

}
