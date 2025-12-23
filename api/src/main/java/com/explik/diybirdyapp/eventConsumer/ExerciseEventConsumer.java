package com.explik.diybirdyapp.eventConsumer;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.event.ExerciseAnsweredEvent;
import com.explik.diybirdyapp.persistence.command.HandleFlashcardPronunciationExerciseAnswerCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExerciseEventConsumer {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ExerciseEventConsumer.class);

    @Autowired
    private CommandHandler<HandleFlashcardPronunciationExerciseAnswerCommand> commandHandler;

    @Async
    @EventListener
    public void handleExerciseAnsweredEvent(ExerciseAnsweredEvent event) {
        Logger.info("Exercise answered event received: " + event.getExerciseId());

        if (event.getExerciseType().equals(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            var command = new HandleFlashcardPronunciationExerciseAnswerCommand();
            command.setExerciseId(event.getExerciseId());
            command.setAnswerId(event.getAnswerId());
            commandHandler.handle(command);
            return;
        }
    }
}
