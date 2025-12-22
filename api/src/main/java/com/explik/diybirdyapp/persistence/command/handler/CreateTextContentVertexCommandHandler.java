package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTextContentVertexCommandHandler implements CommandHandler<CreateTextContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateTextContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateTextContentVertexCommand command) {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setValue(command.getValue());
        vertex.setLanguage(command.getLanguage());
    }
}
