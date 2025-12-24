package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.DeleteFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteFlashcardDeckCommandHandler implements CommandHandler<DeleteFlashcardDeckCommand> {
    private final GraphTraversalSource traversalSource;

    public DeleteFlashcardDeckCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(DeleteFlashcardDeckCommand command) {
        var query = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, command.getDeckId());
        if (!query.hasNext())
            throw new IllegalArgumentException("FlashcardDeck with id " + command.getDeckId() + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (!isDeckOwner(command.getUserId(), vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + command.getDeckId() + " not found");

        query.next().remove();
    }

    private static boolean isDeckOwner(String userId, FlashcardDeckVertex v) {
        if (v.getOwner() == null)
            return true; // public deck

        return v.getOwner().getEmail().equals(userId);
    }
}
