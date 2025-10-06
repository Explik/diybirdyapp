package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class LockFlashcardContentCommandHandler implements AsyncCommandHandler<LockFlashcardContentCommand> {
    private final GraphTraversalSource traversalSource;

    public LockFlashcardContentCommandHandler(GraphTraversalSource g) {
        this.traversalSource = g;
    }

    @Override
    public void handleAsync(LockFlashcardContentCommand command) {
        FlashcardVertex vertex = FlashcardVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new IllegalArgumentException("Flashcard not found");

        vertex.makeStatic();
    }
}
