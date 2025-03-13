package com.explik.diybirdyapp.persistence.modelFactory;

public interface ModelFactory<T1, T2> {
    T2 create(T1 vertex);
}
