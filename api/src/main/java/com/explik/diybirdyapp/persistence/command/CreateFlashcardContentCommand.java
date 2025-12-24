package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.content.FlashcardDto;

/**
 * Command to create a flashcard vertex along with its content vertices.
 * This command encapsulates the entire flashcard creation process, including
 * creating the front and back content vertices.
 */
public class CreateFlashcardContentCommand implements AtomicCommand {
    private FlashcardDto flashcardDto;

    public FlashcardDto getFlashcardDto() {
        return flashcardDto;
    }

    public void setFlashcardDto(FlashcardDto flashcardDto) {
        this.flashcardDto = flashcardDto;
    }
}
