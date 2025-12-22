package com.explik.diybirdyapp.persistence.generalCommand;

public interface AsyncCommandHandler<T> {
    void handleAsync(T command);
}
