package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateImageContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateImageContentVertexCommandHandler implements CommandHandler<CreateImageContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateImageContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateImageContentVertexCommand command) {
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setUrl(command.getUrl());
    }
}
