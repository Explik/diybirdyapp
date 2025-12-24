package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.persistence.command.CreateWriteFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseSessionCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWriteFlashcardSessionCommandHandler implements CommandHandler<CreateWriteFlashcardSessionCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateWriteFlashcardSessionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateWriteFlashcardSessionCommand command) {
        // Create the session vertex
        var sessionVertex = ExerciseSessionCommandHelper.createSessionVertex(
                traversalSource,
                command.getId(),
                ExerciseSessionTypes.WRITE_FLASHCARD,
                command.getFlashcardDeckId()
        );

        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();

        // Create the options vertex
        var optionVertex = ExerciseSessionCommandHelper.createOptionsVertex(
                traversalSource,
                ExerciseSessionTypes.WRITE_FLASHCARD,
                command.getTextToSpeechEnabled()
        );

        // Add answer languages
        ExerciseSessionCommandHelper.addAnswerLanguages(
                traversalSource,
                optionVertex,
                command.getAnswerLanguageIds(),
                flashcardDeckVertex
        );

        // Set retype correct answer
        optionVertex.setRetypeCorrectAnswer(command.getRetypeCorrectAnswer() != null ? 
                command.getRetypeCorrectAnswer() : false);

        // Link options to session
        sessionVertex.setOptions(optionVertex);
    }
}
