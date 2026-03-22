package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.contentCrawler.FlashcardDeckContentCrawler;
import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseCreationManager.SelectFlashcardExerciseCreationManager;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.command.CreateSelectFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component(ExerciseSessionTypes.SELECT_FLASHCARD_DECK + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerSelectFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private SelectFlashcardExerciseCreationManager selectFlashcardExerciseCreationManager;

    @Autowired
    private CommandHandler<CreateSelectFlashcardSessionCommand> createSelectFlashcardSessionCommandHandler;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;
    
    @Autowired
    private FlashcardDeckContentCrawler deckContentCrawler;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Create session using command
        var sessionId = (options.getId() != null) ? options.getId() : UUID.randomUUID().toString();
        var command = new CreateSelectFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(options.getFlashcardDeckId());
        command.setTextToSpeechEnabled(false);
        command.setInitiallyHideOptions(false);
        command.setShuffleFlashcards(false);
        
        createSelectFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        
        // Populate activeContent with all flashcards
        populateActiveContent(traversalSource, vertex);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + sessionId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        // Check if shuffle is enabled
        var options = sessionVertex.getOptions();
        boolean shuffleFlashcards = options != null && options.getShuffleFlashcards();
        
        FlashcardVertex flashcardVertex;
        
        if (shuffleFlashcards) {
            // Get all non-exercised flashcards and pick randomly
            var nonExercisedFlashcards = FlashcardVertex.findNonExercised(
                    traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
            
            if (!nonExercisedFlashcards.isEmpty()) {
                Random random = new Random();
                flashcardVertex = nonExercisedFlashcards.get(random.nextInt(nonExercisedFlashcards.size()));
            } else {
                flashcardVertex = null;
            }
        } else {
            // Get first flashcard in order
            flashcardVertex = FlashcardVertex.findFirstNonExercised(
                    traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
        }

        if (flashcardVertex != null) {
            var context = ExerciseCreationContext.createForFlashcard(
                    sessionVertex,
                    flashcardVertex,
                    "front",
                    ExerciseTypes.SELECT_FLASHCARD);

            var flashcardDeck = sessionVertex.getFlashcardDeck();
            if (flashcardDeck != null) {
                context.setContentStream(deckContentCrawler.crawl(flashcardDeck));
            }
            
            return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
        } else {
            // If no flashcards are found, the session is complete
            sessionVertex.setCompleted(true);
        }

        return null;
    }
    
    /**
     * Populates activeContent with all flashcards from the deck.
     * For SELECT_FLASHCARD exercises, all flashcards are available as active content.
     */
    private void populateActiveContent(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        // Create or get the activeContent state
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        
        // Check if already populated
        if (!stateVertex.getActiveContent().isEmpty()) {
            return;
        }
        
        // Get all flashcards from the deck
        var flashcards = flashcardDeck.getFlashcards();
        
        // Add all flashcards to active content
        for (FlashcardVertex flashcard : flashcards) {
            stateVertex.addActiveContent(flashcard);
        }
    }
    
    /**
     * Gets or creates the activeContent state vertex.
     */
    private ExerciseSessionStateVertex getOrCreateActiveContentState(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        
        var stateVertices = sessionVertex.getStatesWithType("activeContent");
        
        if (stateVertices.isEmpty()) {
            var stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("activeContent");
            sessionVertex.addState(stateVertex);
            return stateVertex;
        }
        
        return stateVertices.get(0);
    }
    
}
