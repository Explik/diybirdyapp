package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseCreationManager.WriteFlashcardExerciseCreationManager;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.command.CreateWriteFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component(ExerciseSessionTypes.WRITE_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionsManagerWriteFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private WriteFlashcardExerciseCreationManager writeFlashcardExerciseCreationManager;

    @Autowired
    private CommandHandler<CreateWriteFlashcardSessionCommand> createWriteFlashcardSessionCommandHandler;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Create session using command
        var sessionId = (options.getId() != null) ? options.getId() : UUID.randomUUID().toString();
        var command = new CreateWriteFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(options.getFlashcardDeckId());
        command.setTextToSpeechEnabled(false);
        command.setRetypeCorrectAnswer(false);
        command.setShuffleFlashcards(false);
        
        createWriteFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    @SuppressWarnings("unused")
    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // Check if shuffle is enabled
        var options = sessionVertex.getOptions();
        boolean shuffleFlashcards = options != null && options.getShuffleFlashcards();
        
        FlashcardVertex flashcardVertex;
        
        if (shuffleFlashcards) {
            // Get all non-exercised flashcards and pick randomly
            var nonExercisedFlashcards = FlashcardVertex.findNonExercised(
                    traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
            
            if (!nonExercisedFlashcards.isEmpty()) {
                Random random = new Random();
                flashcardVertex = nonExercisedFlashcards.get(random.nextInt(nonExercisedFlashcards.size()));
            } else {
                flashcardVertex = null;
            }
        } else {
            // Get first flashcard in order
            flashcardVertex = FlashcardVertex.findFirstNonExercised(
                    traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        }

        if (flashcardVertex != null) {
            var answerLanguageId = (options != null && !options.getAnswerLanguages().isEmpty())
                    ? options.getAnswerLanguages().get(0).getId()
                    : null;
            var flashcardSide = FlashcardVertex.findPromptSideForAnswerLanguageId(flashcardVertex, answerLanguageId);
            var context = ExerciseCreationContext.createForFlashcard(
                    sessionVertex,
                    flashcardVertex,
                    flashcardSide,
                    ExerciseTypes.WRITE_FLASHCARD);
            
            return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
        }
        else {
            sessionVertex.setCompleted(true);
        }

        return null;
    }
}
