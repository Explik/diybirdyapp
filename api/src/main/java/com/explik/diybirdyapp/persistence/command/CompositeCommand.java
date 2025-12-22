package com.explik.diybirdyapp.persistence.command;

import java.util.List;

// CompositeCommand represents a command that is composed of multiple atomic commands.
// It allows for grouping several operations into a single command for execution.
// Will be used for setup, testing, and other complex operations.
public interface CompositeCommand {
    List<AtomicCommand> getCommands();
}
