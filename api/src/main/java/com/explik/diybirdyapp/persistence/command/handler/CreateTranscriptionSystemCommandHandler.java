package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateTranscriptionSystemCommand;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionSystemVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTranscriptionSystemCommandHandler implements CommandHandler<CreateTranscriptionSystemCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateTranscriptionSystemCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateTranscriptionSystemCommand command) {
        // Check if transcription system already exists
        var existingSystem = TranscriptionSystemVertex.findById(traversalSource, command.getId());
        if (existingSystem != null) {
            return; // System already exists, no need to create
        }

        // Create new transcription system
        var systemVertex = TranscriptionSystemVertex.create(traversalSource);
        systemVertex.setId(command.getId());
    }
}
