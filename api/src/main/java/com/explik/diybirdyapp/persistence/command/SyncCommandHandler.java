package com.explik.diybirdyapp.persistence.command;

public interface SyncCommandHandler<T1, T2> {
    T2 handle(T1 command);
}
