package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateFlashcardDeckVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateFlashcardDeckVertexCommandHandler implements AtomicCommandHandler<UpdateFlashcardDeckVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateFlashcardDeckVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateFlashcardDeckVertexCommand command) {
        var vertex = FlashcardDeckVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("FlashcardDeckVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getName() != null)
            vertex.setName(command.getName());

        if (command.getFlashcards() != null) {
            // Clear existing flashcards and add new ones
            for(var flashcard : vertex.getFlashcards()) {
                vertex.removeFlashcard(flashcard);
            }
            for(var flashcard : command.getFlashcards()) {
                vertex.addFlashcard(flashcard);
            }
        }
    }

    private FlashcardDeckVertex copy(FlashcardDeckVertex existingVertex) {
        var graphVertex = traversalSource.addV(FlashcardDeckVertex.LABEL).next();
        var vertex = new FlashcardDeckVertex(traversalSource, graphVertex);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setName(existingVertex.getName());

        for(var flashcard : existingVertex.getFlashcards()) {
            vertex.addFlashcard(flashcard);
        }

        return vertex;
    }
}
