package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.helper.ContentVertexCommandHelper;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Handler for CreateFlashcardContentCommand.
 * Creates a flashcard vertex along with its front and back content vertices
 * without using other command handlers.
 */
@Component
public class CreateFlashcardContentCommandHandler implements CommandHandler<CreateFlashcardContentCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateFlashcardContentCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateFlashcardContentCommand command) {
        var flashcardDto = command.getFlashcardDto();
        
        if (flashcardDto.getFrontContent() == null) {
            throw new IllegalArgumentException("Flashcard is missing front content");
        }
        if (flashcardDto.getBackContent() == null) {
            throw new IllegalArgumentException("Flashcard is missing back content");
        }

        // Create front content vertex
        var frontContentVertex = ContentVertexCommandHelper.createContentVertex(
            traversalSource, 
            flashcardDto.getFrontContent()
        );

        // Create back content vertex
        var backContentVertex = ContentVertexCommandHelper.createContentVertex(
            traversalSource, 
            flashcardDto.getBackContent()
        );

        // Create flashcard vertex
        var flashcardId = flashcardDto.getId() != null ? flashcardDto.getId() : UUID.randomUUID().toString();
        var flashcardVertex = FlashcardVertex.create(traversalSource);
        flashcardVertex.setId(flashcardId);
        flashcardVertex.setLeftContent(frontContentVertex);
        flashcardVertex.setRightContent(backContentVertex);
    }
}
