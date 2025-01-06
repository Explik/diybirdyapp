package com.explik.diybirdyapp.eventConsumer;

import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class FlashcardEventConsumer {
    private static final Logger Logger = LoggerFactory.getLogger(FlashcardEventConsumer.class);

    @Async
    @EventListener
    public void handleFlashcardAddedEvent(FlashcardAddedEvent event) {
        Logger.info("Flashcard added event received: " + event.getFlashcardId());
    }

    @Async
    @EventListener
    public void handleFlashcardUpdatedEvent(FlashcardUpdatedEvent event) {
        Logger.info("Flashcard updated event received: " + event.getFlashcardId());
    }
}
