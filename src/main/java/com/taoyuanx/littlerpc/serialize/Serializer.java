package com.taoyuanx.littlerpc.serialize;

/**
 * @author 都市桃源
 * 2018年10月23日 下午3:04:50
*/
public interface Serializer {
	<T> byte[] serialize(T obj);
    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
