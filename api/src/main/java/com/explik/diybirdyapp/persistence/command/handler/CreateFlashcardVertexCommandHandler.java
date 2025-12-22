package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFlashcardVertexCommandHandler implements CommandHandler<CreateFlashcardVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateFlashcardVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateFlashcardVertexCommand command) {
        var graphVertex = traversalSource.addV(FlashcardVertex.LABEL).next();
        var vertex = new FlashcardVertex(traversalSource, graphVertex);
        vertex.setId(command.getId());
        vertex.setLeftContent(command.getLeftContent());
        vertex.setRightContent(command.getRightContent());
    }
}
