package com.explik.diybirdyapp.serializer;

public class SerializerException extends RuntimeException {
    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
