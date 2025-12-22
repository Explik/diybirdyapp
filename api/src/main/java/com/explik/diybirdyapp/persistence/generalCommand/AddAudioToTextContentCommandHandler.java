package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddAudioToTextContentCommandHandler implements SyncCommandHandler<AddAudioToTextContentCommand, FileContentCommandResult> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandCommandHandler;

    @Override
    public FileContentCommandResult handle(AddAudioToTextContentCommand command) {
        var textContentId = command.getTextId();
        if (textContentId == null || textContentId.isEmpty())
            throw new RuntimeException("Text ID is empty");

        var audioUrl = command.getAudioUrl();
        if (audioUrl == null || audioUrl.isEmpty())
            throw new RuntimeException("Audio URL is empty");

        var textContentVertex = TextContentVertex.findById(traversalSource, command.getTextId());
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + command.getTextId());


        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(UUID.randomUUID().toString());
        createCommand.setAudioUrl(audioUrl);
        createCommand.setSourceVertex(textContentVertex);
        createPronunciationVertexCommandCommandHandler.handle(createCommand);

        return new FileContentCommandResult(audioUrl.getBytes(), "audio/mpeg");
    }
}
