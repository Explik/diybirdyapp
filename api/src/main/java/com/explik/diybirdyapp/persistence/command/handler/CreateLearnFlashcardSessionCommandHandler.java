package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.persistence.command.CreateLearnFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseSessionCommandHelper;
import com.explik.diybirdyapp.persistence.vertex.ExerciseTypeVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateLearnFlashcardSessionCommandHandler implements CommandHandler<CreateLearnFlashcardSessionCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateLearnFlashcardSessionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateLearnFlashcardSessionCommand command) {
        // Create the session vertex
        var sessionVertex = ExerciseSessionCommandHelper.createSessionVertex(
                traversalSource,
                command.getId(),
                ExerciseSessionTypes.LEARN_FLASHCARD,
                command.getFlashcardDeckId()
        );

        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();

        // Create the options vertex
        var optionVertex = ExerciseSessionCommandHelper.createOptionsVertex(
                traversalSource,
                ExerciseSessionTypes.LEARN_FLASHCARD,
                command.getTextToSpeechEnabled()
        );

        // Set retype correct answer
        optionVertex.setRetypeCorrectAnswer(command.getRetypeCorrectAnswer() != null ? 
                command.getRetypeCorrectAnswer() : false);

        // Add answer languages
        ExerciseSessionCommandHelper.addAnswerLanguages(
                traversalSource,
                optionVertex,
                command.getAnswerLanguageIds(),
                flashcardDeckVertex
        );

        // Set exercise type flags
        optionVertex.setIncludeReviewExercises(command.getIncludeReviewExercises() != null ? 
                command.getIncludeReviewExercises() : true);
        optionVertex.setIncludeMultipleChoiceExercises(command.getIncludeMultipleChoiceExercises() != null ? 
                command.getIncludeMultipleChoiceExercises() : true);
        optionVertex.setIncludeWritingExercises(command.getIncludeWritingExercises() != null ? 
                command.getIncludeWritingExercises() : true);
        optionVertex.setIncludeListeningExercises(command.getIncludeListeningExercises() != null ? 
                command.getIncludeListeningExercises() : false);
        optionVertex.setIncludePronunciationExercises(command.getIncludePronunciationExercises() != null ? 
                command.getIncludePronunciationExercises() : true);

        // Add exercise types
        if (command.getExerciseTypeIds() != null && !command.getExerciseTypeIds().isEmpty()) {
            for (String exerciseTypeId : command.getExerciseTypeIds()) {
                var exerciseTypeVertex = ExerciseTypeVertex.findById(traversalSource, exerciseTypeId);
                if (exerciseTypeVertex != null) {
                    optionVertex.addExerciseType(exerciseTypeVertex);
                }
            }
        }

        // Link options to session
        sessionVertex.setOptions(optionVertex);
    }
}
