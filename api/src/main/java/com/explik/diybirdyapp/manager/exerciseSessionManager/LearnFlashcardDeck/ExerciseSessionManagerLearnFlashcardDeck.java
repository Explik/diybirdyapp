package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.manager.contentCrawler.FailedExerciseErrorScoreEvaluator;
import com.explik.diybirdyapp.manager.contentCrawler.PrioritizedFlashcardContentCrawler;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.manager.contentCrawler.FlashcardDeckContentCrawler;
import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.command.CreateLearnFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerLearnFlashcardDeck implements ExerciseSessionManager {
    
    // Batch configuration constants
    private static final int BATCH_SIZE = 20; // Number of exercises per batch
    
    @Autowired
    private FlashcardDeckExerciseManager exerciseManager;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private CommandHandler<CreateLearnFlashcardSessionCommand> createLearnFlashcardSessionCommandHandler;
    
    @Autowired
    private FlashcardDeckAssociatedContentCreationManager contentCreationManager;
    
    @Autowired
    private FlashcardDeckContentCrawler deckContentCrawler;
    
    @Autowired
    private PrioritizedFlashcardContentCrawler prioritizedContentCrawler;

    @Autowired
    private FailedExerciseErrorScoreEvaluator failedExerciseErrorScoreEvaluator;

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
        
        createLearnFlashcardSessionCommandHandler.handle(command);

        // Load the created session
        var vertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        
        // Populate availableContent cache for multiple choice options
        // TODO move into crawler as caching, because it gets slow for larger decks
        populateAvailableContent(traversalSource, vertex);
        
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

        var stateVertex = getActiveContentState(sessionVertex);

        // Refresh hard-content scores before selecting any new batch content.
        if (stateVertex != null && sessionVertex.getFlashcardDeck() != null) {
            failedExerciseErrorScoreEvaluator.evaluate(sessionVertex.getFlashcardDeck(), stateVertex);
        }
        
        // Check if current batch has reached the batch size limit
        if (stateVertex != null && getBatchExerciseCount(stateVertex) >= BATCH_SIZE) {
            // Start a new batch: clear content and reset counters
            startNewBatch(traversalSource, sessionVertex, stateVertex);
        }
        
        // Try to generate next exercise
        var exerciseVertex = exerciseManager.nextExerciseVertex(traversalSource, sessionVertex);
        
        // If no exercise created, try to populate more content and try again
        if (exerciseVertex == null) {
            if (stateVertex != null) {
                int previousActiveContentSize = stateVertex.getActiveContent().size();
                populateMoreActiveContent(traversalSource, sessionVertex, stateVertex);

                int updatedActiveContentSize = stateVertex.getActiveContent().size();
                if (updatedActiveContentSize > previousActiveContentSize) {
                    // New content was appended, so restart round traversal for the expanded batch.
                    stateVertex.setCurrentRound(0);
                    stateVertex.setCurrentContentIndex(0);
                }
                
                // Try to create exercise again with new content
                exerciseVertex = exerciseManager.nextExerciseVertex(traversalSource, sessionVertex);
            }
        }
        
        // If exercise was created, increment batch counter
        if (exerciseVertex != null && stateVertex != null) {
            incrementBatchExerciseCount(stateVertex);
        }
        
        // Mark session as completed if still no exercises after populating
        if (exerciseVertex == null) {
            sessionVertex.setCompleted(true);
        }
        
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }
    
    @Override
    public void updateOptions(GraphTraversalSource traversalSource, String sessionId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null) {
            return; // Session not found, nothing to do
        }
        
        var stateVertex = getActiveContentState(sessionVertex);
        if (stateVertex != null) {
            // Regenerate the batch with new options
            startNewBatch(traversalSource, sessionVertex, stateVertex);
        } else {
            // Recover gracefully if state is missing, then bootstrap a fresh batch.
            populateInitialActiveContent(traversalSource, sessionVertex);
        }
        
        // Refresh available content for multiple choice options
        var availableContentState = getAvailableContentState(sessionVertex);
        if (availableContentState != null) {
            availableContentState.clearAvailableContent();
            populateAvailableContent(traversalSource, sessionVertex);
        }
        
        // Dispatch new content creation with updated target language  
        dispatchContentCreation(traversalSource, sessionVertex);

        // Options updates must immediately produce a new current exercise.
        sessionVertex.setCompleted(false);
        var sessionModel = new ExerciseSessionDto();
        sessionModel.setId(sessionId);
        var context = ExerciseCreationContext.createDefault(sessionModel);
        nextExercise(traversalSource, context);
    }
    
    /**
     * Populates availableContent for the session.
     * This content is used to generate multiple choice options in exercises.
     * Creates a separate state vertex to cache all deck content.
     */
    private void populateAvailableContent(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        // Create or get the availableContent state
        var stateVertex = getOrCreateAvailableContentState(traversalSource, sessionVertex);
        
        // Check if already populated
        if (!stateVertex.getAvailableContent().isEmpty()) {
            return;
        }
        
        // Collect all deck content using FlashcardDeckContentCrawler
        var allContent = deckContentCrawler.crawl(flashcardDeck).toList();
        stateVertex.setAvailableContent(allContent);
    }
    
    /**
     * Gets or creates the availableContent state vertex.
     */
    private ExerciseSessionStateVertex getOrCreateAvailableContentState(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        
        var stateVertices = sessionVertex.getStatesWithType("availableContent");
        
        if (stateVertices.isEmpty()) {
            var stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("availableContent");
            sessionVertex.addState(stateVertex);
            return stateVertex;
        }
        
        return stateVertices.get(0);
    }
    
    /**
     * Populates initial active content for the session.
     * Creates the activeContentBatch state and adds the first batch of content.
     * Uses prioritized crawler selection: failed content > insufficiently practiced > new content.
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
        stateVertex.setCurrentRound(0);
        sessionVertex.addState(stateVertex);
        
        // Populate first batch of content using prioritized crawler selection.
        var params = new FlashcardDeckSessionParams(flashcardDeck, stateVertex);
        var contentList = prioritizedContentCrawler.crawl(params).toList();
        for (AbstractVertex content : contentList) {
            stateVertex.addActiveContent(content);
            if (content instanceof FlashcardVertex) {
                stateVertex.addPracticedContent(content);
            }
        }
    }

    /**
     * Populates more active content for an existing session.
     * Uses prioritized crawler selection: failed content > insufficiently practiced > new content.
     */
    private void populateMoreActiveContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {
        
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return;
        }
        
        // Collect content using prioritized crawler selection.
        var params = new FlashcardDeckSessionParams(flashcardDeck, stateVertex);
        var contentList = prioritizedContentCrawler.crawl(params).toList();

        // Add all collected content to the active content collection
        for (AbstractVertex content : contentList) {
            stateVertex.addActiveContent(content);
            if (content instanceof FlashcardVertex) {
                stateVertex.addPracticedContent(content);
            }
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
     * Gets the available content state vertex for the session.
     */
    private ExerciseSessionStateVertex getAvailableContentState(ExerciseSessionVertex sessionVertex) {
        var stateVertices = sessionVertex.getStatesWithType("availableContent");
        return stateVertices.isEmpty() ? null : stateVertices.get(0);
    }
    
    /**
     * Gets the number of exercises created in the current batch.
     */
    private int getBatchExerciseCount(ExerciseSessionStateVertex stateVertex) {
        Integer count = stateVertex.getPropertyValue("batchExerciseCount", 0);
        return count != null ? count : 0;
    }
    
    /**
     * Increments the batch exercise counter.
     */
    private void incrementBatchExerciseCount(ExerciseSessionStateVertex stateVertex) {
        int currentCount = getBatchExerciseCount(stateVertex);
        stateVertex.setPropertyValue("batchExerciseCount", currentCount + 1);
    }
    
    /**
     * Starts a new batch by clearing active content and resetting counters.
     * Then populates new content from the crawlers.
     */
    private void startNewBatch(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {
        
        // Clear all active content
        stateVertex.clearActiveContent();
        
        // Reset counters
        stateVertex.setCurrentContentIndex(0);
        stateVertex.setCurrentRound(0);
        stateVertex.setPropertyValue("batchExerciseCount", 0);
        
        // Populate new content for the new batch
            var flashcardDeck = sessionVertex.getFlashcardDeck();
            if (flashcardDeck != null) {
                var params = new FlashcardDeckSessionParams(flashcardDeck, stateVertex);
                var contentList = prioritizedContentCrawler.crawl(params).toList();
                for (AbstractVertex content : contentList) {
                    stateVertex.addActiveContent(content);
                    if (content instanceof FlashcardVertex) {
                        stateVertex.addPracticedContent(content);
                    }
                }
            }
    }
    
    /**
     * Dispatches async content creation for all flashcards and their text content in the session's deck.
     * This includes TTS generation for text content that has a language with TTS configuration.
     * Created pronunciation vertices are automatically added to the active content batch.
     */
    private void dispatchContentCreation(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var activeContentState = getActiveContentState(sessionVertex);
        if (activeContentState == null)
            return; // Cannot add content without an active content state

        var activeFlashcards = activeContentState.getActiveContent().stream()
                .filter(vertex -> vertex instanceof FlashcardVertex)
                .map(vertex -> (FlashcardVertex) vertex)
                .toList();

        var contentVertices = activeFlashcards.stream()
                .flatMap(flashcard -> {
                    var contents = new java.util.ArrayList<ContentVertex>();
                    if (flashcard.getLeftContent() != null) contents.add(flashcard.getLeftContent());
                    if (flashcard.getRightContent() != null) contents.add(flashcard.getRightContent());
                    return contents.stream();
                })
                .toList();

        // Get target language from session options
        var options = sessionVertex.getOptions();
        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        var targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;
        
        // Dispatch content creation with callback to add created vertices to active content
        contentCreationManager.dispatchContentCreation(contentVertices, targetLanguageId, pronunciationVertex -> {
            if (pronunciationVertex != null) {
                activeContentState.addActiveContent(pronunciationVertex);
            }
        });
    }
}
