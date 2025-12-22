package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreatePronunciationVertexCommandHandler implements CommandHandler<CreatePronunciationVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreatePronunciationVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreatePronunciationVertexCommand command) {
        var vertex = PronunciationVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setAudioContent(command.getAudioContentVertex());

        command.getSourceVertex().addPronunciation(vertex);
        if (!command.getSourceVertex().hasMainPronunciation())
            command.getSourceVertex().setMainPronunciation(vertex);
        command.getSourceVertex().makeStatic();
    }
}
