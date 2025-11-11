package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.AudioContentVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.PronunciationVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddAudioForTextCommandHandler implements SyncCommandHandler<AddAudioForTextCommand, FileContentCommandResult> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Override
    public FileContentCommandResult handle(AddAudioForTextCommand command) {
        var textContentId = command.getTextId();
        if (textContentId == null || textContentId.isEmpty())
            throw new RuntimeException("Text ID is empty");

        var audioUrl = command.getAudioUrl();
        if (audioUrl == null || audioUrl.isEmpty())
            throw new RuntimeException("Audio URL is empty");

        var textContentVertex = TextContentVertex.findById(traversalSource, command.getTextId());
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + command.getTextId());

        var audioVertex = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(UUID.randomUUID().toString(), textContentId, textContentVertex.getLanguage()));

        pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options(UUID.randomUUID().toString(), textContentVertex, audioVertex));

        return new FileContentCommandResult(audioUrl.getBytes(), "audio/mpeg");
    }
}
