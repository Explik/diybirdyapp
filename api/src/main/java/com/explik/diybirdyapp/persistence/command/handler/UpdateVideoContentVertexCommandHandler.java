package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateVideoContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.VideoContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateVideoContentVertexCommandHandler implements AtomicCommandHandler<UpdateVideoContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateVideoContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateVideoContentVertexCommand command) {
        var vertex = VideoContentVertex.getById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("VideoContentVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getUrl() != null)
            vertex.setUrl(command.getUrl());
        if (command.getLanguageVertex() != null)
            vertex.setLanguage(command.getLanguageVertex());
    }

    private VideoContentVertex copy(VideoContentVertex existingVertex) {
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }
}
