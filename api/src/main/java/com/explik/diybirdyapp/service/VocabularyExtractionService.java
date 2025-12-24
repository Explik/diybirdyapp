package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.persistence.command.ExtractWordsFromFlashcardCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetFlashcardByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.model.content.FlashcardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service for managing vocabulary word extraction from flashcards.
 * Handles coordination for extracting and creating word vertices from flashcard text content.
 */
@Component
public class VocabularyExtractionService {
    @Autowired
    private QueryHandler<GetFlashcardByIdQuery, FlashcardDto> getFlashcardByIdQueryHandler;

    @Autowired
    private CommandHandler<ExtractWordsFromFlashcardCommand> extractWordsFromFlashcardCommandHandler;

    /**
     * Extracts words from a flashcard's text content and creates word vertices.
     * This operation is asynchronous and processes both left and right flashcard sides.
     *
     * @param flashcardId The ID of the flashcard to extract words from
     * @throws RuntimeException if flashcard is not found
     */
    public void extractWordsFromFlashcard(String flashcardId) {
        // Validate input
        if (flashcardId == null || flashcardId.isEmpty()) {
            throw new RuntimeException("Flashcard ID is required");
        }

        // Query to verify flashcard exists
        var query = new GetFlashcardByIdQuery();
        query.setId(flashcardId);
        var flashcard = getFlashcardByIdQueryHandler.handle(query);
        
        if (flashcard == null) {
            throw new RuntimeException("Flashcard not found: " + flashcardId);
        }

        // Execute command to extract words
        var command = new ExtractWordsFromFlashcardCommand();
        command.setFlashcardId(flashcardId);
        extractWordsFromFlashcardCommandHandler.handle(command);
    }
}
