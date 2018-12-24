package com.taoyuanx.littlerpc.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.taoyuanx.littlerpc.ex.RpcException;
import com.taoyuanx.littlerpc.serialize.Serializer;

public class JdkSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {

        try {
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            ObjectOutputStream out=new ObjectOutputStream(byteArr);
            out.writeObject(obj);
            out.flush();
            byte[] data = byteArr.toByteArray();
            out.close();
            return data;
        } catch (Exception e) {
          throw new RpcException(e);
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ObjectInputStream input=new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object readObject = input.readObject();
            input.close();
            return readObject;
        } catch (Exception e) {
        	throw new RpcException(e);
        }
    }

}
