package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateVideoContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.VideoContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateVideoContentVertexCommandHandler implements CommandHandler<CreateVideoContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateVideoContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateVideoContentVertexCommand command) {
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setUrl(command.getUrl());
        var languageVertex = LanguageVertex.findById(traversalSource, command.getLanguageVertexId());
        vertex.setLanguage(languageVertex);
    }
}
