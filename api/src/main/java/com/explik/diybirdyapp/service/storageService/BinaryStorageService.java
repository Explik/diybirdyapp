package com.explik.diybirdyapp.service.storageService;

public interface BinaryStorageService {
    byte[] get(String key);

    void set(String key, byte[] value);

    void delete(String key);
}