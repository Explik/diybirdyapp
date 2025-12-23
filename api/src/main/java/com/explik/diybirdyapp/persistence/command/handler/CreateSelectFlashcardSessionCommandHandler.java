package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.persistence.command.CreateSelectFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseSessionCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSelectFlashcardSessionCommandHandler implements CommandHandler<CreateSelectFlashcardSessionCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateSelectFlashcardSessionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateSelectFlashcardSessionCommand command) {
        // Create the session vertex
        var sessionVertex = ExerciseSessionCommandHelper.createSessionVertex(
                traversalSource,
                command.getId(),
                ExerciseSessionTypes.SELECT_FLASHCARD_DECK,
                command.getFlashcardDeckId()
        );

        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();

        // Create the options vertex
        var optionVertex = ExerciseSessionCommandHelper.createOptionsVertex(
                traversalSource,
                ExerciseSessionTypes.SELECT_FLASHCARD_DECK,
                command.getTextToSpeechEnabled()
        );

        // Set initial flashcard language
        ExerciseSessionCommandHelper.setInitialFlashcardLanguage(
                traversalSource,
                optionVertex,
                command.getInitialFlashcardLanguageId(),
                flashcardDeckVertex
        );

        // Link options to session
        sessionVertex.setOptions(optionVertex);
    }
}
