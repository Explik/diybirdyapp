package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.AtomicCommand;

public interface CommandHandler<T extends AtomicCommand> {
    void handle(T command);
}
