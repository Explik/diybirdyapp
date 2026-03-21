package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.manager.contentCrawler.FailedExerciseContentCrawler;
import com.explik.diybirdyapp.manager.contentCrawler.FailedExerciseErrorScoreEvaluator;
import com.explik.diybirdyapp.manager.contentCrawler.InsufficientlyExercisedContentCrawler;
import com.explik.diybirdyapp.manager.contentCrawler.UnpracticedFlashcardContentCrawler;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.command.CreateLearnFlashcardSessionCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionManagerLearnFlashcardDeck implements ExerciseSessionManager {
    private static final int BATCH_SIZE = 20; // Number of exercises per batch
    private static final int INITIAL_NEW_CONTENT_COUNT = 3;
    private static final int INITIAL_INSUFFICIENT_CONTENT_COUNT = 3;
    private static final int INITIAL_DIFFICULT_CONTENT_COUNT = 4;
    private static final int SUBSEQUENT_INSUFFICIENT_CONTENT_COUNT = 3;
    private static final int SUBSEQUENT_DIFFICULT_CONTENT_COUNT = 4;
    private static final int SUBSEQUENT_FALLBACK_NEW_CONTENT_COUNT = 3;
    
    @Autowired
    private FlashcardDeckExerciseManager exerciseManager;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private CommandHandler<CreateLearnFlashcardSessionCommand> createLearnFlashcardSessionCommandHandler;
    
    @Autowired
    private FlashcardDeckAssociatedContentCreationManager contentCreationManager;
    
    @Autowired
    private UnpracticedFlashcardContentCrawler unpracticedFlashcardContentCrawler;

    @Autowired
    private InsufficientlyExercisedContentCrawler insufficientlyExercisedContentCrawler;

    @Autowired
    private FailedExerciseContentCrawler failedExerciseContentCrawler;

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

        var exerciseVertex = generateBatch(traversalSource, vertex);
        vertex.setCompleted(exerciseVertex == null);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");
        assert sessionVertex.getFlashcardDeck() != null : "Session should have an associated flashcard deck";

        var stateVertex = createOrGetActiveContentState(traversalSource, sessionVertex);
        assert stateVertex != null : "Active content state should have been created if it did not exist";

        ExerciseVertex exerciseVertex;
        if (getBatchExerciseCount(stateVertex) >= BATCH_SIZE) {
            exerciseVertex = generateBatch(traversalSource, sessionVertex);
        } else {
            exerciseVertex = generateBatchExercise(traversalSource, sessionVertex, stateVertex);
        }

        sessionVertex.setCompleted(exerciseVertex == null);
        
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }
    
    @Override
    public void updateOptions(GraphTraversalSource traversalSource, String sessionId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null) {
            return; // Session not found, nothing to do
        }

        var exerciseVertex = generateBatch(traversalSource, sessionVertex);
        sessionVertex.setCompleted(exerciseVertex == null);
        sessionVertex.reload();
    }

    /**
     * Regenerates the active batch and immediately creates the first exercise.
     * This method can be called on a fresh session and will create required state.
     */
    private ExerciseVertex generateBatch(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {

        var stateVertex = createOrGetActiveContentState(traversalSource, sessionVertex);
        assert stateVertex != null : "Active content state vertex should have been created if it did not exist";

        stateVertex.clearActiveContent();
        stateVertex.setCurrentContentIndex(0);
        stateVertex.setPropertyValue("batchExerciseCount", 0);

        fetchInitialContent(sessionVertex, stateVertex);
        dispatchContentCreation(sessionVertex, stateVertex);

        return generateBatchExercise(traversalSource, sessionVertex, stateVertex);
    }

    /**
     * Creates one exercise from the current batch and retries after fetching more content.
     * This method can be called on a fresh session and will create required state.
     */
    private ExerciseVertex generateBatchExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {
        assert traversalSource != null : "Traversal source should not be null";
        assert sessionVertex != null : "Session vertex should not be null";
        assert stateVertex != null : "State vertex should not be null";

        var exerciseVertex = nextExerciseVertex(traversalSource, sessionVertex, stateVertex);
        if (exerciseVertex == null) {
            fetchAdditionalContent(sessionVertex, stateVertex);
            exerciseVertex = nextExerciseVertex(traversalSource, sessionVertex, stateVertex);
        }
        if (exerciseVertex == null) {
            // Fetch new content until exercise can be create or no more content is available
            for (int i = 0; i < 10; i++) {
                var params = new FlashcardDeckSessionParams(sessionVertex.getFlashcardDeck(), stateVertex);
                var count = appendContent(stateVertex, unpracticedFlashcardContentCrawler.crawl(params), SUBSEQUENT_FALLBACK_NEW_CONTENT_COUNT);
                if (count == 0)
                    break;

                exerciseVertex = nextExerciseVertex(traversalSource, sessionVertex, stateVertex);
                if (exerciseVertex != null)
                    break;
            }
        }

        if (exerciseVertex != null) {
            incrementBatchExerciseCount(stateVertex);
        }
        else {
            // No exercise could be created even after fetching additional content, mark session as completed
            sessionVertex.setCompleted(true);
        }

        return exerciseVertex;
    }

    /**
     * Creates the next exercise from the current active batch.
     * Traverses active content linearly; exercise creation is delegated.
     */
    private ExerciseVertex nextExerciseVertex(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {

        if (stateVertex == null) {
            return null;
        }

        var activeContent = stateVertex.getActiveContent();
        if (activeContent.isEmpty()) {
            return null;
        }

        int currentIndex = stateVertex.getCurrentContentIndex();
        while (currentIndex < activeContent.size()) {
            var content = activeContent.get(currentIndex);
            var exerciseVertex = exerciseManager.createExerciseForContent(
                    traversalSource,
                    sessionVertex,
                    stateVertex,
                    content);

            stateVertex.setCurrentContentIndex(currentIndex + 1);

            if (exerciseVertex != null) {
                var contentId = getContentId(content);
                if (contentId != null) {
                    stateVertex.incrementExerciseCountForContent(contentId);
                }

                return exerciseVertex;
            }

            currentIndex = stateVertex.getCurrentContentIndex();
        }

        return null;
    }

    private String getContentId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        }

        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }

        if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }

        return null;
    }
    
    /**
     * Appends initial batch content: first new, then insufficiently practiced, then difficult items.
     */
    private void fetchInitialContent(
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {

        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null)
            return;

        failedExerciseErrorScoreEvaluator.evaluate(flashcardDeck, stateVertex);

        var params = new FlashcardDeckSessionParams(flashcardDeck, stateVertex);
        appendContent(stateVertex, unpracticedFlashcardContentCrawler.crawl(params), INITIAL_NEW_CONTENT_COUNT);
        appendContent(stateVertex, insufficientlyExercisedContentCrawler.crawl(params), INITIAL_INSUFFICIENT_CONTENT_COUNT);
        appendContent(stateVertex, failedExerciseContentCrawler.crawl(params), INITIAL_DIFFICULT_CONTENT_COUNT);
    }

    /**
     * Appends subsequent content pulls: first insufficiently practiced, then difficult items;
     */
    private void fetchAdditionalContent(
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex) {
        assert sessionVertex != null : "Session vertex should not be null";
        assert stateVertex != null : "State vertex should not be null";

        var flashcardDeck = sessionVertex.getFlashcardDeck();
        assert flashcardDeck != null : "Session should have an associated flashcard deck";

        failedExerciseErrorScoreEvaluator.evaluate(flashcardDeck, stateVertex);

        var params = new FlashcardDeckSessionParams(flashcardDeck, stateVertex);
        appendContent(stateVertex, insufficientlyExercisedContentCrawler.crawl(params), SUBSEQUENT_INSUFFICIENT_CONTENT_COUNT);
        appendContent(stateVertex, failedExerciseContentCrawler.crawl(params), SUBSEQUENT_DIFFICULT_CONTENT_COUNT);
    }

    private int appendContent(
            ExerciseSessionStateVertex stateVertex,
            Stream<AbstractVertex> contentStream,
            int maxCount) {
        assert stateVertex != null : "State vertex should not be null";
        assert contentStream != null : "Content stream should not be null";
        assert maxCount > 0 : "Max count should be greater than 0";

        try (var limitedStream = contentStream.limit(maxCount)) {
            var contentList = limitedStream.toList();

            for (AbstractVertex content : contentList) {
                stateVertex.addActiveContent(content);

                if (content instanceof FlashcardVertex) {
                    stateVertex.addPracticedContent(content);
                }
            }
            return contentList.size();
        }
    }

    /**
     * Gets the active content batch state vertex for the session.
     */
    private ExerciseSessionStateVertex createOrGetActiveContentState(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        var stateVertices = sessionVertex.getStatesWithType("activeContentBatch");
        var stateVertex = stateVertices.isEmpty() ? null : stateVertices.get(0);

        if (stateVertex == null) {
            stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("activeContentBatch");
            stateVertex.setCurrentContentIndex(0);
            stateVertex.setPropertyValue("batchExerciseCount", 0);
            sessionVertex.addState(stateVertex);
            return stateVertex;
        }

        Integer batchExerciseCount = stateVertex.getPropertyValue("batchExerciseCount");
        if (batchExerciseCount == null) {
            stateVertex.setPropertyValue("batchExerciseCount", 0);
        }

        return stateVertex;
    }
    
    /**
     * Gets the number of exercises created in the current batch.
     */
    private int getBatchExerciseCount(ExerciseSessionStateVertex stateVertex) {
        assert stateVertex != null : "State vertex should not be null";

        Integer count = stateVertex.getPropertyValue("batchExerciseCount", 0);
        return count != null ? count : 0;
    }
    
    /**
     * Increments the batch exercise counter.
     */
    private void incrementBatchExerciseCount(ExerciseSessionStateVertex stateVertex) {
        assert stateVertex != null : "State vertex should not be null";

        int currentCount = getBatchExerciseCount(stateVertex);
        stateVertex.setPropertyValue("batchExerciseCount", currentCount + 1);
    }
    
    /**
     * Dispatches async content creation for all flashcards and their text content in the session's deck.
     * This includes TTS generation for text content that has a language with TTS configuration.
     * Created pronunciation vertices are automatically added to the active content batch.
     */
    private void dispatchContentCreation(
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex activeContentState) {
        assert sessionVertex != null : "Session vertex should not be null";
        assert activeContentState != null : "Active content state vertex should not be null";

        var activeFlashcards = activeContentState.getActiveContent().stream()
                .filter(vertex -> vertex instanceof FlashcardVertex)
                .map(vertex -> (FlashcardVertex) vertex)
                .toList();

        var contentVertices = activeFlashcards.stream()
                .flatMap(flashcard -> {
                    var contents = new ArrayList<ContentVertex>();
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
