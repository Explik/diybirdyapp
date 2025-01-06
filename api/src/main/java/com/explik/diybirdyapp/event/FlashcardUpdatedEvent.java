package com.explik.diybirdyapp.event;

import org.springframework.context.ApplicationEvent;

public class FlashcardUpdatedEvent extends ApplicationEvent {
    private final String flashcardId;

    public FlashcardUpdatedEvent(Object source, String flashcardId) {
        super(source);
        this.flashcardId = flashcardId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }
}
