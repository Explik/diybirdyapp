package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.command.CreateLearnFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerLearnFlashcardDeck implements ExerciseSessionManager {
    @Autowired
    private FlashcardDeckExerciseManager exerciseManager;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private CommandHandler<CreateLearnFlashcardSessionCommand> createLearnFlashcardSessionCommandHandler;
    
    @Autowired
    private FlashcardDeckAssociatedContentCreationManager contentCreationManager;
    
    @Autowired
    private FlashcardDeckContentCrawler contentCrawler;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Create session using command
        var sessionId = (options.getId() != null) ? options.getId() : UUID.randomUUID().toString();
        var command = new CreateLearnFlashcardSessionCommand();
        command.setId(sessionId);
        command.setFlashcardDeckId(options.getFlashcardDeckId());
        command.setRetypeCorrectAnswer(false);
        command.setTextToSpeechEnabled(false);
        command.setIncludeReviewExercises(true);
        command.setIncludeMultipleChoiceExercises(true);
        command.setIncludeWritingExercises(true);
        command.setIncludeListeningExercises(true);
        command.setIncludePronunciationExercises(true);
        command.setShuffleFlashcards(false);
        
        var exerciseTypeIds = getInitialExerciseTypes(traversalSource).stream()
                .map(ExerciseTypeVertex::getId)
                .toList();
        command.setExerciseTypeIds(exerciseTypeIds);
        
        createLearnFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        
        // Populate initial active content batch
        populateInitialActiveContent(traversalSource, vertex);
        
        // Dispatch async content creation for flashcard deck
        dispatchContentCreation(traversalSource, vertex);

        // Generate first exercise
        exerciseManager.nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Try to generate next exercise
        var exerciseVertex = exerciseManager.nextExerciseVertex(traversalSource, sessionVertex);
        
        // If no exercise created, try to populate more content and try again
        if (exerciseVertex == null) {
            var stateVertex = getActiveContentState(sessionVertex);
            if (stateVertex != null) {
                // Reset index and populate more content
                stateVertex.setCurrentContentIndex(0);
                populateMoreActiveContent(traversalSource, sessionVertex, stateVertex);
                
                // Try to create exercise again with new content
                exerciseVertex = exerciseManager.nextExerciseVertex(traversalSource, sessionVertex);
            }
        }
        
        // Mark session as completed if still no exercises after populating
        if (exerciseVertex == null) {
            sessionVertex.setCompleted(true);
        }
        
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }
    
    /**
     * Populates initial active content for the session.
     * Creates the activeContentBatch state and adds the first batch of content.
     */
    private void populateInitialActiveContent(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        // Create the activeContentBatch state
        var stateVertex = ExerciseSessionStateVertex.create(traversalSource);
        stateVertex.setType("activeContentBatch");
        stateVertex.setCurrentContentIndex(0);
        sessionVertex.addState(stateVertex);
        
        // Populate first batch of content using the crawler
        var contentList = contentCrawler.collectNextFlashcardContent(flashcardDeck, stateVertex);
        for (AbstractVertex content : contentList) {
            stateVertex.addActiveContent(content);
        }
    }
    
    /**
     * Populates more active content for an existing session.
     * Uses the crawler to get the next batch of flashcard content.
     */
    private void populateMoreActiveContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {
        
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        // Collect content for the next flashcard using the crawler
        var contentList = contentCrawler.collectNextFlashcardContent(flashcardDeck, stateVertex);
        
        // Add all collected content to the active content collection
        for (AbstractVertex content : contentList) {
            stateVertex.addActiveContent(content);
        }
    }
    
    /**
     * Gets the active content batch state vertex for the session.
     */
    private ExerciseSessionStateVertex getActiveContentState(ExerciseSessionVertex sessionVertex) {
        var stateVertices = sessionVertex.getStatesWithType("activeContentBatch");
        return stateVertices.isEmpty() ? null : stateVertices.get(0);
    }
    
    /**
     * Dispatches async content creation for all flashcards and their text content in the session's deck.
     * This includes TTS generation for text content that has a language with TTS configuration.
     * Created pronunciation vertices are automatically added to the active content batch.
     */
    private void dispatchContentCreation(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        var flashcards = flashcardDeck.getFlashcards();
        var contentVertices = flashcards.stream()
                .flatMap(flashcard -> {
                    var contents = new java.util.ArrayList<ContentVertex>();
                    if (flashcard.getLeftContent() != null) contents.add(flashcard.getLeftContent());
                    if (flashcard.getRightContent() != null) contents.add(flashcard.getRightContent());
                    return contents.stream();
                })
                .toList();
        
        // Get the active content state to add created pronunciations
        var stateVertex = getActiveContentState(sessionVertex);
        
        // Dispatch content creation with callback to add created vertices to active content
        contentCreationManager.dispatchContentCreation(contentVertices, pronunciationVertex -> {
            if (stateVertex != null && pronunciationVertex != null) {
                stateVertex.addActiveContent(pronunciationVertex);
            }
        });
    }

    private List<ExerciseTypeVertex> getInitialExerciseTypes(GraphTraversalSource traversalSource) {
        // This list should not contain any non-flashcard-based exercises as it breaks the next-exercise algorithm
        return List.of(
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.REVIEW_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.SELECT_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.WRITE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.PRONOUNCE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.LISTEN_AND_SELECT),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.LISTEN_AND_WRITE)
        );
    }
}
