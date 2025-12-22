package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreatePronunciationVertexCommandHandler implements CommandHandler<CreatePronunciationVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreatePronunciationVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreatePronunciationVertexCommand command) {
        var audioVertex = AudioContentVertex.create(traversalSource);
        audioVertex.setId(UUID.randomUUID().toString());
        audioVertex.setUrl(command.getAudioUrl());
        audioVertex.setLanguage(command.getSourceVertex().getLanguage());

        var vertex = PronunciationVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setAudioContent(audioVertex);

        command.getSourceVertex().addPronunciation(vertex);
        if (!command.getSourceVertex().hasMainPronunciation())
            command.getSourceVertex().setMainPronunciation(vertex);
        command.getSourceVertex().makeStatic();
    }
}
