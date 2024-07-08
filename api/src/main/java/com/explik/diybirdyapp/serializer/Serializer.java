package com.explik.diybirdyapp.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface Serializer<T> {
    public String serialize(T obj) throws SerializerException;
    public String serializeList(List<T> obj) throws SerializerException;

    public T deserialize(String json) throws SerializerException;
    public List<T> deserializeList(String json) throws SerializerException;
}
