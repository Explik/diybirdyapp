package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateTranscriptionCommand;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionSystemVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTranscriptionCommandHandler implements CommandHandler<CreateTranscriptionCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateTranscriptionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateTranscriptionCommand command) {
        // Find the source content (text content to be transcribed)
        var sourceContent = TextContentVertex.findById(traversalSource, command.getSourceContentId());
        if (sourceContent == null) {
            throw new IllegalArgumentException("Source content with id " + command.getSourceContentId() + " not found");
        }

        // Find the transcription system
        var transcriptionSystem = TranscriptionSystemVertex.findById(traversalSource, command.getTranscriptionSystemId());
        if (transcriptionSystem == null) {
            throw new IllegalArgumentException("Transcription system with id " + command.getTranscriptionSystemId() + " not found");
        }

        // Create the transcription text content vertex
        var textContent = TextContentVertex.create(traversalSource);
        textContent.setId(java.util.UUID.randomUUID().toString());
        textContent.setValue(command.getTextValue());

        // Create the transcription vertex
        var transcriptionVertex = TranscriptionVertex.create(traversalSource);
        transcriptionVertex.setId(command.getId());
        transcriptionVertex.setSourceContent(sourceContent);
        transcriptionVertex.setTextContent(textContent);
        transcriptionVertex.setSystem(transcriptionSystem);
    }
}
