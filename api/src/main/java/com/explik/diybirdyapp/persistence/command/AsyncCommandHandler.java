package com.explik.diybirdyapp.persistence.command;

public interface AsyncCommandHandler<T> {
    void handleAsync(T command);
}
