package com.taoyuanx.littlerpc.serialize.impl;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;

/**
 * @author 都市桃源
 * 2018年10月23日 下午3:14:31
 * impl:kryo
*/
public class KryoSerializer implements Serializer {

    /**
     * kryo 不是线程安全的，所以使用池控制
     */
    private static final KryoPool kryoPool = new KryoPool.Builder(
            new KryoFactory() {
                public Kryo create() {
                    Kryo kryo = new Kryo();
                    return kryo;

                }

            }).build();

    @Override
    public <T> byte[] serialize(T obj){
    	try {
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		 Output output = new Output(out);
    		 Kryo kryo = kryoPool.borrow();
             kryo.writeObject(output, obj);
             kryoPool.release(kryo);
             output.flush();
             return out.toByteArray();
		} catch (Exception e) {
			throw new RpcException(e);
		}
     

    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz){
    	try {
    		Input input = new Input(bytes);
    		 Kryo kryo = kryoPool.borrow();
             T res = kryo.readObject(input, clazz);
             kryoPool.release(kryo);
             return res;
		} catch (Exception e) {
			throw new RpcException(e);
		}
        
    }
}
