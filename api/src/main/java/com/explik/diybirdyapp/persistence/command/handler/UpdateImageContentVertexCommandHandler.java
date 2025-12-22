package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateImageContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateImageContentVertexCommandHandler implements CommandHandler<UpdateImageContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateImageContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateImageContentVertexCommand command) {
        var vertex = ImageContentVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("ImageContentVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getUrl() != null)
            vertex.setUrl(command.getUrl());
    }

    private ImageContentVertex copy(ImageContentVertex existingVertex) {
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());

        return vertex;
    }
}
