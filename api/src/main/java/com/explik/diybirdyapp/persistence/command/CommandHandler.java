package com.explik.diybirdyapp.persistence.command;

public interface CommandHandler<T> {
    void handle(T command);
}
