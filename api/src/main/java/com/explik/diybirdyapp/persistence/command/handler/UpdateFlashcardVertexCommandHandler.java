package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateFlashcardVertexCommandHandler implements AtomicCommandHandler<UpdateFlashcardVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateFlashcardVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateFlashcardVertexCommand command) {
        var vertex = FlashcardVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("FlashcardVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getLeftContent() != null)
            vertex.setLeftContent(command.getLeftContent());
        if (command.getRightContent() != null)
            vertex.setRightContent(command.getRightContent());
    }

    private FlashcardVertex copy(FlashcardVertex existingVertex) {
        var graphVertex = traversalSource.addV(FlashcardVertex.LABEL).next();
        var vertex = new FlashcardVertex(traversalSource, graphVertex);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setLeftContent(existingVertex.getLeftContent());
        vertex.setRightContent(existingVertex.getRightContent());

        var deckVertex = existingVertex.getDeck();
        deckVertex.addFlashcard(vertex);
        deckVertex.removeFlashcard(existingVertex);

        return vertex;
    }
}
