package com.explik.diybirdyapp.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Serializer<T> {
    public String serialize(T obj) throws SerializerException;

    public T deserialize(String json) throws SerializerException;
}
