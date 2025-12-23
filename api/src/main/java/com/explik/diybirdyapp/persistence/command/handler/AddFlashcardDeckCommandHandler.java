package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.AddFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddFlashcardDeckCommandHandler implements CommandHandler<AddFlashcardDeckCommand> {
    private final GraphTraversalSource traversalSource;

    public AddFlashcardDeckCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(AddFlashcardDeckCommand command) {
        // Fetch owner vertex
        var userVertex = UserVertex.findWithEmail(traversalSource, command.getUserId());
        if (userVertex == null)
            throw new IllegalArgumentException("User with id " + command.getUserId() + " not found");

        // Create flashcard deck vertex
        var flashcardDeckVertex = FlashcardDeckVertex.create(traversalSource);
        var flashcardDeckId = command.getDeckId() != null ? command.getDeckId() : UUID.randomUUID().toString();
        var flashcardDeckName = command.getName() != null ? command.getName() : "";
        flashcardDeckVertex.setId(flashcardDeckId);
        flashcardDeckVertex.setName(flashcardDeckName);
        
        if (command.getDescription() != null) {
            flashcardDeckVertex.setDescription(command.getDescription());
        }
        
        flashcardDeckVertex.setOwner(userVertex);

        // Store result ID for query
        command.setResultId(flashcardDeckVertex.getId());
    }
}
