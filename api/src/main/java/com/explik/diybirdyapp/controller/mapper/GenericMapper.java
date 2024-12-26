package com.explik.diybirdyapp.controller.mapper;

public interface GenericMapper<T1, T2> {
    T2 map(T1 t1);
}
