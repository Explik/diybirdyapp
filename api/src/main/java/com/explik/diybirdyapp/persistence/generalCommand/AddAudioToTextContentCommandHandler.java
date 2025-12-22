package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.PronunciationVertexFactory;
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
    private PronunciationVertexFactory pronunciationVertexFactory;

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

        var audioId = UUID.randomUUID().toString();
        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId(audioId);
        createAudioCommand.setUrl(audioUrl);
        createAudioCommand.setLanguageVertex(textContentVertex.getLanguage());
        createAudioContentVertexCommandHandler.handle(createAudioCommand);

        var audioVertex = AudioContentVertex.getById(traversalSource, audioId);

        pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options(UUID.randomUUID().toString(), textContentVertex, audioVertex));

        return new FileContentCommandResult(audioUrl.getBytes(), "audio/mpeg");
    }
}
