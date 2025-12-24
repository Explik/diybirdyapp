package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateFlashcardDeckCommandHandler implements CommandHandler<UpdateFlashcardDeckCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateFlashcardDeckCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateFlashcardDeckCommand command) {
        if (command.getDeckId() == null)
            throw new IllegalArgumentException("FlashcardDeck is missing id");

        var query = traversalSource.V().has("flashcardDeck", "id", command.getDeckId());
        if (!query.hasNext())
            throw new IllegalArgumentException("FlashcardDeck with id " + command.getDeckId() + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (!isDeckOwner(command.getUserId(), vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + command.getDeckId() + " not found");

        if (command.getName() != null) {
            vertex.setName(command.getName());
        }
        if (command.getDescription() != null) {
            vertex.setDescription(command.getDescription());
        }

        // Store result ID for query
        command.setResultId(vertex.getId());
    }

    private static boolean isDeckOwner(String userId, FlashcardDeckVertex v) {
        if (v.getOwner() == null)
            return true; // public deck

        return v.getOwner().getEmail().equals(userId);
    }
}
