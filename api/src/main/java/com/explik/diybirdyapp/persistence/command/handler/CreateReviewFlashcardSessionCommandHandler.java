package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.persistence.command.CreateReviewFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseSessionCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateReviewFlashcardSessionCommandHandler implements CommandHandler<CreateReviewFlashcardSessionCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateReviewFlashcardSessionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateReviewFlashcardSessionCommand command) {
        // Create the session vertex
        var sessionVertex = ExerciseSessionCommandHelper.createSessionVertex(
                traversalSource,
                command.getId(),
                ExerciseSessionTypes.REVIEW_FLASHCARD,
                command.getFlashcardDeckId()
        );

        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();

        // Create the options vertex
        var optionVertex = ExerciseSessionCommandHelper.createOptionsVertex(
                traversalSource,
                ExerciseSessionTypes.REVIEW_FLASHCARD,
                command.getTextToSpeechEnabled()
        );

        // Set initial flashcard language
        ExerciseSessionCommandHelper.setInitialFlashcardLanguage(
                traversalSource,
                optionVertex,
                command.getInitialFlashcardLanguageId(),
                flashcardDeckVertex
        );

        // Set algorithm
        optionVertex.setAlgorithm(command.getAlgorithm() != null ? 
                command.getAlgorithm() : "SuperMemo2");

        // Link options to session
        sessionVertex.setOptions(optionVertex);
    }
}
