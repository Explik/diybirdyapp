package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.LockFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command handler for locking flashcard content.
 * Makes the flashcard content static/immutable in the graph.
 */
@Component
public class LockFlashcardContentCommandHandler implements CommandHandler<LockFlashcardContentCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Override
    public void handle(LockFlashcardContentCommand command) {
        FlashcardVertex vertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());
        if (vertex == null) {
            throw new RuntimeException("Flashcard not found: " + command.getFlashcardId());
        }

        vertex.makeStatic();
    }
}
