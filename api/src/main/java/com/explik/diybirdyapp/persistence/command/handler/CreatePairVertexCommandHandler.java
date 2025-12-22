package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreatePairVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreatePairVertexCommandHandler implements AtomicCommandHandler<CreatePairVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreatePairVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreatePairVertexCommand command) {
        var vertex = PairVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setLeftContent(command.getLeft());
        vertex.setRightContent(command.getRight());
    }
}
