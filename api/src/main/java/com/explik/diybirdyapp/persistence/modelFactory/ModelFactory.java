package com.explik.diybirdyapp.persistence.modelFactory;

public interface ModelFactory<T1, T2> {
    T1 create(T2 vertex);
}
