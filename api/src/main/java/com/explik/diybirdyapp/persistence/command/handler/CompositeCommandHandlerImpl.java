package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.AtomicCommand;
import org.springframework.stereotype.Component;

@Component
public class CompositeCommandHandlerImpl implements CompositeCommandHandler {
    @Override
    public void handle(AtomicCommand command) {
        // TODO implement logic
    }
}
