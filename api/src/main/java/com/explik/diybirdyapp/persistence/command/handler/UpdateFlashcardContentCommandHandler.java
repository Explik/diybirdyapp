package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.helper.ContentVertexCommandHelper;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler for UpdateFlashcardContentCommand.
 * Updates a flashcard vertex along with its front and back content vertices
 * without using other command handlers.
 */
@Component
public class UpdateFlashcardContentCommandHandler implements CommandHandler<UpdateFlashcardContentCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateFlashcardContentCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateFlashcardContentCommand command) {
        var flashcardDto = command.getFlashcardDto();
        
        if (flashcardDto.getId() == null) {
            throw new IllegalArgumentException("Flashcard is missing id");
        }

        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardDto.getId());
        if (flashcardVertex == null) {
            throw new IllegalArgumentException("Flashcard not found");
        }

        // Update deck relation if needed
        if (flashcardDto.getDeckId() != null) {
            var currentDeck = flashcardVertex.getDeck();
            if (currentDeck != null) {
                currentDeck.removeFlashcard(flashcardVertex);
            }

            var flashcardDeckVertex = getFlashcardDeckVertex(traversalSource, flashcardDto.getDeckId());
            flashcardDeckVertex.addFlashcard(flashcardVertex);
        }

        // Update deck order if needed
        if (flashcardDto.getDeckOrder() != null) {
            flashcardVertex.setDeckOrder(flashcardDto.getDeckOrder());
        }

        // Update front content
        if (flashcardDto.getFrontContent() != null) {
            var frontModel = flashcardDto.getFrontContent();
            var frontVertex = flashcardVertex.getLeftContent();
            frontVertex = ContentVertexCommandHelper.createOrUpdateContentVertex(
                traversalSource, 
                frontModel, 
                frontVertex
            );
            flashcardVertex.setLeftContent(frontVertex);
        }

        // Update back content
        if (flashcardDto.getBackContent() != null) {
            var backModel = flashcardDto.getBackContent();
            var backVertex = flashcardVertex.getRightContent();
            backVertex = ContentVertexCommandHelper.createOrUpdateContentVertex(
                traversalSource, 
                backModel, 
                backVertex
            );
            flashcardVertex.setRightContent(backVertex);
        }
    }

    /**
     * Retrieves a flashcard deck vertex by its ID.
     * 
     * @param traversalSource the graph traversal source
     * @param deckId the deck ID
     * @return the FlashcardDeckVertex
     * @throws IllegalArgumentException if the deck is not found
     */
    private static FlashcardDeckVertex getFlashcardDeckVertex(GraphTraversalSource traversalSource, String deckId) {
        var vertex = traversalSource.V()
            .has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId)
            .tryNext();
        if (vertex.isEmpty()) {
            throw new IllegalArgumentException("Flashcard deck not found: " + deckId);
        }
        return new FlashcardDeckVertex(traversalSource, vertex.get());
    }
}
