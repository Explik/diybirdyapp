package com.explik.diybirdyapp.persistence.generalCommand;

public interface SyncCommandHandler<T1, T2> {
    T2 handle(T1 command);
}
