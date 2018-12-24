package com.taoyuanx.littlerpc.serialize.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taoyuanx.littlerpc.serialize.Serializer;

/**
 * @author 都市桃源
 * 2018年10月23日 下午3:12:36
 *impl: Jackson
*/
public class JacksonSerializer implements Serializer {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz)  {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (JsonParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
