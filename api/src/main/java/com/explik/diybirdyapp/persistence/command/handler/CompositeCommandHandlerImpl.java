package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.AtomicCommand;
import com.explik.diybirdyapp.persistence.command.CompositeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Component
public class CompositeCommandHandlerImpl implements CompositeCommandHandler {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void handle(AtomicCommand command) {
        if (command instanceof CompositeCommand compositeCommand) {
            for (var subCommand : compositeCommand.getCommands()) {
                handle(subCommand);
            }
        } else {
            // Find the appropriate handler for this command
            var handlerType = ResolvableType.forClassWithGenerics(
                CommandHandler.class, 
                command.getClass()
            );
            
            try {
                @SuppressWarnings("unchecked")
                CommandHandler<AtomicCommand> handler = 
                    (CommandHandler<AtomicCommand>) applicationContext.getBeanProvider(handlerType).getObject();
                handler.handle(command);
            } catch (Exception e) {
                throw new RuntimeException("Failed to find handler for command: " + command.getClass().getSimpleName(), e);
            }
        }
    }
}
