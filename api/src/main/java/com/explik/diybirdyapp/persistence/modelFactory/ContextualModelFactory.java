package com.explik.diybirdyapp.persistence.modelFactory;

public interface ContextualModelFactory <T1, T2, C> {
    T2 create(T1 vertex, C context);
}
