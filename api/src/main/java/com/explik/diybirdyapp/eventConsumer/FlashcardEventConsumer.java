package com.explik.diybirdyapp.eventConsumer;

import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import com.explik.diybirdyapp.persistence.generalCommand.AsyncCommandHandler;
import com.explik.diybirdyapp.persistence.generalCommand.ExtractWordsFromFlashcardCommand;
import com.explik.diybirdyapp.persistence.generalCommand.GenerateAudioForFlashcardCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class FlashcardEventConsumer {
    private static final Logger Logger = LoggerFactory.getLogger(FlashcardEventConsumer.class);

    @Autowired
    AsyncCommandHandler<ExtractWordsFromFlashcardCommand> wordCommandHandler;

    @Autowired
    AsyncCommandHandler<GenerateAudioForFlashcardCommand> audioCommandHandler;

    @Async
    @EventListener
    public void handleFlashcardAddedEvent(FlashcardAddedEvent event) {
        Logger.info("Flashcard added event received: " + event.getFlashcardId());

        wordCommandHandler.handleAsync(
                new ExtractWordsFromFlashcardCommand(event.getFlashcardId()));

        audioCommandHandler.handleAsync(
                new GenerateAudioForFlashcardCommand(event.getFlashcardId()));
    }

    @Async
    @EventListener
    public void handleFlashcardUpdatedEvent(FlashcardUpdatedEvent event) {
        Logger.info("Flashcard updated event received: " + event.getFlashcardId());

        wordCommandHandler.handleAsync(
                new ExtractWordsFromFlashcardCommand(event.getFlashcardId()));

        audioCommandHandler.handleAsync(
                new GenerateAudioForFlashcardCommand(event.getFlashcardId()));
    }
}
