package com.explik.diybirdyapp.persistence.provider;

public interface GenericProvider<T> {
    T get(String type);
}
